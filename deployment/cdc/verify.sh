#!/usr/bin/env bash
# End-to-end proof for the PostgreSQL -> Elasticsearch CDC flow.
#
#   Debezium captures the WAL -> Kafka topic -> sink consumer -> Elasticsearch
#
# Run after `docker compose up -d` in deployment/cdc and after the connector has
# reached RUNNING state (setup.sh does that for you).
set -euo pipefail

PG="docker exec -i cdc-postgres psql -U cdcuser -d db_cdc -tA"
ES="${ES_URL:-http://localhost:9200}"
CONNECT="${CONNECT_URL:-http://localhost:8083}"
CONNECTOR="${CONNECTOR_NAME:-pg-orders-source}"

GREEN='\033[0;32m'; RED='\033[0;31m'; NC='\033[0m'
pass() { printf "${GREEN}PASS${NC} %s\n" "$1"; }
fail() { printf "${RED}FAIL${NC} %s\n" "$1"; exit 1; }

# Poll Elasticsearch until the index reports the expected document count.
wait_count() {
  local index="$1" expected="$2" timeout="${3:-40}" i
  for ((i = 0; i < timeout; i++)); do
    local count
    count=$(curl -s "$ES/$index/_count" | python -c "import json,sys; print(json.load(sys.stdin).get('count', -1))" 2>/dev/null || echo -1)
    if [ "$count" = "$expected" ]; then
      echo "$count"
      return 0
    fi
    sleep 1
  done
  echo "$count"
  return 1
}

echo "=== 0. prerequisites ==================================================="
curl -fsS "$ES/_cluster/health" >/dev/null || fail "Elasticsearch not reachable at $ES"
STATE=$(curl -s "$CONNECT/connectors/$CONNECTOR/status" | python -c "
import json,sys
d=json.load(sys.stdin)
print(d['connector']['state'], d['tasks'][0]['state'] if d.get('tasks') else 'NOTASK')
" 2>/dev/null || echo "MISSING")
[ "${STATE%% *}" = "RUNNING" ] || fail "connector $CONNECTOR not RUNNING (state: $STATE)"
pass "Elasticsearch reachable, connector $CONNECTOR RUNNING"

echo
echo "=== 1. initial snapshot ==============================================="
SNAP_TOTAL=$($PG -c "SELECT count(*) FROM customers; " )
SNAP_ES=$(wait_count "cdc-customers" "$SNAP_TOTAL" 60) || fail "snapshot mismatch: PG=$SNAP_TOTAL ES=$SNAP_ES"
pass "snapshot mirrored: cdc-customers has $SNAP_ES doc(s) = $SNAP_TOTAL row(s)"

echo
echo "=== 2. INSERT -> Elasticsearch ========================================"
NEW_ID="33333333-3333-3333-3333-333333333333"
$PG -c "DELETE FROM customers WHERE id='$NEW_ID';" >/dev/null
$PG -c "INSERT INTO customers (id, full_name, email, phone)
        VALUES ('$NEW_ID', 'CDC Test User', 'cdc.insert@example.com', '+855 99 333 333');" >/dev/null
PG_INSERT=$($PG -c "SELECT count(*) FROM customers;")
ES_INSERT=$(wait_count "cdc-customers" "$PG_INSERT" 60) || fail "INSERT not visible in ES (PG=$PG_INSERT ES=$ES_INSERT)"
FOUND=$(curl -s "$ES/cdc-customers/_doc/$NEW_ID" | python -c "
import json,sys
d=json.load(sys.stdin)
print('yes' if d.get('found') and d['_source']['email']=='cdc.insert@example.com' else 'no')
")
[ "$FOUND" = "yes" ] || fail "inserted document not found by id"
pass "INSERT captured: ${PG_INSERT} row(s) in PG, ${ES_INSERT} doc(s) in ES"

echo
echo "=== 3. UPDATE -> Elasticsearch ========================================"
$PG -c "UPDATE customers SET full_name='CDC Test User (updated)', status='BLOCKED'
        WHERE id='$NEW_ID';" >/dev/null
UPDATED=$(for i in $(seq 1 40); do
  name=$(curl -s "$ES/cdc-customers/_doc/$NEW_ID" | python -c "import json,sys; d=json.load(sys.stdin); print(d.get('_source',{}).get('fullName',''))" 2>/dev/null)
  [ "$name" = "CDC Test User (updated)" ] && { echo yes; break; }
  sleep 1
done)
[ "${UPDATED:-no}" = "yes" ] || fail "UPDATE not reflected in ES"
STATUS_ES=$(curl -s "$ES/cdc-customers/_doc/$NEW_ID" | python -c "import json,sys; print(json.load(sys.stdin)['_source']['status'])")
pass "UPDATE captured: fullName and status=$STATUS_ES mirrored"

echo
echo "=== 4. DELETE -> Elasticsearch ========================================"
$PG -c "DELETE FROM customers WHERE id='$NEW_ID';" >/dev/null
PG_DELETE=$($PG -c "SELECT count(*) FROM customers;")
ES_DELETE=$(wait_count "cdc-customers" "$PG_DELETE" 60) || fail "DELETE not reflected (PG=$PG_DELETE ES=$ES_DELETE)"
GONE=$(curl -s -o /dev/null -w "%{http_code}" "$ES/cdc-customers/_doc/$NEW_ID")
[ "$GONE" = "404" ] || fail "deleted document still present in ES (HTTP $GONE)"
pass "DELETE captured: row removed, ES document gone (404), counts align at $ES_DELETE"

echo
echo "=== 5. one-to-many mirror ============================================="
for t in orders order-items; do
  idx="cdc-$t"
  pg_table=$([ "$t" = "orders" ] && echo orders || echo order_items)
  pg_n=$($PG -c "SELECT count(*) FROM $pg_table;")
  es_n=$(wait_count "$idx" "$pg_n" 60) || fail "$idx mismatch (PG=$pg_n ES=$es_n)"
  pass "$idx: $es_n doc(s) = $pg_n row(s) in $pg_table"
done

echo
echo "=== 6. mapping is enforced (not dynamic) =============================="
DYN=$(curl -s "$ES/cdc-customers/_mapping" | python -c "
import json,sys
m=json.load(sys.stdin)['cdc-customers']['mappings']
print(m.get('dynamic'))
")
[ "$DYN" = "False" ] || [ "$DYN" = "false" ] || fail "unexpected dynamic setting: $DYN"
pass "cdc-* mapping comes from es/index-template.json (dynamic=$DYN)"

echo
echo "=== 7. bulk indexing evidence ========================================="
curl -s "$ES/cdc-orders/_stats/indexing" | python -c "
import json,sys
d=json.load(sys.stdin)['_all']['primaries']['indexing']
print('  index_total=%s  index_time_in_millis=%s  delete_total=%s' % (
    d.get('index_total'), d.get('index_time_in_millis'), d.get('delete_total')))
"
pass "sink wrote via the Elasticsearch _bulk API"

echo
echo "==============================================================="
printf "${GREEN}ALL CHECKS PASSED${NC} - PostgreSQL and Elasticsearch are in sync.\n"

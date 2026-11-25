#!/usr/bin/env bash
# One-shot setup for the PostgreSQL -> Elasticsearch CDC flow.
#
#   ./setup.sh          start the stack, apply the index template, start CDC
#   ./setup.sh --reset  also delete the connector/slot first (fresh snapshot)
set -euo pipefail

cd "$(dirname "$0")"

# The compose file joins ite-net as an external network, so it must exist first.
docker network inspect ite-net >/dev/null 2>&1 || docker network create ite-net >/dev/null

ES="http://localhost:9200"
CONNECT="http://localhost:8083"
CONNECTOR_NAME="pg-orders-source"

echo "=== 1/5 starting containers ==========================================="
docker compose up -d

echo "=== 2/5 waiting for PostgreSQL (wal_level=logical) ====================="
for i in $(seq 1 40); do
  if docker exec cdc-postgres pg_isready -U cdcuser -d db_cdc >/dev/null 2>&1; then break; fi
  sleep 2
done
WAL=$(docker exec cdc-postgres psql -U cdcuser -d db_cdc -tAc "SHOW wal_level;")
[ "$WAL" = "logical" ] || { echo "wal_level is '$WAL', expected logical"; exit 1; }
echo "  wal_level=$WAL"

echo "=== 3/5 waiting for Elasticsearch ====================================="
for i in $(seq 1 90); do
  if curl -fsS "$ES/_cluster/health" >/dev/null 2>&1; then break; fi
  sleep 2
done
curl -fsS "$ES/_cluster/health" >/dev/null || { echo "Elasticsearch not reachable"; exit 1; }
echo "  $(curl -s "$ES" | head -1)"

echo "=== 4/5 applying the index template =================================="
curl -fsS -X PUT "$ES/_index_template/cdc" \
  -H 'Content-Type: application/json' \
  --data-binary "@es/index-template.json" | python -c "import json,sys; print('  ', json.load(sys.stdin))"

echo "=== 5/5 registering the Debezium connector ==========================="
for i in $(seq 1 60); do
  if curl -fsS "$CONNECT/connectors" >/dev/null 2>&1; then break; fi
  sleep 2
done

if [ "${1:-}" = "--reset" ]; then
  curl -s -X DELETE "$CONNECT/connectors/$CONNECTOR_NAME" >/dev/null || true
  docker exec cdc-postgres psql -U cdcuser -d db_cdc -tAc \
    "SELECT pg_drop_replication_slot(slot_name) FROM pg_replication_slots WHERE slot_name='cdc_orders_slot';" >/dev/null 2>&1 || true
  echo "  connector and replication slot dropped"
fi

if curl -fsS "$CONNECT/connectors/$CONNECTOR_NAME" >/dev/null 2>&1; then
  echo "  connector already registered"
  curl -s -X PUT "$CONNECT/connectors/$CONNECTOR_NAME/config" \
    -H 'Content-Type: application/json' \
    --data-binary "$(python -c "import json;print(json.dumps(json.load(open('connectors/pg-source.json'))['config']))")" >/dev/null
  echo "  configuration refreshed"
else
  curl -fsS -X POST "$CONNECT/connectors" \
    -H 'Content-Type: application/json' \
    --data-binary "@connectors/pg-source.json" >/dev/null
  echo "  registered $CONNECTOR_NAME"
fi

echo "  waiting for RUNNING ..."
for i in $(seq 1 60); do
  STATE=$(curl -s "$CONNECT/connectors/$CONNECTOR_NAME/status" | python -c "
import json,sys
d=json.load(sys.stdin)
print(d['connector']['state'], (d['tasks'][0]['state'] if d.get('tasks') else 'NOTASK'))
" 2>/dev/null || echo "PENDING PENDING")
  case "$STATE" in
    "RUNNING RUNNING") echo "  connector RUNNING"; break ;;
    *FAILED*) echo "  connector FAILED: $STATE"; curl -s "$CONNECT/connectors/$CONNECTOR_NAME/status"; exit 1 ;;
  esac
  sleep 2
done

cat <<'EOF'

=== ready =============================================================
  Kafka topics   : docker exec cdc-kafka /opt/kafka/bin/kafka-topics.sh \
                     --bootstrap-server localhost:9092 --list
  Connector      : curl -s localhost:8083/connectors/pg-orders-source/status
  Elasticsearch  : curl -s localhost:9200/_cat/indices/cdc-*?v
  Sink log       : docker logs -f cdc-sink
  Prove sync     : ./verify.sh
EOF

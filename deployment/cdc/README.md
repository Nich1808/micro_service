# PostgreSQL → Elasticsearch (Change Data Capture)

Debezium reads the PostgreSQL write-ahead log, publishes every row change to
Kafka, and a sink consumer transforms the Debezium envelope into Elasticsearch
documents using the bulk API. PostgreSQL stays the source of truth; Elasticsearch
is a near-real-time, query-optimised copy.

```
┌──────────────┐   logical    ┌───────────┐    Debezium    ┌─────────────┐
│ cdc-postgres │──replication─▶│ cdc-kafka │◀── connector ──│ cdc-connect │
│ wal_level =  │   (pgoutput) │  (KRaft)  │   (source)     │  :8083      │
│   logical    │              └─────┬─────┘                └─────────────┘
└──────────────┘                    │ cdc.public.customers
                                    │ cdc.public.orders
                                    │ cdc.public.order_items
                              ┌─────▼──────┐   _bulk API    ┌──────────────────┐
                              │  cdc-sink  │───────────────▶│ cdc-elasticsearch│
                              │  (Python)  │  transform     │  :9200           │
                              └────────────┘                └──────────────────┘
```

## Containers

| Service | Image | Port | Role |
| --- | --- | --- | --- |
| `cdc-postgres` | `postgres:18.4` | 9545 | source DB, `wal_level=logical` |
| `cdc-kafka` | `apache/kafka:4.1.1` | 19092 | single-node KRaft broker |
| `cdc-connect` | `debezium/connect:3.0.0.Final` | 8083 | Kafka Connect + Debezium PG connector |
| `cdc-elasticsearch` | `elasticsearch:8.19.25` | 9200 | sink, security disabled for the demo |
| `cdc-sink` | built from `sink/` | – | consumer that transforms and bulk indexes |

All five join the external `ite-net` network, so they address each other by
container name.

## Quick start

```bash
cd deployment/cdc
./setup.sh          # up -d, wait for health, index template, register connector
./verify.sh         # proves INSERT / UPDATE / DELETE stay in sync
```

`./setup.sh --reset` drops the connector and replication slot first, so the next
run takes a fresh snapshot.

## Files

| File | Purpose |
| --- | --- |
| `docker-compose.yaml` | the five containers |
| `init/01-schema.sql` | demo schema, `debezium` replication role, seed rows |
| `connectors/pg-source.json` | Debezium PostgreSQL source connector |
| `es/index-template.json` | explicit index mappings for `cdc-*` |
| `sink/elasticsearch_sink.py` | consume → transform → bulk index |
| `setup.sh` / `verify.sh` | reproducible start and proof |

## Schema

`customers 1─N orders 1─N order_items`, with `REPLICA IDENTITY FULL` on all three
so UPDATE and DELETE events carry the previous row image.

## How the flow works

1. **WAL capture.** `wal_level=logical` makes PostgreSQL publish row-level changes
   in a logical, parseable form. The `debezium` role holds `REPLICATION` privilege
   and the connector opens a logical replication slot (`cdc_orders_slot`), then
   streams through the pre-created publication (`cdc_publication`).
2. **Kafka topics.** One topic per table, `cdc.public.<table>`, because
   `topic.prefix=cdc` + `schema.include.list=public`.
3. **Sink transform.** The consumer unwraps the envelope, converts `snake_case`
   columns to `camelCase`, coerces Debezium's string-encoded decimals back to
   numbers, and attaches a `_cdc` block with `op`, `table`, `lsn` and `tsMs`.
4. **Bulk indexing.** Up to 200 actions are buffered and written with
   `helpers.bulk`. Offsets are committed **after** Elasticsearch acknowledges the
   batch — at-least-once delivery, made idempotent by using the source primary key
   as the document `_id`.
5. **Deletes.** `op=d` becomes a `delete` action keyed by `before.id`, so the
   document disappears from the index instead of being tombstoned.

## Connector properties, in plain language

| Property | Value | Why |
| --- | --- | --- |
| `plugin.name` | `pgoutput` | native PostgreSQL 10+ logical decoding, no extra extension |
| `slot.name` | `cdc_orders_slot` | the replication cursor; PostgreSQL retains WAL until it advances |
| `publication.name` | `cdc_publication` | defines which tables the slot publishes; created by `init/01-schema.sql` as the table owner, because `CREATE PUBLICATION` requires ownership and `debezium` is not a superuser |
| `publication.autocreate.mode` | `disabled` | the connector only *uses* the publication; it never tries to create one (which would need table ownership) |
| `key/value.converter` + `.schemas.enable` | `JsonConverter`, `false` | connector-level converters override the worker defaults, so Kafka messages are plain JSON instead of `{"schema":…,"payload":…}` wrappers |
| `table.include.list` | 3 tables | keeps unrelated tables out of the stream |
| `snapshot.mode` | `initial` | copies existing rows before switching to streaming |
| `tombstones.on.delete` | `true` | emits a null-value tombstone so log compaction can purge keys |
| `decimal.handling.mode` | `string` | decimals arrive as `"149.98"`, not base64 bytes |
| `heartbeat.interval.ms` | `10000` | keeps the slot advancing when the tables are idle |

## Message shape

```json
{
  "before": null,
  "after":  { "id": "1111...", "full_name": "Sokha Chann", "email": "sokha@example.com" },
  "source": { "db": "db_cdc", "schema": "public", "table": "customers", "lsn": 24112496 },
  "op": "c",
  "ts_ms": 1764100000000
}
```

`op`: `r` snapshot read, `c` insert, `u` update, `d` delete, `t` truncate.

The result in Elasticsearch:

```json
{
  "id": "1111...", "fullName": "Sokha Chann", "email": "sokha@example.com",
  "_cdc": { "op": "c", "table": "customers", "lsn": 24124, "tsMs": 1764100000000 }
}
```

## Verifying by hand

```bash
# change a row
docker exec -i cdc-postgres psql -U cdcuser -d db_cdc \
  -c "UPDATE orders SET status='SHIPPED' WHERE order_number='ORD-2026-0001';"

# watch the sink
docker logs -f cdc-sink

# read it back out of Elasticsearch
curl -s "localhost:9200/cdc-orders/_search?q=orderNumber:ORD-2026-0001" | python -m json.tool | head -30

# documents vs rows
curl -s "localhost:9200/_cat/indices/cdc-*?v"
docker exec -i cdc-postgres psql -U cdcuser -d db_cdc -c "SELECT count(*) FROM orders;"
```

## Troubleshooting

| Symptom | Cause | Fix |
| --- | --- | --- |
| connector `FAILED`, "logical decoding requires wal_level >= logical" | Postgres started without the flag | the compose `command:` must keep `-c wal_level=logical` |
| "replication slot already exists" after a reset | the slot outlived the connector | `./setup.sh --reset` |
| no documents in ES | sink not consuming | `docker logs cdc-sink`, then `curl localhost:9200/_cat/indices?v` |
| ES refuses to start | fewer than ~1.5 GB RAM free | stop other containers; single-node needs its 512 MB heap |
| documents present but fields unsearchable | mapping says `dynamic:false` and the column is new | add the field to `es/index-template.json` and reindex |

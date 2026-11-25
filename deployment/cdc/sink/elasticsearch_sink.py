"""
Kafka -> Elasticsearch sink for the Debezium PostgreSQL CDC stream.

Why a consumer instead of a sink connector: the Confluent Elasticsearch sink
connector is not available on any artifact channel reachable from this machine,
and a small consumer makes the transformation step explicit, which is exactly
what the assignment asks us to demonstrate:

    consume CDC events -> transform -> bulk index -> keep PG and ES in sync

Message contract
----------------
Kafka Connect runs with JsonConverter and schemas disabled, so every message is
a plain Debezium envelope:

    {"before": {...}|null, "after": {...}|null, "source": {...}, "op": "c|u|d|r", "ts_ms": 123}

`op` semantics:
    r = snapshot read      -> index
    c = create (INSERT)    -> index
    u = update (UPDATE)    -> index (full document replace, keyed by PK)
    d = delete (DELETE)    -> delete by PK
    t = truncate           -> ignored
"""

from __future__ import annotations

import json
import logging
import os
import signal
import sys
import time
from typing import Any

from confluent_kafka import Consumer, KafkaError, KafkaException
from elasticsearch import Elasticsearch, helpers

logging.basicConfig(
    level=os.getenv("LOG_LEVEL", "INFO"),
    format="%(asctime)s %(levelname)-7s %(message)s",
    datefmt="%H:%M:%S",
)
log = logging.getLogger("es-sink")

BOOTSTRAP = os.getenv("KAFKA_BOOTSTRAP", "cdc-kafka:9092")
TOPICS = [t.strip() for t in os.getenv(
    "KAFKA_TOPICS",
    "cdc.public.customers,cdc.public.orders,cdc.public.order_items",
).split(",") if t.strip()]
GROUP_ID = os.getenv("KAFKA_GROUP", "cdc-elasticsearch-sink")
ES_URL = os.getenv("ES_URL", "http://cdc-elasticsearch:9200")
INDEX_PREFIX = os.getenv("ES_INDEX_PREFIX", "cdc-")
BATCH_MAX = int(os.getenv("BATCH_MAX", "200"))
LINGER_SECONDS = float(os.getenv("LINGER_SECONDS", "1.0"))

# Columns Debezium renders as strings (decimal.handling.mode=string) that the
# Elasticsearch mapping declares as numeric.
NUMERIC_FIELDS = {"quantity", "totalAmount", "unitPrice", "total_amount", "unit_price"}

running = True
warned_topics: set[str] = set()


def _stop(signum, frame):  # noqa: ARG001
    global running
    running = False
    log.info("shutdown signal received, finishing current batch")


def camel_case(name: str) -> str:
    head, *tail = name.split("_")
    return head + "".join(part.capitalize() for part in tail)


def transform(row: dict[str, Any], table: str, op: str, source: dict[str, Any], ts_ms: int) -> dict[str, Any]:
    """Relational row -> Elasticsearch document."""
    doc: dict[str, Any] = {}
    for column, value in row.items():
        key = camel_case(column)
        if key in NUMERIC_FIELDS and isinstance(value, str):
            value = float(value)
        doc[key] = value

    # Provenance so a document can be traced back to its CDC event.
    doc["_cdc"] = {
        "op": op,
        "table": table,
        "lsn": source.get("lsn"),
        "tsMs": ts_ms,
        "indexedAt": time.strftime("%Y-%m-%dT%H:%M:%SZ", time.gmtime()),
    }
    return doc


def index_name_for(topic: str) -> str:
    return INDEX_PREFIX + topic.rsplit(".", 1)[-1].replace("_", "-")


def doc_id(row: dict[str, Any]) -> str | None:
    return str(row["id"]) if row.get("id") is not None else None


def build_actions(events: list[tuple[str, dict[str, Any]]]) -> list[dict[str, Any]]:
    actions: list[dict[str, Any]] = []
    for topic, envelope in events:
        op = envelope.get("op")
        if op == "t":  # TRUNCATE has no row payload
            continue

        table = topic.rsplit(".", 1)[-1]
        index = index_name_for(topic)
        source = envelope.get("source") or {}
        ts_ms = int(envelope.get("ts_ms") or 0)

        if op == "d":
            row = envelope.get("before") or {}
            _id = doc_id(row)
            if _id is None:
                log.warning("DELETE without a primary key on %s, skipping", topic)
                continue
            actions.append({"_op_type": "delete", "_index": index, "_id": _id})
            log.info("DELETE %s/%s", index, _id)
            continue

        if op is None:
            # Schema-wrapped envelopes ("schema"/"payload") mean the connector's
            # converters are misconfigured. Skipping silently would leave the
            # index empty while every metric looks healthy.
            log.error("unexpected envelope on %s (no 'op' field): keys=%s",
                      topic, sorted(envelope)[:6])
            continue

        row = envelope.get("after")
        if not row:
            continue
        _id = doc_id(row)
        if _id is None:
            log.warning("event without primary key on %s, skipping", topic)
            continue

        actions.append({
            "_op_type": "index",
            "_index": index,
            "_id": _id,
            "_source": transform(row, table, op, source, ts_ms),
        })
        log.debug("%s %s/%s", op.upper(), index, _id)
    return actions


def flush(es: Elasticsearch, consumer: Consumer, actions: list[dict[str, Any]]) -> None:
    if not actions:
        consumer.commit(asynchronous=False)
        return

    indexes: set[str] = set()
    deletes = 0
    try:
        succeeded, errors = helpers.bulk(
            es.options(request_timeout=60),
            actions,
            stats_only=False,
            raise_on_error=False,
        )
        for action in actions:
            indexes.add(action["_index"])
            if action["_op_type"] == "delete":
                deletes += 1

        # 404 on delete is not a failure: the document was already gone.
        real_errors = [
            e for e in (errors or [])
            if not (e.get("delete", {}).get("status") == 404)
        ]
        if real_errors:
            log.error("bulk reported %d error(s): %s", len(real_errors), real_errors[:3])
            raise RuntimeError("bulk indexing failed")

        log.info(
            "BULK ok: %d op(s) -> %s (%d delete, %d index)",
            succeeded, ", ".join(sorted(indexes)), deletes, succeeded - deletes,
        )
    except Exception:
        log.exception("bulk failed, offsets NOT committed - events will be retried")
        raise

    # Commit only after Elasticsearch acknowledged the batch: at-least-once with
    # idempotent writes (documents are keyed by the source primary key).
    consumer.commit(asynchronous=False)


def wait_for_es(es: Elasticsearch, timeout: float = 120.0) -> None:
    deadline = time.time() + timeout
    while time.time() < deadline:
        try:
            if es.ping():
                log.info("connected to Elasticsearch at %s", ES_URL)
                return
        except Exception as exc:  # noqa: BLE001
            log.debug("elasticsearch not ready: %s", exc)
        time.sleep(2)
    raise SystemExit(f"Elasticsearch never became reachable at {ES_URL}")


def main() -> int:
    signal.signal(signal.SIGTERM, _stop)
    signal.signal(signal.SIGINT, _stop)

    es = Elasticsearch(ES_URL, request_timeout=30)
    wait_for_es(es)

    consumer = Consumer({
        "bootstrap.servers": BOOTSTRAP,
        "group.id": GROUP_ID,
        "auto.offset.reset": "earliest",
        "enable.auto.commit": False,
        # Commit one event at a time so a batch never skips past a failed record.
        "enable.auto.offset.store": True,
        "max.poll.interval.ms": 300000,
    })
    consumer.subscribe(TOPICS, on_assign=lambda c, p: log.info("assigned %s", [x.topic for x in p]))
    log.info("subscribed to %s as group %s", TOPICS, GROUP_ID)

    pending: list[tuple[str, dict[str, Any]]] = []
    deadline = time.time() + LINGER_SECONDS

    try:
        while running:
            msg = consumer.poll(0.5)

            if msg is not None:
                if msg.error():
                    code = msg.error().code()
                    if code == KafkaError._PARTITION_EOF:
                        continue
                    # The Debezium topics appear only once the connector takes its
                    # first snapshot, which can be well after this sink starts.
                    if code in (KafkaError.UNKNOWN_TOPIC_OR_PART, KafkaError.UNKNOWN_PARTITION):
                        if msg.topic() not in warned_topics:
                            warned_topics.add(msg.topic())
                            log.warning("topic %s not created yet, waiting for the connector", msg.topic())
                        time.sleep(2)
                        continue
                    raise KafkaException(msg.error())

                raw = msg.value()
                if raw is None:  # Debezium tombstone after a delete
                    pending.append((msg.topic(), {"op": "t"}))
                else:
                    try:
                        pending.append((msg.topic(), json.loads(raw)))
                    except json.JSONDecodeError:
                        log.error("unparsable message on %s, skipping", msg.topic())

                if len(pending) >= BATCH_MAX:
                    flush(es, consumer, build_actions(pending))
                    pending.clear()
                    deadline = time.time() + LINGER_SECONDS
                continue

            if pending and time.time() >= deadline:
                flush(es, consumer, build_actions(pending))
                pending.clear()
                deadline = time.time() + LINGER_SECONDS
    finally:
        try:
            if pending:
                flush(es, consumer, build_actions(pending))
        finally:
            consumer.close()
            log.info("sink stopped")
    return 0


if __name__ == "__main__":
    sys.exit(main())

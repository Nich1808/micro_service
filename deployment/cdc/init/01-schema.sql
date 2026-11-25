-- Demo schema for the PostgreSQL -> Elasticsearch CDC flow.
-- customers 1---N orders 1---N order_items  (real one-to-many data).

\set ON_ERROR_STOP on

CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Debezium reads the WAL as a replication client, so it needs a dedicated login
-- with the REPLICATION attribute. Not a superuser.
CREATE ROLE debezium WITH LOGIN REPLICATION PASSWORD 'dbz';

GRANT CONNECT ON DATABASE db_cdc TO debezium;
GRANT USAGE ON SCHEMA public TO debezium;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT SELECT ON TABLES TO debezium;

CREATE TABLE customers (
    id          UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    full_name   VARCHAR(120)  NOT NULL,
    email       VARCHAR(180)  NOT NULL UNIQUE,
    phone       VARCHAR(30),
    status      VARCHAR(20)   NOT NULL DEFAULT 'ACTIVE',
    created_at  TIMESTAMPTZ   NOT NULL DEFAULT now(),
    updated_at  TIMESTAMPTZ   NOT NULL DEFAULT now()
);

CREATE TABLE orders (
    id            UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    customer_id   UUID NOT NULL REFERENCES customers (id),
    order_number  VARCHAR(30)   NOT NULL UNIQUE,
    status        VARCHAR(20)   NOT NULL DEFAULT 'PENDING',
    total_amount  NUMERIC(12, 2) NOT NULL DEFAULT 0,
    currency      CHAR(3)       NOT NULL DEFAULT 'USD',
    created_at    TIMESTAMPTZ   NOT NULL DEFAULT now(),
    updated_at    TIMESTAMPTZ   NOT NULL DEFAULT now()
);

CREATE TABLE order_items (
    id          UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    order_id    UUID NOT NULL REFERENCES orders (id) ON DELETE CASCADE,
    product_name VARCHAR(120)   NOT NULL,
    quantity    INTEGER         NOT NULL CHECK (quantity > 0),
    unit_price  NUMERIC(12, 2)  NOT NULL,
    created_at  TIMESTAMPTZ     NOT NULL DEFAULT now()
);

CREATE INDEX idx_orders_customer ON orders (customer_id);
CREATE INDEX idx_items_order ON order_items (order_id);

-- REPLICA IDENTITY FULL publishes the `before` image on UPDATE/DELETE, which is
-- what lets the sink see the previous state of a row.
ALTER TABLE customers   REPLICA IDENTITY FULL;
ALTER TABLE orders      REPLICA IDENTITY FULL;
ALTER TABLE order_items REPLICA IDENTITY FULL;

GRANT SELECT ON ALL TABLES IN SCHEMA public TO debezium;

-- CREATE PUBLICATION requires ownership of the tables, which the replication
-- role does not have. Creating it here as cdcuser (the owner) lets the connector
-- run with least privilege: publication.autocreate.mode = disabled.
CREATE PUBLICATION cdc_publication FOR TABLE customers, orders, order_items;

-- ---------------------------------------------------------------- seed data ---
INSERT INTO customers (id, full_name, email, phone) VALUES
    ('11111111-1111-1111-1111-111111111111', 'Sokha Chann',   'sokha@example.com',   '+855 12 111 111'),
    ('22222222-2222-2222-2222-222222222222', 'Dara Vann',     'dara@example.com',    '+855 12 222 222');

INSERT INTO orders (id, customer_id, order_number, status, total_amount) VALUES
    ('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', '11111111-1111-1111-1111-111111111111',
     'ORD-2026-0001', 'CONFIRMED', 149.98),
    ('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', '22222222-2222-2222-2222-222222222222',
     'ORD-2026-0002', 'PENDING',    39.99);

INSERT INTO order_items (order_id, product_name, quantity, unit_price) VALUES
    ('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'Mechanical Keyboard', 1, 99.99),
    ('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'Mouse Pad',           2, 25.00),
    ('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', 'USB-C Cable',         1, 39.99);

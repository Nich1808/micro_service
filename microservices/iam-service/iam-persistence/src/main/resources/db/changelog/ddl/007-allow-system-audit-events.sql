-- liquibase formatted sql

-- changeset istad:007-allow-system-audit-events
-- Changes to a role, permission, client or system setting belong to no single account, so
-- the audit trail has to be able to record events without an owning user. The per-user
-- activity feed keeps filtering on user_id and is unaffected.
ALTER TABLE iam_account_activities ALTER COLUMN user_id DROP NOT NULL;

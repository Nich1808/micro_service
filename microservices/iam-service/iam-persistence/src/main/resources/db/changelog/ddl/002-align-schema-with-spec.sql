-- liquibase formatted sql

-- changeset istad:002-rename-audit-flags
ALTER TABLE iam_users RENAME COLUMN deleted TO is_deleted;

ALTER TABLE iam_roles RENAME COLUMN system TO is_system;
ALTER TABLE iam_roles RENAME COLUMN active TO is_active;
ALTER TABLE iam_roles RENAME COLUMN deleted TO is_deleted;

ALTER TABLE iam_permissions RENAME COLUMN system TO is_system;
ALTER TABLE iam_permissions RENAME COLUMN active TO is_active;
ALTER TABLE iam_permissions RENAME COLUMN deleted TO is_deleted;

ALTER TABLE iam_identities RENAME COLUMN deleted TO is_deleted;

-- changeset istad:002-rename-address-columns
ALTER TABLE iam_users RENAME COLUMN street_address TO address_street_address;
ALTER TABLE iam_users RENAME COLUMN locality TO address_locality;
ALTER TABLE iam_users RENAME COLUMN region TO address_region;
ALTER TABLE iam_users RENAME COLUMN postal_code TO address_postal_code;
ALTER TABLE iam_users RENAME COLUMN country TO address_country;

-- changeset istad:002-align-column-types
ALTER TABLE iam_users ALTER COLUMN username TYPE VARCHAR(50);
ALTER TABLE iam_users ALTER COLUMN gender TYPE VARCHAR(20);
ALTER TABLE iam_users ALTER COLUMN zoneinfo TYPE VARCHAR(50);
ALTER TABLE iam_users ALTER COLUMN locale TYPE VARCHAR(10);
ALTER TABLE iam_users ALTER COLUMN locale SET NOT NULL;
ALTER TABLE iam_users ALTER COLUMN phone_number TYPE VARCHAR(20);
ALTER TABLE iam_users ALTER COLUMN address_postal_code TYPE VARCHAR(20);
ALTER TABLE iam_users ALTER COLUMN address_country TYPE VARCHAR(2);
ALTER TABLE iam_users ALTER COLUMN password_hash DROP NOT NULL;

ALTER TABLE iam_identities ALTER COLUMN provider TYPE VARCHAR(20);
ALTER TABLE iam_identities ALTER COLUMN display_name TYPE VARCHAR(150);

ALTER TABLE iam_user_permissions ALTER COLUMN effect TYPE VARCHAR(5);

-- changeset istad:002-unique-user-phone-number
ALTER TABLE iam_users
    ADD CONSTRAINT uk_iam_user_phone_number UNIQUE (phone_number);

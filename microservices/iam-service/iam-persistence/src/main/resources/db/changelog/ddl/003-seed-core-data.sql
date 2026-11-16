-- liquibase formatted sql

-- changeset istad:003-seed-permissions
INSERT INTO iam_permissions
    (uuid, name, resource, action, description,
     is_system, is_active, is_deleted,
     created_at, created_by, updated_at, updated_by, version)
VALUES
    ('11111111-1111-4111-8111-000000000001', 'user:create', 'user', 'create', 'Create a user', true, true, false, now(), 'system', now(), 'system', 0),
    ('11111111-1111-4111-8111-000000000002', 'user:read', 'user', 'read', 'Read users', true, true, false, now(), 'system', now(), 'system', 0),
    ('11111111-1111-4111-8111-000000000003', 'user:update', 'user', 'update', 'Update a user', true, true, false, now(), 'system', now(), 'system', 0),
    ('11111111-1111-4111-8111-000000000004', 'user:delete', 'user', 'delete', 'Delete a user', true, true, false, now(), 'system', now(), 'system', 0),
    ('11111111-1111-4111-8111-000000000005', 'user:assign', 'user', 'assign', 'Assign roles and permissions to a user', true, true, false, now(), 'system', now(), 'system', 0),
    ('11111111-1111-4111-8111-000000000006', 'role:create', 'role', 'create', 'Create a role', true, true, false, now(), 'system', now(), 'system', 0),
    ('11111111-1111-4111-8111-000000000007', 'role:read', 'role', 'read', 'Read roles', true, true, false, now(), 'system', now(), 'system', 0),
    ('11111111-1111-4111-8111-000000000008', 'role:update', 'role', 'update', 'Update a role', true, true, false, now(), 'system', now(), 'system', 0),
    ('11111111-1111-4111-8111-000000000009', 'role:delete', 'role', 'delete', 'Delete a role', true, true, false, now(), 'system', now(), 'system', 0),
    ('11111111-1111-4111-8111-000000000010', 'role:assign', 'role', 'assign', 'Assign permissions to a role', true, true, false, now(), 'system', now(), 'system', 0),
    ('11111111-1111-4111-8111-000000000011', 'permission:create', 'permission', 'create', 'Create a permission', true, true, false, now(), 'system', now(), 'system', 0),
    ('11111111-1111-4111-8111-000000000012', 'permission:read', 'permission', 'read', 'Read permissions', true, true, false, now(), 'system', now(), 'system', 0),
    ('11111111-1111-4111-8111-000000000013', 'permission:update', 'permission', 'update', 'Update a permission', true, true, false, now(), 'system', now(), 'system', 0),
    ('11111111-1111-4111-8111-000000000014', 'permission:delete', 'permission', 'delete', 'Delete a permission', true, true, false, now(), 'system', now(), 'system', 0);

-- changeset istad:003-seed-roles
INSERT INTO iam_roles
    (uuid, name, description,
     is_system, is_active, is_deleted,
     created_at, created_by, updated_at, updated_by, version)
VALUES
    ('22222222-2222-4222-8222-000000000001', 'ADMIN', 'System administrator with full access', true, true, false, now(), 'system', now(), 'system', 0),
    ('22222222-2222-4222-8222-000000000002', 'USER', 'Default user with read access to user, role and permission', true, true, false, now(), 'system', now(), 'system', 0);

-- changeset istad:003-seed-admin-role-permissions
INSERT INTO iam_role_permissions (role_id, permission_id, assigned_at, assigned_by)
SELECT role_row.id, permission_row.id, now(), 'system'
FROM iam_roles role_row
CROSS JOIN iam_permissions permission_row
WHERE role_row.name = 'ADMIN';

-- changeset istad:003-seed-user-role-permissions
INSERT INTO iam_role_permissions (role_id, permission_id, assigned_at, assigned_by)
SELECT role_row.id, permission_row.id, now(), 'system'
FROM iam_roles role_row
JOIN iam_permissions permission_row
    ON permission_row.name IN ('user:read', 'role:read', 'permission:read')
WHERE role_row.name = 'USER';

import { useCallback, useEffect, useState } from 'react';
import { Alert, Card, Empty, Field, Loading } from '../../components/ui';
import { useI18n } from '../../i18n';
import {
  adminApi,
  type PermissionDetail,
  type RoleDetail,
} from '../../lib/adminApi';

export function AdminRolesPage() {
  const { t } = useI18n();
  const [roles, setRoles] = useState<RoleDetail[]>([]);
  const [permissions, setPermissions] = useState<PermissionDetail[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [busy, setBusy] = useState<string | null>(null);
  const [selected, setSelected] = useState<RoleDetail | null>(null);
  const [form, setForm] = useState({ name: '', description: '' });

  const load = useCallback(() => {
    setLoading(true);
    Promise.all([adminApi.rolesDetail(), adminApi.permissions(0, 200)])
      .then(([roleList, permissionPage]) => {
        setRoles(roleList);
        setPermissions(permissionPage.content);
      })
      .catch((cause: Error) => setError(cause.message))
      .finally(() => setLoading(false));
  }, []);

  useEffect(load, [load]);

  const act = async (key: string, action: () => Promise<unknown>) => {
    setBusy(key);
    setError(null);
    try {
      await action();
      load();
    } catch (cause) {
      setError(cause instanceof Error ? cause.message : 'Action failed');
    } finally {
      setBusy(null);
    }
  };

  if (loading && roles.length === 0) {
    return <Loading />;
  }

  return (
    <>
      <Card
        title={t('admin.roles', 'Roles')}
        subtitle={t('admin.rolesSub', 'Create, view, update and delete roles, and assign or remove permissions')}
      >
        {error ? <Alert kind="error">{error}</Alert> : null}

        <form
          onSubmit={(event) => {
            event.preventDefault();
            act('create', async () => {
              await adminApi.createRole({ name: form.name, description: form.description });
              setForm({ name: '', description: '' });
            });
          }}
        >
          <div className="grid">
            <Field label={t('admin.roleName', 'Role name')}>
              <input required value={form.name}
                onChange={(e) => setForm({ ...form, name: e.target.value })} />
            </Field>
            <Field label="Description">
              <input value={form.description}
                onChange={(e) => setForm({ ...form, description: e.target.value })} />
            </Field>
          </div>
          <div className="actions">
            <button className="btn" type="submit" disabled={busy === 'create'}>
              {t('admin.createRole', 'Create role')}
            </button>
          </div>
        </form>

        {roles.length === 0 ? (
          <Empty />
        ) : (
          <table className="table" style={{ marginTop: 14 }}>
            <thead>
              <tr>
                <th>Name</th>
                <th>Description</th>
                <th>Status</th>
                <th style={{ textAlign: 'right' }}>Actions</th>
              </tr>
            </thead>
            <tbody>
              {roles.map((role) => (
                <tr key={role.uuid}>
                  <td className="mono">{role.name}</td>
                  <td>{role.description ?? '—'}</td>
                  <td>
                    {role.active ? (
                      <span className="badge badge--ok">active</span>
                    ) : (
                      <span className="badge">inactive</span>
                    )}
                    {role.system ? (
                      <span className="badge badge--brand" style={{ marginLeft: 6 }}>system</span>
                    ) : null}
                  </td>
                  <td>
                    <div style={{ display: 'flex', gap: 6, justifyContent: 'flex-end', flexWrap: 'wrap' }}>
                      <button className="btn btn--sm btn--secondary" type="button"
                        onClick={() => setSelected(role)}>Permissions</button>
                      <button className="btn btn--sm btn--secondary" type="button"
                        disabled={busy === role.uuid || role.system}
                        onClick={() => act(role.uuid, () => adminApi.activateRole(role.uuid))}>
                        Activate
                      </button>
                      <button className="btn btn--sm btn--secondary" type="button"
                        disabled={busy === role.uuid || role.system}
                        onClick={() => act(role.uuid, () => adminApi.deactivateRole(role.uuid))}>
                        Deactivate
                      </button>
                      <button className="btn btn--sm btn--danger" type="button"
                        disabled={busy === role.uuid || role.system}
                        onClick={() => {
                          if (window.confirm(`Delete role ${role.name}?`)) {
                            act(role.uuid, () => adminApi.deleteRole(role.uuid));
                          }
                        }}>
                        Delete
                      </button>
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </Card>

      {selected ? (
        <Card
          title={`${selected.name} — ${t('admin.permissions', 'Permissions')}`}
          actions={
            <button className="btn btn--sm btn--secondary" type="button"
              onClick={() => setSelected(null)}>Close</button>
          }
        >
          <p className="subtitle" style={{ marginBottom: 12 }}>
            {t('admin.assignPermissionsHint', 'Assign or remove permissions for this role')}
          </p>
          <div className="rows">
            {permissions.map((permission) => (
              <div className="row" key={permission.uuid}>
                <div>
                  <div className="row__title mono">{permission.name}</div>
                  <div className="row__meta">{permission.description ?? '—'}</div>
                </div>
                <div style={{ display: 'flex', gap: 6 }}>
                  <button className="btn btn--sm btn--secondary" type="button"
                    disabled={busy === permission.uuid}
                    onClick={() =>
                      act(permission.uuid, () =>
                        adminApi.assignPermissionsToRole(
                          selected.uuid, [permission.uuid], 'Administrator action',
                        ),
                      )
                    }>
                    Assign
                  </button>
                  <button className="btn btn--sm btn--danger" type="button"
                    disabled={busy === permission.uuid}
                    onClick={() =>
                      act(permission.uuid, () =>
                        adminApi.revokePermissionFromRole(
                          selected.uuid, permission.uuid, 'Administrator action',
                        ),
                      )
                    }>
                    Remove
                  </button>
                </div>
              </div>
            ))}
          </div>
        </Card>
      ) : null}
    </>
  );
}

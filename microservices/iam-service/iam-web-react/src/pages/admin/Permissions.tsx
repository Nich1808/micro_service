import { useCallback, useEffect, useState } from 'react';
import { Alert, Card, Empty, Field, Loading } from '../../components/ui';
import { useI18n } from '../../i18n';
import { adminApi, type PermissionDetail } from '../../lib/adminApi';

export function AdminPermissionsPage() {
  const { t } = useI18n();
  const [permissions, setPermissions] = useState<PermissionDetail[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [busy, setBusy] = useState<string | null>(null);
  const [form, setForm] = useState({ resource: '', action: '', description: '' });

  const load = useCallback(() => {
    setLoading(true);
    adminApi
      .permissions(0, 200)
      .then((page) => setPermissions(page.content))
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

  if (loading && permissions.length === 0) {
    return <Loading />;
  }

  return (
    <Card
      title={t('admin.permissions', 'Permissions')}
      subtitle={t('admin.permissionsSub', 'Create, view, update and delete permissions and define resource-based access')}
    >
      {error ? <Alert kind="error">{error}</Alert> : null}

      <form
        onSubmit={(event) => {
          event.preventDefault();
          act('create', async () => {
            await adminApi.createPermission(form);
            setForm({ resource: '', action: '', description: '' });
          });
        }}
      >
        <div className="grid">
          <Field label={t('admin.resource', 'Resource')}>
            <input required placeholder="course" value={form.resource}
              onChange={(e) => setForm({ ...form, resource: e.target.value })} />
          </Field>
          <Field label={t('admin.action', 'Action')}>
            <input required placeholder="publish" value={form.action}
              onChange={(e) => setForm({ ...form, action: e.target.value })} />
          </Field>
          <Field label="Description">
            <input value={form.description}
              onChange={(e) => setForm({ ...form, description: e.target.value })} />
          </Field>
        </div>
        <div className="actions">
          <button className="btn" type="submit" disabled={busy === 'create'}>
            {t('admin.createPermission', 'Create permission')}
          </button>
        </div>
      </form>

      {permissions.length === 0 ? (
        <Empty />
      ) : (
        <table className="table" style={{ marginTop: 14 }}>
          <thead>
            <tr>
              <th>Name</th>
              <th>Resource</th>
              <th>Action</th>
              <th>Status</th>
              <th style={{ textAlign: 'right' }}>Actions</th>
            </tr>
          </thead>
          <tbody>
            {permissions.map((permission) => (
              <tr key={permission.uuid}>
                <td className="mono">{permission.name}</td>
                <td>{permission.resource}</td>
                <td>{permission.action}</td>
                <td>
                  {permission.active ? (
                    <span className="badge badge--ok">active</span>
                  ) : (
                    <span className="badge">inactive</span>
                  )}
                  {permission.system ? (
                    <span className="badge badge--brand" style={{ marginLeft: 6 }}>system</span>
                  ) : null}
                </td>
                <td>
                  <div style={{ display: 'flex', gap: 6, justifyContent: 'flex-end', flexWrap: 'wrap' }}>
                    <button className="btn btn--sm btn--secondary" type="button"
                      disabled={busy === permission.uuid || permission.system}
                      onClick={() => act(permission.uuid, () => adminApi.activatePermission(permission.uuid))}>
                      Activate
                    </button>
                    <button className="btn btn--sm btn--secondary" type="button"
                      disabled={busy === permission.uuid || permission.system}
                      onClick={() => act(permission.uuid, () => adminApi.deactivatePermission(permission.uuid))}>
                      Deactivate
                    </button>
                    <button className="btn btn--sm btn--danger" type="button"
                      disabled={busy === permission.uuid || permission.system}
                      onClick={() => {
                        if (window.confirm(`Delete ${permission.name}?`)) {
                          act(permission.uuid, () => adminApi.deletePermission(permission.uuid));
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
  );
}

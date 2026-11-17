import { useCallback, useEffect, useState } from 'react';
import {
  Alert,
  Card,
  Empty,
  Field,
  Loading,
  Pagination,
  formatDateTime,
} from '../../components/ui';
import { useI18n } from '../../i18n';
import {
  adminApi,
  type PermissionDetail,
  type RoleDetail,
  type UserSummaryRow,
} from '../../lib/adminApi';
import type { LoginAttempt, Page, UserSession } from '../../lib/types';

export function AdminUsersPage() {
  const { t } = useI18n();
  const [data, setData] = useState<Page<UserSummaryRow> | null>(null);
  const [roles, setRoles] = useState<RoleDetail[]>([]);
  const [permissions, setPermissions] = useState<PermissionDetail[]>([]);
  const [page, setPage] = useState(0);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [notice, setNotice] = useState<string | null>(null);
  const [busy, setBusy] = useState<string | null>(null);

  const [selected, setSelected] = useState<UserSummaryRow | null>(null);
  const [sessions, setSessions] = useState<UserSession[]>([]);
  const [history, setHistory] = useState<LoginAttempt[]>([]);
  const [assignedRoles, setAssignedRoles] = useState<string[]>([]);
  const [directPermissions, setDirectPermissions] = useState<
    { name: string; effect: 'GRANT' | 'DENY' }[]
  >([]);
  const [editingProfile, setEditingProfile] = useState<{
    uuid: string;
    givenName: string;
    familyName: string;
    email: string;
    phoneNumber: string;
  } | null>(null);

  const [creating, setCreating] = useState(false);
  const [form, setForm] = useState({
    username: '',
    email: '',
    password: '',
    givenName: '',
    familyName: '',
    roleUuids: [] as string[],
  });

  const load = useCallback((target: number) => {
    setLoading(true);
    Promise.all([
      adminApi.users(target, 20),
      adminApi.rolesDetail(),
      adminApi.permissions(0, 200),
    ])
      .then(([users, roleList, permissionPage]) => {
        setData(users);
        setRoles(roleList);
        setPermissions(permissionPage.content);
        setPage(target);
      })
      .catch((cause: Error) => setError(cause.message))
      .finally(() => setLoading(false));
  }, []);

  useEffect(() => load(0), [load]);

  const act = async (uuid: string, action: () => Promise<unknown>) => {
    setBusy(uuid);
    setError(null);
    try {
      await action();
      load(page);
    } catch (cause) {
      setError(cause instanceof Error ? cause.message : 'Action failed');
    } finally {
      setBusy(null);
    }
  };

  const openDetails = async (user: UserSummaryRow) => {
    setSelected(user);
    setSessions([]);
    setHistory([]);
    try {
      const [s, h, a] = await Promise.all([
        adminApi.userSessions(user.uuid),
        adminApi.userLoginHistory(user.uuid),
        adminApi.userAuthorities(user.uuid),
      ]);
      setSessions(s);
      setHistory(h.content);
      setAssignedRoles(a.roles.map((role) => role.name));
      setDirectPermissions(a.directPermissions);
    } catch (cause) {
      setError(cause instanceof Error ? cause.message : 'Unable to load details');
    }
  };

  const create = async (event: React.FormEvent) => {
    event.preventDefault();
    setBusy('create');
    setError(null);
    try {
      await adminApi.createUser({
        username: form.username,
        email: form.email,
        password: form.password,
        givenName: form.givenName,
        familyName: form.familyName,
        locale: 'en',
        roleUuids: form.roleUuids,
      });
      setCreating(false);
      setForm({ username: '', email: '', password: '', givenName: '', familyName: '', roleUuids: [] });
      setNotice('User created.');
      load(0);
    } catch (cause) {
      setError(cause instanceof Error ? cause.message : 'Unable to create user');
    } finally {
      setBusy(null);
    }
  };

  const roleAssigned = (roleName: string) => assignedRoles.includes(roleName);

  const toggleRole = async (uuid: string, roleName: string) => {
    const role = roles.find((r) => r.name === roleName);
    if (!role) {
      return;
    }

    const assign = !roleAssigned(roleName);

    await act(uuid, () =>
      assign
        ? adminApi.assignRoles(uuid, [role.uuid], 'Administrator action')
        : adminApi.revokeRole(uuid, role.uuid, 'Administrator action'),
    );

    const authorities = await adminApi.userAuthorities(uuid);
    setAssignedRoles(authorities.roles.map((r) => r.name));
  };

  const grantPermission = async (
    uuid: string,
    permissionUuid: string,
    effect: 'GRANT' | 'DENY',
  ) => {
    await act(uuid, () =>
      adminApi.grantPermissions(uuid, [permissionUuid], effect, 'Administrator action'),
    );
    const authorities = await adminApi.userAuthorities(uuid);
    setDirectPermissions(authorities.directPermissions);
  };

  const revokePermission = async (uuid: string, permissionName: string) => {
    const permission = permissions.find((p) => p.name === permissionName);
    if (!permission) {
      return;
    }
    await act(uuid, () =>
      adminApi.revokePermission(uuid, permission.uuid, 'Administrator action'),
    );
    const authorities = await adminApi.userAuthorities(uuid);
    setDirectPermissions(authorities.directPermissions);
  };

  if (loading && !data) {
    return <Loading />;
  }

  return (
    <>
      <Card
        title={t('admin.users', 'Users')}
        subtitle={t('admin.usersSub', 'Create, view, update, delete, activate, deactivate, lock and unlock accounts')}
        actions={
          <button type="button" className="btn" onClick={() => setCreating(!creating)}>
            {creating ? t('common.cancel') : t('admin.createUser', 'Create user')}
          </button>
        }
      >
        {error ? <Alert kind="error">{error}</Alert> : null}
        {notice ? <Alert kind="ok">{notice}</Alert> : null}

        {creating ? (
          <form onSubmit={create} style={{ marginTop: 14 }}>
            <div className="grid">
              <Field label={t('profile.username', 'Username')}>
                <input required value={form.username}
                  onChange={(e) => setForm({ ...form, username: e.target.value })} />
              </Field>
              <Field label={t('profile.email', 'Email')}>
                <input type="email" required value={form.email}
                  onChange={(e) => setForm({ ...form, email: e.target.value })} />
              </Field>
              <Field label={t('security.new', 'Password')}>
                <input type="password" minLength={8} required value={form.password}
                  onChange={(e) => setForm({ ...form, password: e.target.value })} />
              </Field>
              <Field label={t('profile.givenName', 'Given name')}>
                <input required value={form.givenName}
                  onChange={(e) => setForm({ ...form, givenName: e.target.value })} />
              </Field>
              <Field label={t('profile.familyName', 'Family name')}>
                <input required value={form.familyName}
                  onChange={(e) => setForm({ ...form, familyName: e.target.value })} />
              </Field>
              <Field label={t('profile.roles', 'Roles')}>
                <select
                  multiple
                  size={4}
                  value={form.roleUuids}
                  onChange={(e) =>
                    setForm({
                      ...form,
                      roleUuids: Array.from(e.target.selectedOptions, (o) => o.value),
                    })
                  }
                >
                  {roles.map((role) => (
                    <option key={role.uuid} value={role.uuid}>{role.name}</option>
                  ))}
                </select>
              </Field>
            </div>
            <div className="actions">
              <button type="submit" className="btn" disabled={busy === 'create'}>
                {busy === 'create' ? t('common.saving') : t('admin.createUser', 'Create user')}
              </button>
            </div>
          </form>
        ) : null}

        {!data || data.content.length === 0 ? (
          <Empty />
        ) : (
          <>
            <table className="table">
              <thead>
                <tr>
                  <th>{t('profile.username', 'Username')}</th>
                  <th>{t('profile.email', 'Email')}</th>
                  <th>{t('profile.name', 'Name')}</th>
                  <th>Created</th>
                  <th style={{ textAlign: 'right' }}>Actions</th>
                </tr>
              </thead>
              <tbody>
                {data.content.map((user) => (
                  <tr key={user.uuid}>
                    <td className="mono">{user.username}</td>
                    <td>{user.email}</td>
                    <td>{user.name}</td>
                    <td>{formatDateTime(user.createdAt)}</td>
                    <td>
                      <div style={{ display: 'flex', gap: 6, flexWrap: 'wrap', justifyContent: 'flex-end' }}>
                        <button className="btn btn--sm btn--secondary" type="button"
                          onClick={() => openDetails(user)}>Manage</button>
                        <button className="btn btn--sm btn--secondary" type="button"
                          onClick={() => setEditingProfile({
                            uuid: user.uuid,
                            givenName: user.name.split(' ')[0] ?? '',
                            familyName: user.name.split(' ').slice(1).join(' '),
                            email: user.email,
                            phoneNumber: '',
                          })}>
                          Edit
                        </button>
                        <button className="btn btn--sm btn--secondary" type="button"
                          disabled={busy === user.uuid}
                          onClick={() => act(user.uuid, () => adminApi.enableUser(user.uuid))}>
                          Enable
                        </button>
                        <button className="btn btn--sm btn--secondary" type="button"
                          disabled={busy === user.uuid}
                          onClick={() => act(user.uuid, () => adminApi.disableUser(user.uuid))}>
                          Disable
                        </button>
                        <button className="btn btn--sm btn--secondary" type="button"
                          disabled={busy === user.uuid}
                          onClick={() => act(user.uuid, () => adminApi.lockUser(user.uuid))}>
                          Lock
                        </button>
                        <button className="btn btn--sm btn--secondary" type="button"
                          disabled={busy === user.uuid}
                          onClick={() => act(user.uuid, () => adminApi.unlockUser(user.uuid))}>
                          Unlock
                        </button>
                        <button className="btn btn--sm btn--secondary" type="button"
                          disabled={busy === user.uuid}
                          onClick={() => {
                            const password = window.prompt('New password (min 8 chars)');
                            if (password && password.length >= 8) {
                              act(user.uuid, () => adminApi.resetPassword(user.uuid, password));
                            }
                          }}>
                          Reset password
                        </button>
                        <button className="btn btn--sm btn--danger" type="button"
                          disabled={busy === user.uuid}
                          onClick={() => {
                            if (window.confirm(`Delete ${user.username}?`)) {
                              act(user.uuid, () => adminApi.deleteUser(user.uuid));
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

            <Pagination page={page} totalPages={data.page.totalPages} onChange={load} />
          </>
        )}
      </Card>

      {editingProfile ? (
        <Card
          title={t('admin.editUser', 'Edit user')}
          subtitle={t('admin.editUserSub', 'Update the user profile details')}
          actions={
            <button className="btn btn--sm btn--secondary" type="button"
              onClick={() => setEditingProfile(null)}>Close</button>
          }
        >
          <form
            onSubmit={async (event) => {
              event.preventDefault();
              setBusy('edit');
              setError(null);
              try {
                await adminApi.updateUser(editingProfile.uuid, {
                  givenName: editingProfile.givenName || undefined,
                  familyName: editingProfile.familyName || undefined,
                  email: editingProfile.email || undefined,
                  phoneNumber: editingProfile.phoneNumber || undefined,
                });
                setNotice('User updated.');
                setEditingProfile(null);
                load(page);
              } catch (cause) {
                setError(cause instanceof Error ? cause.message : 'Unable to update user');
              } finally {
                setBusy(null);
              }
            }}
          >
            <div className="grid">
              <Field label={t('profile.givenName', 'Given name')}>
                <input value={editingProfile.givenName}
                  onChange={(e) => setEditingProfile({ ...editingProfile, givenName: e.target.value })} />
              </Field>
              <Field label={t('profile.familyName', 'Family name')}>
                <input value={editingProfile.familyName}
                  onChange={(e) => setEditingProfile({ ...editingProfile, familyName: e.target.value })} />
              </Field>
              <Field label={t('profile.email', 'Email')}>
                <input type="email" value={editingProfile.email}
                  onChange={(e) => setEditingProfile({ ...editingProfile, email: e.target.value })} />
              </Field>
              <Field label={t('profile.phone', 'Phone number')}>
                <input value={editingProfile.phoneNumber}
                  onChange={(e) => setEditingProfile({ ...editingProfile, phoneNumber: e.target.value })} />
              </Field>
            </div>
            <div className="actions">
              <button className="btn" type="submit" disabled={busy === 'edit'}>
                {busy === 'edit' ? t('common.saving') : t('common.save')}
              </button>
            </div>
          </form>
        </Card>
      ) : null}

      {selected ? (
        <Card
          title={`${selected.username} — ${t('admin.manageUser', 'sessions, history and roles')}`}
          actions={
            <button className="btn btn--secondary btn--sm" type="button"
              onClick={() => setSelected(null)}>Close</button>
          }
        >
          <h3 style={{ fontSize: 14, marginBottom: 8 }}>{t('nav.sessions', 'Active sessions')}</h3>
          {sessions.length === 0 ? (
            <Empty />
          ) : (
            <div className="rows">
              {sessions.map((session) => (
                <div className="row" key={session.uuid}>
                  <div>
                    <div className="row__title">
                      {session.device} · {session.browser}
                    </div>
                    <div className="row__meta">
                      {session.operatingSystem} · {session.ipAddress} · {session.location ?? '—'}
                    </div>
                  </div>
                  <div className="row__aside">
                    <div>{formatDateTime(session.lastActivityAt)}</div>
                    <button className="btn btn--sm btn--danger" type="button"
                      onClick={async () => {
                        await adminApi.terminateUserSession(selected.uuid, session.uuid);
                        setSessions(await adminApi.userSessions(selected.uuid));
                      }}>
                      Terminate
                    </button>
                  </div>
                </div>
              ))}
            </div>
          )}

          <div className="actions">
            <button className="btn btn--sm btn--secondary" type="button"
              onClick={async () => {
                await adminApi.terminateAllUserSessions(selected.uuid);
                setSessions(await adminApi.userSessions(selected.uuid));
              }}>
              {t('sessions.terminateAll', 'Terminate all sessions')}
            </button>
          </div>

          <h3 style={{ fontSize: 14, margin: '18px 0 8px' }}>
            {t('admin.loginHistory', 'Login history')}
          </h3>
          {history.length === 0 ? (
            <Empty />
          ) : (
            <table className="table">
              <thead>
                <tr>
                  <th>{t('history.time', 'Time')}</th>
                  <th>{t('history.result', 'Result')}</th>
                  <th>Device</th>
                  <th>IP</th>
                </tr>
              </thead>
              <tbody>
                {history.map((attempt) => (
                  <tr key={attempt.uuid}>
                    <td>{formatDateTime(attempt.attemptedAt)}</td>
                    <td>
                      <span className={`badge ${attempt.successful ? 'badge--ok' : 'badge--danger'}`}>
                        {attempt.successful ? t('history.success', 'Success') : t('history.failed', 'Failed')}
                      </span>
                      {attempt.suspicious ? (
                        <span className="badge badge--danger" style={{ marginLeft: 6 }}>
                          {t('admin.suspicious', 'Suspicious')}
                        </span>
                      ) : null}
                    </td>
                    <td>{attempt.device} · {attempt.browser}</td>
                    <td className="mono">{attempt.ipAddress ?? '—'}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          )}

          <h3 style={{ fontSize: 14, margin: '18px 0 8px' }}>{t('profile.roles', 'Roles')}</h3>
          <div style={{ display: 'flex', gap: 8, flexWrap: 'wrap' }}>
            {roles.map((role) => {
              const assigned = roleAssigned(role.name);

              return (
                <button
                  key={role.uuid}
                  type="button"
                  className={`btn btn--sm ${assigned ? 'btn--danger' : 'btn--secondary'}`}
                  disabled={busy === selected.uuid}
                  onClick={() => toggleRole(selected.uuid, role.name)}
                >
                  {assigned ? `Revoke ${role.name}` : `Assign ${role.name}`}
                </button>
              );
            })}
          </div>

          <h3 style={{ fontSize: 14, margin: '18px 0 8px' }}>
            {t('admin.directPermissions', 'Direct permissions')}
          </h3>
          {directPermissions.length === 0 ? (
            <p className="subtitle">{t('admin.noDirectPermissions', 'No permission is granted directly to this user.')}</p>
          ) : (
            <div style={{ display: 'flex', gap: 8, flexWrap: 'wrap', marginBottom: 10 }}>
              {directPermissions.map((permission) => (
                <span
                  key={permission.name}
                  className={`badge ${permission.effect === 'GRANT' ? 'badge--ok' : 'badge--danger'}`}
                >
                  {permission.name} · {permission.effect}
                  <button
                    type="button"
                    className="btn btn--sm btn--secondary"
                    style={{ marginLeft: 8, padding: '0 8px' }}
                    disabled={busy === selected.uuid}
                    onClick={() => revokePermission(selected.uuid, permission.name)}
                  >
                    ×
                  </button>
                </span>
              ))}
            </div>
          )}

          <Field label={t('admin.grantPermission', 'Grant a permission directly')}>
            <select
              defaultValue=""
              disabled={busy === selected.uuid}
              onChange={(event) => {
                const value = event.target.value;
                if (!value) {
                  return;
                }
                const [permissionUuid, effect] = value.split('|');
                grantPermission(
                  selected.uuid,
                  permissionUuid,
                  effect as 'GRANT' | 'DENY',
                );
                event.target.value = '';
              }}
            >
              <option value="">{t('admin.selectPermission', 'Select a permission…')}</option>
              {permissions
                .filter((permission) =>
                  !directPermissions.some((grant) => grant.name === permission.name))
                .map((permission) => (
                  <optgroup key={permission.uuid} label={permission.resource}>
                    <option value={`${permission.uuid}|GRANT`}>
                      GRANT · {permission.name}
                    </option>
                    <option value={`${permission.uuid}|DENY`}>
                      DENY · {permission.name}
                    </option>
                  </optgroup>
                ))}
            </select>
          </Field>
        </Card>
      ) : null}
    </>
  );
}

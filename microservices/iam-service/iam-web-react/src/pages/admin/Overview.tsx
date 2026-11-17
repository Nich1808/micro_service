import { useEffect, useState } from 'react';
import { Alert, Card, Empty, Loading, formatDateTime } from '../../components/ui';
import { useI18n } from '../../i18n';
import { adminApi, type AdminOverview } from '../../lib/adminApi';

function Stat({ label, value }: { label: string; value: number | string }) {
  return (
    <div
      className="card"
      style={{ padding: 18, boxShadow: 'none', background: 'var(--surface-muted)' }}
    >
      <div style={{ fontSize: 28, fontWeight: 700, letterSpacing: '-0.02em' }}>
        {value}
      </div>
      <div className="label" style={{ marginTop: 2 }}>
        {label}
      </div>
    </div>
  );
}

export function AdminOverviewPage() {
  const { t } = useI18n();
  const [data, setData] = useState<AdminOverview | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    adminApi
      .overview()
      .then(setData)
      .catch((cause: Error) => setError(cause.message))
      .finally(() => setLoading(false));
  }, []);

  if (loading) {
    return <Loading />;
  }

  return (
    <>
      <Card title={t('admin.overview', 'Admin overview')} subtitle={t('admin.overviewSub', 'Platform activity at a glance')}>
        {error ? <Alert kind="error">{error}</Alert> : null}
        {data ? (
          <div className="grid" style={{ gridTemplateColumns: 'repeat(auto-fit,minmax(140px,1fr))' }}>
            <Stat label={t('admin.totalUsers', 'Total users')} value={data.totalUsers} />
            <Stat label={t('admin.activeUsers', 'Active users')} value={data.activeUsers} />
            <Stat label={t('admin.lockedUsers', 'Locked')} value={data.lockedUsers} />
            <Stat label={t('admin.disabledUsers', 'Disabled')} value={data.disabledUsers} />
            <Stat label={t('admin.roles', 'Roles')} value={data.totalRoles} />
            <Stat label={t('admin.permissions', 'Permissions')} value={data.totalPermissions} />
            <Stat label={t('admin.clients', 'Clients')} value={data.totalClients} />
            <Stat label={t('admin.sessions', 'Active sessions')} value={data.activeSessions} />
            <Stat label={t('admin.loginsToday', 'Logins today')} value={data.loginAttemptsToday} />
            <Stat label={t('admin.failedToday', 'Failed today')} value={data.failedLoginsToday} />
          </div>
        ) : null}
      </Card>

      <Card title={t('admin.recentActivity', 'Recent activity')}>
        {!data || data.recentEvents.length === 0 ? (
          <Empty />
        ) : (
          <div className="rows">
            {data.recentEvents.map((event, index) => (
              <div className="row" key={`${event.createdAt}-${index}`}>
                <div>
                  <div className="row__title">{event.type}</div>
                  <div className="row__meta">
                    {event.description ?? '—'} · {event.actor}
                  </div>
                </div>
                <div className="row__aside">{formatDateTime(event.createdAt)}</div>
              </div>
            ))}
          </div>
        )}
      </Card>
    </>
  );
}

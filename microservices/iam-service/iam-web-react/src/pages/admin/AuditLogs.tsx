import { useCallback, useEffect, useState } from 'react';
import {
  Alert,
  Card,
  Empty,
  Loading,
  Pagination,
  formatDateTime,
} from '../../components/ui';
import { useI18n } from '../../i18n';
import { adminApi } from '../../lib/adminApi';
import type { AccountActivity, Page } from '../../lib/types';

const FILTERS = ['ALL', 'LOGIN_SUCCEEDED', 'LOGIN_FAILED', 'PASSWORD_CHANGED',
  'PASSWORD_RESET', 'PROFILE_UPDATED', 'ROLE_ASSIGNED', 'ROLE_REVOKED',
  'PERMISSION_GRANTED', 'PERMISSION_REVOKED', 'SESSION_REVOKED',
  'ACCOUNT_ENABLED', 'ACCOUNT_DISABLED', 'ACCOUNT_LOCKED', 'ACCOUNT_UNLOCKED',
  'ACCOUNT_DEACTIVATED', 'ACCOUNT_DELETED'];

export function AuditLogsPage() {
  const { t } = useI18n();
  const [data, setData] = useState<Page<AccountActivity> | null>(null);
  const [page, setPage] = useState(0);
  const [filter, setFilter] = useState('ALL');
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const load = useCallback((target: number, eventType = filter) => {
    setLoading(true);
    adminApi
      .auditLogs(target, 25, eventType)
      .then((result) => {
        setData(result);
        setPage(target);
      })
      .catch((cause: Error) => setError(cause.message))
      .finally(() => setLoading(false));
  }, [filter]);

  useEffect(() => load(0, filter), [filter, load]);

  const rows = data?.content ?? [];

  return (
    <Card
      title={t('admin.auditLogs', 'Audit logs')}
      subtitle={t('admin.auditLogsSub', 'Login events, user changes, role and permission modifications')}
    >
      {error ? <Alert kind="error">{error}</Alert> : null}

      <div className="field" style={{ maxWidth: 280 }}>
        <span className="label">{t('activity.event', 'Event')}</span>
        <select
          value={filter}
          onChange={(e) => {
            setFilter(e.target.value);
            setPage(0);
          }}
        >
          {FILTERS.map((value) => (
            <option key={value} value={value}>
              {value === 'ALL' ? t('admin.all', 'All events') : value}
            </option>
          ))}
        </select>
      </div>

      {loading ? (
        <Loading />
      ) : rows.length === 0 ? (
        <Empty />
      ) : (
        <>
          <table className="table">
            <thead>
              <tr>
                <th>{t('activity.time', 'Time')}</th>
                <th>{t('activity.event', 'Event')}</th>
                <th>Description</th>
                <th>{t('activity.actor', 'Actor')}</th>
                <th>IP</th>
              </tr>
            </thead>
            <tbody>
              {rows.map((event) => (
                <tr key={event.uuid}>
                  <td>{formatDateTime(event.createdAt)}</td>
                  <td className="mono">{event.type}</td>
                  <td>{event.description ?? '—'}</td>
                  <td>{event.actor}</td>
                  <td className="mono">{event.ipAddress ?? '—'}</td>
                </tr>
              ))}
            </tbody>
          </table>

          <Pagination
            page={page}
            totalPages={data?.page.totalPages ?? 1}
            onChange={(target) => load(target, filter)}
          />
        </>
      )}
    </Card>
  );
}

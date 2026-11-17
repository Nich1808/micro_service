import { useCallback, useEffect, useState } from 'react';
import {
  Alert,
  Card,
  Empty,
  Loading,
  Pagination,
  formatDateTime,
} from '../components/ui';
import { useI18n } from '../i18n';
import { api } from '../lib/api';
import type { LoginAttempt, Page } from '../lib/types';

export function LoginHistoryPage() {
  const { t } = useI18n();
  const [data, setData] = useState<Page<LoginAttempt> | null>(null);
  const [page, setPage] = useState(0);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const load = useCallback((target: number) => {
    setLoading(true);
    setError(null);

    api
      .loginHistory(target, 20)
      .then((result) => {
        setData(result);
        setPage(target);
      })
      .catch((cause: Error) => setError(cause.message))
      .finally(() => setLoading(false));
  }, []);

  useEffect(() => load(0), [load]);

  return (
    <Card title={t('history.title')} subtitle={t('history.subtitle')}>
      {error ? <Alert kind="error">{error}</Alert> : null}

      {loading ? (
        <Loading />
      ) : !data || data.content.length === 0 ? (
        <Empty />
      ) : (
        <>
          <table className="table">
            <thead>
              <tr>
                <th>{t('history.time')}</th>
                <th>{t('history.result')}</th>
                <th>{t('history.identifier')}</th>
                <th>{t('sessions.device')}</th>
                <th>{t('sessions.ip')}</th>
              </tr>
            </thead>
            <tbody>
              {data.content.map((attempt) => (
                <tr key={attempt.uuid}>
                  <td>{formatDateTime(attempt.attemptedAt)}</td>
                  <td>
                    {attempt.successful ? (
                      <span className="badge badge--ok">
                        {t('history.success')}
                      </span>
                    ) : (
                      <span className="badge badge--danger">
                        {t('history.failed')}
                      </span>
                    )}
                    {attempt.failureReason ? (
                      <div className="row__meta">{attempt.failureReason}</div>
                    ) : null}
                  </td>
                  <td className="mono">{attempt.identifier}</td>
                  <td>
                    {attempt.device ?? '—'} · {attempt.browser ?? '—'}
                    <div className="row__meta">
                      {attempt.operatingSystem ?? '—'}
                    </div>
                  </td>
                  <td className="mono">{attempt.ipAddress ?? '—'}</td>
                </tr>
              ))}
            </tbody>
          </table>

          <Pagination
            page={page}
            totalPages={data.page.totalPages}
            onChange={load}
          />
        </>
      )}
    </Card>
  );
}

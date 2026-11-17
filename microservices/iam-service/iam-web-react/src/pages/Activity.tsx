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
import type { AccountActivity, Page } from '../lib/types';

export function ActivityPage() {
  const { t } = useI18n();
  const [data, setData] = useState<Page<AccountActivity> | null>(null);
  const [page, setPage] = useState(0);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const load = useCallback((target: number) => {
    setLoading(true);
    setError(null);

    api
      .activity(target, 20)
      .then((result) => {
        setData(result);
        setPage(target);
      })
      .catch((cause: Error) => setError(cause.message))
      .finally(() => setLoading(false));
  }, []);

  useEffect(() => load(0), [load]);

  return (
    <Card title={t('activity.title')} subtitle={t('activity.subtitle')}>
      {error ? <Alert kind="error">{error}</Alert> : null}

      {loading ? (
        <Loading />
      ) : !data || data.content.length === 0 ? (
        <Empty />
      ) : (
        <>
          <div className="rows">
            {data.content.map((activity) => (
              <div className="row" key={activity.uuid}>
                <div>
                  <div className="row__title">{activity.type}</div>
                  <div className="row__meta">
                    {activity.description ?? '—'} · {t('activity.actor')}:{' '}
                    {activity.actor}
                  </div>
                </div>
                <div className="row__aside">
                  {formatDateTime(activity.createdAt)}
                  <div>{activity.ipAddress ?? '—'}</div>
                </div>
              </div>
            ))}
          </div>

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

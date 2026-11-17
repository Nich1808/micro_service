import { useCallback, useEffect, useState } from 'react';
import { Alert, Card, Empty, Loading, formatDateTime } from '../components/ui';
import { useI18n } from '../i18n';
import { api } from '../lib/api';
import type { UserSession } from '../lib/types';

export function SessionsPage() {
  const { t } = useI18n();
  const [sessions, setSessions] = useState<UserSession[]>([]);
  const [loading, setLoading] = useState(true);
  const [busy, setBusy] = useState<string | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [notice, setNotice] = useState<string | null>(null);

  const load = useCallback(() => {
    setLoading(true);
    api
      .sessions()
      .then(setSessions)
      .catch((cause: Error) => setError(cause.message))
      .finally(() => setLoading(false));
  }, []);

  useEffect(load, [load]);

  const terminate = async (session: UserSession) => {
    setBusy(session.uuid);
    setError(null);

    try {
      await api.terminateSession(session.uuid);
      setNotice(t('sessions.revoked'));
      load();
    } catch (cause) {
      setError(cause instanceof Error ? cause.message : 'Unable to terminate');
    } finally {
      setBusy(null);
    }
  };

  const terminateAll = async () => {
    setBusy('all');
    setError(null);

    try {
      const result = await api.terminateAllSessions();
      setNotice(`${t('sessions.revokedAll')} (${result.revoked})`);
      load();
    } catch (cause) {
      setError(cause instanceof Error ? cause.message : 'Unable to terminate');
    } finally {
      setBusy(null);
    }
  };

  if (loading) {
    return <Loading />;
  }

  return (
    <Card
      title={t('sessions.title')}
      subtitle={t('sessions.subtitle')}
      actions={
        <button
          type="button"
          className="btn btn--secondary"
          disabled={busy === 'all' || sessions.length <= 1}
          onClick={terminateAll}
        >
          {t('sessions.terminateAll')}
        </button>
      }
    >
      {error ? <Alert kind="error">{error}</Alert> : null}
      {notice ? <Alert kind="ok">{notice}</Alert> : null}

      {sessions.length === 0 ? (
        <div style={{ marginTop: 12 }}>
          <Empty />
        </div>
      ) : (
        <div className="rows" style={{ marginTop: 12 }}>
          {sessions.map((session) => (
            <div className="row" key={session.uuid}>
              <div>
                <div className="row__title">
                  {session.device ?? t('sessions.unknown')} ·{' '}
                  {session.browser ?? t('sessions.unknown')}
                  {session.current ? (
                    <span className="badge badge--brand" style={{ marginLeft: 8 }}>
                      {t('common.current')}
                    </span>
                  ) : null}
                </div>
                <div className="row__meta">
                  {session.operatingSystem ?? t('sessions.unknown')} ·{' '}
                  {t('sessions.ip')}: {session.ipAddress ?? '—'} ·{' '}
                  {t('sessions.location')}: {session.location ?? '—'}
                </div>
              </div>
              <div className="row__aside">
                <div>
                  {t('sessions.lastActive')}:{' '}
                  {formatDateTime(session.lastActivityAt)}
                </div>
                <div>
                  {t('sessions.created')}: {formatDateTime(session.createdAt)}
                </div>
                <button
                  type="button"
                  className="btn btn--sm btn--danger"
                  style={{ marginTop: 8 }}
                  disabled={session.current || busy === session.uuid}
                  onClick={() => terminate(session)}
                >
                  {t('sessions.terminate')}
                </button>
              </div>
            </div>
          ))}
        </div>
      )}
    </Card>
  );
}

import { useState } from 'react';
import { Alert, Card, Field } from '../components/ui';
import { useI18n } from '../i18n';
import { api } from '../lib/api';
import { clearTokens } from '../lib/oidc';

export function DeactivatePage() {
  const { t } = useI18n();
  const [reason, setReason] = useState('');
  const [confirmed, setConfirmed] = useState(false);
  const [busy, setBusy] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [done, setDone] = useState(false);

  const submit = async (event: React.FormEvent) => {
    event.preventDefault();
    setError(null);

    if (!confirmed) {
      setError(t('deactivate.confirm'));
      return;
    }

    setBusy(true);

    try {
      await api.deactivate(reason);
      setDone(true);
      clearTokens();
    } catch (cause) {
      setError(cause instanceof Error ? cause.message : 'Unable to deactivate');
    } finally {
      setBusy(false);
    }
  };

  return (
    <Card title={t('deactivate.title')} subtitle={t('deactivate.subtitle')}>
      {error ? <Alert kind="error">{error}</Alert> : null}

      {done ? (
        <div style={{ marginTop: 12 }}>
          <Alert kind="ok">{t('deactivate.done')}</Alert>
          <div className="actions">
            <a className="btn btn--secondary" href="/">
              {t('common.signIn')}
            </a>
          </div>
        </div>
      ) : (
        <>
          <div style={{ marginTop: 12 }}>
            <Alert kind="info">{t('deactivate.warning')}</Alert>
          </div>

          <form onSubmit={submit} style={{ marginTop: 18 }}>
            <Field label={t('deactivate.reason')}>
              <textarea
                rows={3}
                maxLength={255}
                value={reason}
                onChange={(e) => setReason(e.target.value)}
              />
            </Field>

            <label
              className="field"
              style={{ flexDirection: 'row', alignItems: 'center', gap: 10 }}
            >
              <input
                type="checkbox"
                style={{ width: 16, height: 16 }}
                checked={confirmed}
                onChange={(e) => setConfirmed(e.target.checked)}
              />
              <span>{t('deactivate.confirm')}</span>
            </label>

            <div className="actions">
              <button type="submit" className="btn btn--danger" disabled={busy}>
                {busy ? t('common.saving') : t('deactivate.submit')}
              </button>
            </div>
          </form>
        </>
      )}
    </Card>
  );
}

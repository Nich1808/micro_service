import { useState } from 'react';
import { Alert, Card, Field } from '../components/ui';
import { useI18n } from '../i18n';
import { api } from '../lib/api';

export function SecurityPage() {
  const { t } = useI18n();
  const [currentPassword, setCurrentPassword] = useState('');
  const [newPassword, setNewPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  const [busy, setBusy] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [changed, setChanged] = useState(false);

  const submit = async (event: React.FormEvent) => {
    event.preventDefault();
    setError(null);
    setChanged(false);

    if (newPassword !== confirmPassword) {
      setError(t('security.mismatch'));
      return;
    }

    setBusy(true);

    try {
      await api.changePassword({
        currentPassword,
        newPassword,
        confirmPassword,
      });

      setChanged(true);
      setCurrentPassword('');
      setNewPassword('');
      setConfirmPassword('');
    } catch (cause) {
      setError(cause instanceof Error ? cause.message : 'Unable to update');
    } finally {
      setBusy(false);
    }
  };

  return (
    <Card title={t('security.title')} subtitle={t('security.subtitle')}>
      {error ? <Alert kind="error">{error}</Alert> : null}
      {changed ? <Alert kind="ok">{t('security.changed')}</Alert> : null}

      <form onSubmit={submit} style={{ marginTop: error || changed ? 18 : 0 }}>
        <Field label={t('security.current')}>
          <input
            type="password"
            autoComplete="current-password"
            value={currentPassword}
            onChange={(e) => setCurrentPassword(e.target.value)}
            required
          />
        </Field>

        <div className="grid">
          <Field label={t('security.new')}>
            <input
              type="password"
              autoComplete="new-password"
              minLength={8}
              value={newPassword}
              onChange={(e) => setNewPassword(e.target.value)}
              required
            />
          </Field>
          <Field label={t('security.confirm')}>
            <input
              type="password"
              autoComplete="new-password"
              minLength={8}
              value={confirmPassword}
              onChange={(e) => setConfirmPassword(e.target.value)}
              required
            />
          </Field>
        </div>

        <div className="actions">
          <button type="submit" className="btn" disabled={busy}>
            {busy ? t('common.saving') : t('security.submit')}
          </button>
        </div>
      </form>
    </Card>
  );
}

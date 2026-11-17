import { useEffect, useState } from 'react';
import { Alert, Card, Field, Loading } from '../../components/ui';
import { useI18n } from '../../i18n';
import { adminApi, type SystemSettings } from '../../lib/adminApi';

export function SettingsPage() {
  const { t } = useI18n();
  const [form, setForm] = useState<SystemSettings | null>(null);
  const [loading, setLoading] = useState(true);
  const [busy, setBusy] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [saved, setSaved] = useState(false);

  useEffect(() => {
    adminApi
      .settings()
      .then(setForm)
      .catch((cause: Error) => setError(cause.message))
      .finally(() => setLoading(false));
  }, []);

  if (loading || !form) {
    return <Loading />;
  }

  const num = (key: keyof SystemSettings) => (value: string) =>
    setForm({ ...form, [key]: Number(value) } as SystemSettings);
  const bool = (key: keyof SystemSettings) => (value: boolean) =>
    setForm({ ...form, [key]: value } as SystemSettings);

  const submit = async (event: React.FormEvent) => {
    event.preventDefault();
    setBusy(true);
    setError(null);
    setSaved(false);

    try {
      const updated = await adminApi.updateSettings(form);
      setForm(updated);
      setSaved(true);
    } catch (cause) {
      setError(cause instanceof Error ? cause.message : 'Unable to save');
    } finally {
      setBusy(false);
    }
  };

  return (
    <Card
      title={t('admin.settings', 'System configuration')}
      subtitle={t('admin.settingsSub', 'Authentication policy, password policy, session expiry and token lifetimes')}
    >
      {error ? <Alert kind="error">{error}</Alert> : null}
      {saved ? <Alert kind="ok">{t('common.save') + ' ✓'}</Alert> : null}

      <form onSubmit={submit} style={{ marginTop: error || saved ? 16 : 0 }}>
        <h3 style={{ fontSize: 14, marginBottom: 10 }}>
          {t('admin.passwordPolicy', 'Password policy')}
        </h3>
        <div className="grid">
          <Field label={t('admin.minLength', 'Minimum length')}>
            <input
              type="number"
              min={1}
              value={form.passwordMinLength}
              onChange={(e) => num('passwordMinLength')(e.target.value)}
            />
          </Field>
          <Field label={t('admin.lockoutMinutes', 'Lockout (minutes)')}>
            <input
              type="number"
              min={0}
              value={form.lockoutMinutes}
              onChange={(e) => num('lockoutMinutes')(e.target.value)}
            />
          </Field>
          <Field label={t('admin.maxAttempts', 'Max failed attempts')}>
            <input
              type="number"
              min={1}
              value={form.maxFailedLoginAttempts}
              onChange={(e) => num('maxFailedLoginAttempts')(e.target.value)}
            />
          </Field>
        </div>

        <div className="grid">
          <label className="field" style={{ flexDirection: 'row', gap: 8, alignItems: 'center' }}>
            <input
              type="checkbox"
              style={{ width: 16, height: 16 }}
              checked={form.passwordRequireUppercase}
              onChange={(e) => bool('passwordRequireUppercase')(e.target.checked)}
            />
            <span>{t('admin.requireUppercase', 'Require uppercase letter')}</span>
          </label>
          <label className="field" style={{ flexDirection: 'row', gap: 8, alignItems: 'center' }}>
            <input
              type="checkbox"
              style={{ width: 16, height: 16 }}
              checked={form.passwordRequireDigit}
              onChange={(e) => bool('passwordRequireDigit')(e.target.checked)}
            />
            <span>{t('admin.requireDigit', 'Require digit')}</span>
          </label>
          <label className="field" style={{ flexDirection: 'row', gap: 8, alignItems: 'center' }}>
            <input
              type="checkbox"
              style={{ width: 16, height: 16 }}
              checked={form.passwordRequireSymbol}
              onChange={(e) => bool('passwordRequireSymbol')(e.target.checked)}
            />
            <span>{t('admin.requireSymbol', 'Require symbol')}</span>
          </label>
        </div>

        <h3 style={{ fontSize: 14, margin: '18px 0 10px' }}>
          {t('admin.sessionPolicy', 'Session and token policy')}
        </h3>
        <div className="grid">
          <Field label={t('admin.idleTimeout', 'Idle timeout (minutes)')}>
            <input
              type="number"
              min={1}
              value={form.sessionIdleTimeoutMinutes}
              onChange={(e) => num('sessionIdleTimeoutMinutes')(e.target.value)}
            />
          </Field>
          <Field label={t('admin.absoluteTimeout', 'Absolute timeout (hours)')}>
            <input
              type="number"
              min={1}
              value={form.sessionAbsoluteTimeoutHours}
              onChange={(e) => num('sessionAbsoluteTimeoutHours')(e.target.value)}
            />
          </Field>
          <Field label={t('admin.accessTtl', 'Access token TTL (minutes)')}>
            <input
              type="number"
              min={1}
              value={form.accessTokenTtlMinutes}
              onChange={(e) => num('accessTokenTtlMinutes')(e.target.value)}
            />
          </Field>
          <Field label={t('admin.refreshTtl', 'Refresh token TTL (days)')}>
            <input
              type="number"
              min={1}
              value={form.refreshTokenTtlDays}
              onChange={(e) => num('refreshTokenTtlDays')(e.target.value)}
            />
          </Field>
        </div>

        <h3 style={{ fontSize: 14, margin: '18px 0 10px' }}>
          {t('admin.features', 'Features')}
        </h3>
        <div className="grid">
          <label className="field" style={{ flexDirection: 'row', gap: 8, alignItems: 'center' }}>
            <input
              type="checkbox"
              style={{ width: 16, height: 16 }}
              checked={form.registrationEnabled}
              onChange={(e) => bool('registrationEnabled')(e.target.checked)}
            />
            <span>{t('admin.registrationEnabled', 'Self-registration enabled')}</span>
          </label>
          <label className="field" style={{ flexDirection: 'row', gap: 8, alignItems: 'center' }}>
            <input
              type="checkbox"
              style={{ width: 16, height: 16 }}
              checked={form.socialLoginEnabled}
              onChange={(e) => bool('socialLoginEnabled')(e.target.checked)}
            />
            <span>{t('admin.socialEnabled', 'Social login enabled')}</span>
          </label>
        </div>

        <div className="actions">
          <button type="submit" className="btn" disabled={busy}>
            {busy ? t('common.saving') : t('common.save')}
          </button>
        </div>
      </form>
    </Card>
  );
}

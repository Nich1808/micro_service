import { useState } from 'react';
import { useI18n } from '../i18n';
import { beginLogin } from '../lib/oidc';

export function LoginPage() {
  const { t } = useI18n();
  const [busy, setBusy] = useState(false);
  const [error, setError] = useState<string | null>(null);

  return (
    <main className="auth-stage">
      <span className="auth-stage__orb auth-stage__orb--a" />
      <span className="auth-stage__orb auth-stage__orb--b" />
      <span className="auth-stage__orb auth-stage__orb--c" />
      <span className="auth-stage__orb auth-stage__orb--d" />

      <section className="auth-panel">
        <div className="hero">
          <span className="brand">
            <img className="brand__logo" src="/images/logo.png" alt={t('app.name')} />
          </span>
          <h1 className="auth-panel__title" style={{ marginTop: 12 }}>
            {t('login.title')}
          </h1>
          <p className="subtitle auth-panel__subtitle">{t('login.subtitle')}</p>
        </div>

        {error ? (
          <div className="alert alert--error" style={{ marginTop: 18 }}>
            {error}
          </div>
        ) : null}

        <button
          type="button"
          className="btn auth-panel__submit"
          style={{ marginTop: 26 }}
          disabled={busy}
          onClick={async () => {
            setBusy(true);
            setError(null);

            try {
              await beginLogin();
            } catch {
              setError(t('callback.error'));
              setBusy(false);
            }
          }}
        >
          {t('login.action')}
        </button>
      </section>
    </main>
  );
}

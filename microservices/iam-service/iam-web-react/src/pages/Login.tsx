import { useState } from 'react';
import { useI18n } from '../i18n';
import { beginLogin } from '../lib/oidc';

export function LoginPage() {
  const { t } = useI18n();
  const [busy, setBusy] = useState(false);
  const [error, setError] = useState<string | null>(null);

  return (
    <main className="center-narrow">
      <section className="card">
        <div className="hero">
          <span className="brand">
            <span className="brand__mark">D</span>
            <span>{t('app.name')}</span>
          </span>
          <h1 style={{ marginTop: 12 }}>{t('login.title')}</h1>
          <p className="subtitle">{t('login.subtitle')}</p>
        </div>

        {error ? (
          <div className="alert alert--error" style={{ marginTop: 18 }}>
            {error}
          </div>
        ) : null}

        <button
          type="button"
          className="btn btn--block"
          style={{ marginTop: 22 }}
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

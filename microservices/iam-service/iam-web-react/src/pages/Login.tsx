import { useEffect, useRef, useState } from 'react';
import { useI18n } from '../i18n';
import { beginLogin } from '../lib/oidc';

/**
 * The dashboard has no sign-in form of its own: visitors are handed to the
 * authorization server's login page. Signing in there sends the authorization
 * code back to /callback, and the dashboard opens with a token.
 */
export function LoginPage() {
  const { t } = useI18n();
  const [failed, setFailed] = useState(false);
  const started = useRef(false);

  useEffect(() => {
    if (started.current) {
      return;
    }

    started.current = true;

    beginLogin().catch(() => setFailed(true));
  }, []);

  const signIn = () => {
    setFailed(false);
    beginLogin().catch(() => setFailed(true));
  };

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

        {failed ? (
          <>
            <div className="alert alert--error" style={{ marginTop: 18 }}>
              {t('callback.error')}
            </div>
            <button
              type="button"
              className="btn auth-panel__submit"
              style={{ marginTop: 26 }}
              onClick={signIn}
            >
              {t('login.action')}
            </button>
          </>
        ) : (
          <div className="loading" style={{ marginTop: 26 }}>
            <span className="spinner" />
            {t('login.redirecting')}
          </div>
        )}
      </section>
    </main>
  );
}

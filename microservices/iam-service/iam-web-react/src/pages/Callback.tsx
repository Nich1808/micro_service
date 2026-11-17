import { useEffect, useRef, useState } from 'react';
import { useNavigate, useSearchParams } from 'react-router-dom';
import { useI18n } from '../i18n';
import { completeLogin } from '../lib/oidc';

export function CallbackPage() {
  const { t } = useI18n();
  const [params] = useSearchParams();
  const navigate = useNavigate();
  const [error, setError] = useState<string | null>(null);
  const started = useRef(false);

  useEffect(() => {
    if (started.current) {
      return;
    }

    started.current = true;

    const code = params.get('code');
    const state = params.get('state');
    const authError = params.get('error');

    if (authError) {
      setError(params.get('error_description') ?? authError);
      return;
    }

    if (!code || !state) {
      setError(t('callback.error'));
      return;
    }

    completeLogin(code, state)
      .then(() => navigate('/profile', { replace: true }))
      .catch((cause: unknown) =>
        setError(cause instanceof Error ? cause.message : t('callback.error')),
      );
  }, [navigate, params, t]);

  return (
    <main className="center-narrow">
      <section className="card">
        {error ? (
          <>
            <div className="alert alert--error">{error}</div>
            <div className="actions">
              <a className="btn btn--secondary" href="/">
                {t('common.signIn')}
              </a>
            </div>
          </>
        ) : (
          <div className="loading">
            <span className="spinner" />
            {t('callback.working')}
          </div>
        )}
      </section>
    </main>
  );
}

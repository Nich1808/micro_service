import { useEffect, useState } from 'react';
import { useSearchParams } from 'react-router-dom';
import { Alert, Card, Empty, Loading, formatDateTime } from '../components/ui';
import { useI18n } from '../i18n';
import { api } from '../lib/api';
import { ProviderIcon, providerLabel } from '../components/ProviderIcon';
import { config } from '../lib/config';
import type { Identity } from '../lib/types';

const PROVIDERS = ['google', 'github', 'facebook', 'telegram'];

export function LinkedAccountsPage() {
  const { t } = useI18n();
  const [params, setParams] = useSearchParams();
  const [identities, setIdentities] = useState<Identity[]>([]);
  const [loading, setLoading] = useState(true);
  const [busy, setBusy] = useState<string | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [notice, setNotice] = useState<string | null>(null);

  const load = () => {
    setLoading(true);
    api
      .identities()
      .then(setIdentities)
      .catch((cause: Error) => setError(cause.message))
      .finally(() => setLoading(false));
  };

  useEffect(load, []);

  // The link flow returns from the provider with the outcome in the query string.
  useEffect(() => {
    const linkedProvider = params.get('linked');
    const failedProvider = params.get('error');

    if (!linkedProvider && !failedProvider) {
      return;
    }

    if (linkedProvider) {
      setNotice(`${linkedProvider} — ${t('accounts.linked')}`);
      load();
    } else {
      setError(t('accounts.unlinked'));
    }

    setParams({}, { replace: true });
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [params]);

  const linked = new Set(
    identities.map((identity) => identity.provider.toLowerCase()),
  );

  const unlink = async (identity: Identity) => {
    setBusy(identity.uuid);
    setError(null);

    try {
      await api.unlinkIdentity(identity.uuid);
      setNotice(t('accounts.unlinked'));
      load();
    } catch (cause) {
      setError(cause instanceof Error ? cause.message : 'Unable to unlink');
    } finally {
      setBusy(null);
    }
  };

  if (loading) {
    return <Loading />;
  }

  return (
    <Card title={t('accounts.title')} subtitle={t('accounts.subtitle')}>
      {error ? <Alert kind="error">{error}</Alert> : null}
      {notice ? <Alert kind="ok">{notice}</Alert> : null}

      {identities.length === 0 ? (
        <div style={{ marginTop: 12 }}>
          <Empty message={t('accounts.none')} />
        </div>
      ) : (
        <div className="rows" style={{ marginTop: 12 }}>
          {identities.map((identity) => (
            <div className="row" key={identity.uuid}>
              <div>
                <div className="row__title social-title">
                  <ProviderIcon provider={identity.provider} size={20} />
                  <span>{providerLabel(identity.provider)}</span>
                  <span className="badge badge--ok">
                    {t('accounts.linked')}
                  </span>
                </div>
                <div className="row__meta">
                  {identity.email ?? identity.displayName ?? '—'} ·{' '}
                  {formatDateTime(identity.linkedAt)}
                </div>
              </div>
              <button
                type="button"
                className="btn btn--sm btn--danger"
                disabled={busy === identity.uuid}
                onClick={() => unlink(identity)}
              >
                {t('accounts.unlink')}
              </button>
            </div>
          ))}
        </div>
      )}

      <div className="actions">
        {PROVIDERS.filter((provider) => !linked.has(provider)).map((provider) => (
          <a
            key={provider}
            className="btn btn--secondary btn--provider"
            href={`${config.issuer}/profile/accounts/link/${provider}`}
          >
            <ProviderIcon provider={provider} size={18} />
            <span>
              {t('accounts.link')} {providerLabel(provider)}
            </span>
          </a>
        ))}
      </div>
    </Card>
  );
}

import { useCallback, useEffect, useState } from 'react';
import { Alert, Card, Empty, Field, Loading } from '../../components/ui';
import { useI18n } from '../../i18n';
import { adminApi, type ClientRecord } from '../../lib/adminApi';

interface FormState {
  clientId: string;
  clientName: string;
  clientSecret: string;
  authenticationMethods: string;
  grantTypes: string;
  redirectUris: string;
  postLogoutRedirectUris: string;
  scopes: string;
  requireProofKey: boolean;
  requireConsent: boolean;
}

const EMPTY: FormState = {
  clientId: '',
  clientName: '',
  clientSecret: '',
  authenticationMethods: 'none',
  grantTypes: 'authorization_code, refresh_token',
  redirectUris: '',
  postLogoutRedirectUris: '',
  scopes: 'openid, profile, email',
  requireProofKey: true,
  requireConsent: true,
};

const split = (value: string) =>
  value.split(',').map((part) => part.trim()).filter(Boolean);

export function AdminClientsPage() {
  const { t } = useI18n();
  const [clients, setClients] = useState<ClientRecord[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [busy, setBusy] = useState<string | null>(null);
  const [editing, setEditing] = useState<string | null>(null);
  const [form, setForm] = useState<FormState>(EMPTY);

  const load = useCallback(() => {
    setLoading(true);
    adminApi
      .clients()
      .then(setClients)
      .catch((cause: Error) => setError(cause.message))
      .finally(() => setLoading(false));
  }, []);

  useEffect(load, [load]);

  const submit = async (event: React.FormEvent) => {
    event.preventDefault();
    setBusy('save');
    setError(null);

    const body = {
      clientId: form.clientId,
      clientName: form.clientName,
      clientSecret: form.clientSecret || null,
      authenticationMethods: split(form.authenticationMethods),
      grantTypes: split(form.grantTypes),
      redirectUris: split(form.redirectUris),
      postLogoutRedirectUris: split(form.postLogoutRedirectUris),
      scopes: split(form.scopes),
      requireProofKey: form.requireProofKey,
      requireConsent: form.requireConsent,
    };

    try {
      if (editing) {
        await adminApi.updateClient(editing, body);
      } else {
        await adminApi.createClient(body);
      }
      setEditing(null);
      setForm(EMPTY);
      load();
    } catch (cause) {
      setError(cause instanceof Error ? cause.message : 'Unable to save client');
    } finally {
      setBusy(null);
    }
  };

  const startEdit = (client: ClientRecord) => {
    setEditing(client.clientId);
    setForm({
      clientId: client.clientId,
      clientName: client.clientName,
      clientSecret: '',
      authenticationMethods: client.authenticationMethods.join(', '),
      grantTypes: client.grantTypes.join(', '),
      redirectUris: client.redirectUris.join(', '),
      postLogoutRedirectUris: client.postLogoutRedirectUris.join(', '),
      scopes: client.scopes.join(', '),
      requireProofKey: client.requireProofKey,
      requireConsent: client.requireConsent,
    });
  };

  if (loading && clients.length === 0) {
    return <Loading />;
  }

  return (
    <Card
      title={t('admin.clients', 'OAuth2 / OIDC clients')}
      subtitle={t('admin.clientsSub', 'Register, view, update and delete clients; configure credentials, grant types, redirect URIs and scopes')}
    >
      {error ? <Alert kind="error">{error}</Alert> : null}

      <form onSubmit={submit}>
        <div className="grid">
          <Field label="client_id">
            <input required disabled={editing !== null} value={form.clientId}
              onChange={(e) => setForm({ ...form, clientId: e.target.value })} />
          </Field>
          <Field label="Client name">
            <input required value={form.clientName}
              onChange={(e) => setForm({ ...form, clientName: e.target.value })} />
          </Field>
          <Field label={editing ? 'New secret (optional)' : 'Client secret'}>
            <input type="password" value={form.clientSecret}
              onChange={(e) => setForm({ ...form, clientSecret: e.target.value })} />
          </Field>
        </div>

        <div className="grid">
          <Field label="Auth methods (comma separated)">
            <input value={form.authenticationMethods}
              onChange={(e) => setForm({ ...form, authenticationMethods: e.target.value })} />
          </Field>
          <Field label="Grant types">
            <input value={form.grantTypes}
              onChange={(e) => setForm({ ...form, grantTypes: e.target.value })} />
          </Field>
          <Field label="Scopes">
            <input value={form.scopes}
              onChange={(e) => setForm({ ...form, scopes: e.target.value })} />
          </Field>
        </div>

        <Field label="Redirect URIs">
          <input value={form.redirectUris} placeholder="https://app.example.com/callback"
            onChange={(e) => setForm({ ...form, redirectUris: e.target.value })} />
        </Field>
        <Field label="Post logout redirect URIs">
          <input value={form.postLogoutRedirectUris}
            onChange={(e) => setForm({ ...form, postLogoutRedirectUris: e.target.value })} />
        </Field>

        <div className="grid">
          <label className="field" style={{ flexDirection: 'row', gap: 8, alignItems: 'center' }}>
            <input type="checkbox" style={{ width: 16, height: 16 }}
              checked={form.requireProofKey}
              onChange={(e) => setForm({ ...form, requireProofKey: e.target.checked })} />
            <span>Require PKCE</span>
          </label>
          <label className="field" style={{ flexDirection: 'row', gap: 8, alignItems: 'center' }}>
            <input type="checkbox" style={{ width: 16, height: 16 }}
              checked={form.requireConsent}
              onChange={(e) => setForm({ ...form, requireConsent: e.target.checked })} />
            <span>Require consent</span>
          </label>
        </div>

        <div className="actions">
          <button className="btn" type="submit" disabled={busy === 'save'}>
            {busy === 'save'
              ? t('common.saving')
              : editing
                ? t('admin.updateClient', 'Update client')
                : t('admin.createClient', 'Register client')}
          </button>
          {editing ? (
            <button className="btn btn--secondary" type="button"
              onClick={() => { setEditing(null); setForm(EMPTY); }}>
              {t('common.cancel')}
            </button>
          ) : null}
        </div>
      </form>

      {clients.length === 0 ? (
        <Empty />
      ) : (
        <table className="table" style={{ marginTop: 16 }}>
          <thead>
            <tr>
              <th>client_id</th>
              <th>Name</th>
              <th>Grants</th>
              <th>Scopes</th>
              <th>Type</th>
              <th style={{ textAlign: 'right' }}>Actions</th>
            </tr>
          </thead>
          <tbody>
            {clients.map((client) => (
              <tr key={client.clientId}>
                <td className="mono">{client.clientId}</td>
                <td>{client.clientName}</td>
                <td>{client.grantTypes.join(', ')}</td>
                <td>{client.scopes.join(', ')}</td>
                <td>
                  <span className={`badge ${client.confidential ? 'badge--brand' : 'badge--ok'}`}>
                    {client.confidential ? 'confidential' : 'public'}
                  </span>
                </td>
                <td>
                  <div style={{ display: 'flex', gap: 6, justifyContent: 'flex-end' }}>
                    <button className="btn btn--sm btn--secondary" type="button"
                      onClick={() => startEdit(client)}>Edit</button>
                    <button className="btn btn--sm btn--danger" type="button"
                      disabled={busy === client.clientId}
                      onClick={async () => {
                        if (!window.confirm(`Delete client ${client.clientId}?`)) {
                          return;
                        }
                        setBusy(client.clientId);
                        try {
                          await adminApi.deleteClient(client.clientId);
                          load();
                        } catch (cause) {
                          setError(cause instanceof Error ? cause.message : 'Failed');
                        } finally {
                          setBusy(null);
                        }
                      }}>
                      Delete
                    </button>
                  </div>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </Card>
  );
}

import { useEffect, useState } from 'react';
import { Alert, Card, Field, Loading, formatDateTime } from '../components/ui';
import { useI18n } from '../i18n';
import { api } from '../lib/api';
import type { UserProfile } from '../lib/types';

interface FormState {
  username: string;
  email: string;
  givenName: string;
  familyName: string;
  middleName: string;
  nickname: string;
  gender: string;
  birthdate: string;
  phoneNumber: string;
  pictureUrl: string;
  websiteUrl: string;
  zoneinfo: string;
  locale: string;
  streetAddress: string;
  locality: string;
  region: string;
  postalCode: string;
  country: string;
}

const EMPTY: FormState = {
  username: '',
  email: '',
  givenName: '',
  familyName: '',
  middleName: '',
  nickname: '',
  gender: '',
  birthdate: '',
  phoneNumber: '',
  pictureUrl: '',
  websiteUrl: '',
  zoneinfo: '',
  locale: 'en',
  streetAddress: '',
  locality: '',
  region: '',
  postalCode: '',
  country: '',
};

function toForm(profile: UserProfile): FormState {
  const [givenName = '', ...rest] = (profile.name ?? '').split(' ');

  return {
    ...EMPTY,
    username: profile.username,
    email: profile.email,
    givenName,
    familyName: rest.join(' '),
    locale: profile.locale ?? 'en',
  };
}

export function ProfilePage() {
  const { t } = useI18n();
  const [profile, setProfile] = useState<UserProfile | null>(null);
  const [form, setForm] = useState<FormState>(EMPTY);
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [saved, setSaved] = useState(false);

  useEffect(() => {
    api
      .profile()
      .then((data) => {
        setProfile(data);
        setForm(toForm(data));
      })
      .catch((cause: Error) => setError(cause.message))
      .finally(() => setLoading(false));
  }, []);

  const update = (key: keyof FormState) => (value: string) =>
    setForm((current) => ({ ...current, [key]: value }));

  const submit = async (event: React.FormEvent) => {
    event.preventDefault();
    setSaving(true);
    setError(null);
    setSaved(false);

    try {
      const body: Record<string, unknown> = {
        username: form.username || undefined,
        email: form.email || undefined,
        givenName: form.givenName || undefined,
        familyName: form.familyName || undefined,
        middleName: form.middleName || undefined,
        nickname: form.nickname || undefined,
        gender: form.gender || undefined,
        birthdate: form.birthdate || undefined,
        phoneNumber: form.phoneNumber || undefined,
        pictureUrl: form.pictureUrl || undefined,
        websiteUrl: form.websiteUrl || undefined,
        zoneinfo: form.zoneinfo || undefined,
        locale: form.locale || undefined,
        streetAddress: form.streetAddress || undefined,
        locality: form.locality || undefined,
        region: form.region || undefined,
        postalCode: form.postalCode || undefined,
        country: form.country || undefined,
      };

      const updated = await api.updateProfile(body);
      setProfile(updated);
      setSaved(true);
    } catch (cause) {
      setError(cause instanceof Error ? cause.message : 'Unable to save');
    } finally {
      setSaving(false);
    }
  };

  if (loading) {
    return <Loading />;
  }

  return (
    <>
      <Card title={t('profile.title')} subtitle={t('profile.subtitle')}>
        {error ? <Alert kind="error">{error}</Alert> : null}
        {saved ? <Alert kind="ok">{t('profile.saved')}</Alert> : null}

        {profile ? (
          <dl className="kv" style={{ marginTop: error || saved ? 18 : 0 }}>
            <dt>{t('profile.name')}</dt>
            <dd>{profile.name}</dd>
            <dt>{t('profile.emailVerified')}</dt>
            <dd>{profile.emailVerified ? t('common.yes') : t('common.no')}</dd>
            <dt>{t('profile.roles')}</dt>
            <dd>
              {profile.roles.length === 0 ? (
                <span className="badge">—</span>
              ) : (
                profile.roles.map((role) => (
                  <span key={role.uuid} className="badge badge--brand">
                    {role.name}
                  </span>
                ))
              )}
            </dd>
            <dt>Created</dt>
            <dd>{formatDateTime(profile.createdAt)}</dd>
            <dt>Updated</dt>
            <dd>{formatDateTime(profile.updatedAt)}</dd>
          </dl>
        ) : null}
      </Card>

      <form onSubmit={submit}>
        <Card title={t('profile.title')} subtitle={t('profile.subtitle')}>
          <div className="grid">
            <Field label={t('profile.username')}>
              <input
                value={form.username}
                onChange={(e) => update('username')(e.target.value)}
                maxLength={50}
              />
            </Field>
            <Field label={t('profile.email')}>
              <input
                type="email"
                value={form.email}
                onChange={(e) => update('email')(e.target.value)}
              />
            </Field>
          </div>

          <div className="grid">
            <Field label={t('profile.givenName')}>
              <input
                value={form.givenName}
                onChange={(e) => update('givenName')(e.target.value)}
              />
            </Field>
            <Field label={t('profile.familyName')}>
              <input
                value={form.familyName}
                onChange={(e) => update('familyName')(e.target.value)}
              />
            </Field>
            <Field label={t('profile.middleName')}>
              <input
                value={form.middleName}
                onChange={(e) => update('middleName')(e.target.value)}
              />
            </Field>
            <Field label={t('profile.nickname')}>
              <input
                value={form.nickname}
                onChange={(e) => update('nickname')(e.target.value)}
              />
            </Field>
          </div>

          <div className="grid">
            <Field label={t('profile.phone')}>
              <input
                type="tel"
                value={form.phoneNumber}
                onChange={(e) => update('phoneNumber')(e.target.value)}
              />
            </Field>
            <Field label={t('profile.picture')}>
              <input
                type="url"
                value={form.pictureUrl}
                onChange={(e) => update('pictureUrl')(e.target.value)}
              />
            </Field>
            <Field label={t('profile.website')}>
              <input
                type="url"
                value={form.websiteUrl}
                onChange={(e) => update('websiteUrl')(e.target.value)}
              />
            </Field>
          </div>

          <div className="grid">
            <Field label={t('profile.gender')}>
              <select
                value={form.gender}
                onChange={(e) => update('gender')(e.target.value)}
              >
                <option value="">—</option>
                <option value="female">Female</option>
                <option value="male">Male</option>
                <option value="other">Other</option>
              </select>
            </Field>
            <Field label={t('profile.birthdate')}>
              <input
                type="date"
                value={form.birthdate}
                onChange={(e) => update('birthdate')(e.target.value)}
              />
            </Field>
            <Field label={t('profile.zoneinfo')}>
              <input
                value={form.zoneinfo}
                placeholder="Asia/Phnom_Penh"
                onChange={(e) => update('zoneinfo')(e.target.value)}
              />
            </Field>
            <Field label={t('profile.locale')}>
              <select
                value={form.locale}
                onChange={(e) => update('locale')(e.target.value)}
              >
                <option value="en">English</option>
                <option value="km">ខ្មែរ</option>
              </select>
            </Field>
          </div>

          <div className="grid">
            <Field label={t('profile.street')}>
              <input
                value={form.streetAddress}
                onChange={(e) => update('streetAddress')(e.target.value)}
              />
            </Field>
            <Field label={t('profile.locality')}>
              <input
                value={form.locality}
                onChange={(e) => update('locality')(e.target.value)}
              />
            </Field>
            <Field label={t('profile.region')}>
              <input
                value={form.region}
                onChange={(e) => update('region')(e.target.value)}
              />
            </Field>
            <Field label={t('profile.postalCode')}>
              <input
                value={form.postalCode}
                onChange={(e) => update('postalCode')(e.target.value)}
              />
            </Field>
            <Field label={t('profile.country')}>
              <input
                value={form.country}
                maxLength={2}
                onChange={(e) => update('country')(e.target.value)}
              />
            </Field>
          </div>

          <div className="actions">
            <button type="submit" className="btn" disabled={saving}>
              {saving ? t('common.saving') : t('common.save')}
            </button>
          </div>
        </Card>
      </form>
    </>
  );
}

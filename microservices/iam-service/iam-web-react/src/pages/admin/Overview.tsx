import { useEffect, useMemo, useRef, useState } from 'react';
import { Alert, Empty, Loading, formatDateTime } from '../../components/ui';
import { useI18n } from '../../i18n';
import { adminApi, type AdminOverview } from '../../lib/adminApi';

/** Respect the OS-level motion preference for every animation on this page. */
function prefersReducedMotion(): boolean {
  return (
    typeof window !== 'undefined' &&
    window.matchMedia('(prefers-reduced-motion: reduce)').matches
  );
}

/**
 * Counts from 0 to the target once, after mount. Purely presentational: the final value
 * is rendered immediately when motion is reduced, and assistive tech reads the number
 * from the surrounding text, not from the animation.
 */
function useCountUp(target: number, duration = 700, delay = 0): number {
  const [value, setValue] = useState(() => (prefersReducedMotion() ? target : 0));
  const frame = useRef(0);

  useEffect(() => {
    if (prefersReducedMotion()) {
      setValue(target);
      return;
    }

    if (target === 0) {
      setValue(0);
      return;
    }

    let startedAt = 0;

    const tick = (now: number) => {
      if (!startedAt) {
        startedAt = now;
      }

      const elapsed = now - startedAt - delay;

      if (elapsed < 0) {
        frame.current = requestAnimationFrame(tick);
        return;
      }

      const progress = Math.min(elapsed / duration, 1);
      const eased = 1 - Math.pow(1 - progress, 3);

      setValue(Math.round(target * eased));

      if (progress < 1) {
        frame.current = requestAnimationFrame(tick);
      }
    };

    frame.current = requestAnimationFrame(tick);
    return () => cancelAnimationFrame(frame.current);
  }, [target, duration, delay]);

  return value;
}

function Figure({
  value,
  size = 'lg',
  unit,
}: {
  value: number;
  size?: 'xl' | 'lg' | 'md';
  unit?: string;
}) {
  const shown = useCountUp(value);

  return (
    <span className={`ov-figure ov-figure--${size}`}>
      {shown}
      {unit ? <span className="ov-unit">{unit}</span> : null}
    </span>
  );
}

/**
 * Account posture: the share of accounts that can actually sign in right now.
 * Locked and disabled are reported separately because they overlap — a single user can
 * be both, so they must not be stacked into the bar.
 */
function PostureCard({
  total,
  active,
  locked,
  disabled,
}: {
  total: number;
  active: number;
  locked: number;
  disabled: number;
}) {
  const { t } = useI18n();
  const [grown, setGrown] = useState(prefersReducedMotion());

  useEffect(() => {
    if (prefersReducedMotion()) {
      return;
    }

    const id = requestAnimationFrame(() => setGrown(true));
    return () => cancelAnimationFrame(id);
  }, []);

  const restricted = Math.max(total - active, 0);
  const pct = total > 0 ? Math.round((active / total) * 100) : 0;
  const activeWidth = total > 0 ? (active / total) * 100 : 0;

  return (
    <section className="card ov-card">
      <header className="ov-head">
        <span className="ov-eyebrow">{t('ov.posture', 'Account posture')}</span>
        <span className="badge badge--brand">{t('ov.live', 'Live')}</span>
      </header>

      <p className="ov-lede">{t('ov.postureDesc', 'Share of accounts that can sign in right now')}</p>

      <div className="ov-posture__figure">
        <Figure value={pct} size="xl" unit="%" />
        <span className="ov-posture__caption">
          {t('ov.canSignIn', 'can sign in')}
        </span>
      </div>

      <div
        className="ov-bar"
        role="img"
        aria-label={`${active} ${t('ov.active', 'active')}, ${restricted} ${t('ov.restricted', 'restricted')}`}
      >
        <span
          className="ov-bar__seg ov-bar__seg--active"
          style={{ width: grown ? `${activeWidth}%` : '0%' }}
        />
        <span
          className="ov-bar__seg ov-bar__seg--restricted"
          style={{ width: grown ? `${100 - activeWidth}%` : '0%' }}
        />
      </div>

      <ul className="ov-legend">
        <li>
          <span className="ov-dot ov-dot--active" aria-hidden="true" />
          <span className="ov-legend__label">{t('ov.active', 'Active')}</span>
          <span className="ov-legend__value">{active}</span>
        </li>
        <li>
          <span className="ov-dot ov-dot--restricted" aria-hidden="true" />
          <span className="ov-legend__label">{t('ov.restricted', 'Restricted')}</span>
          <span className="ov-legend__value">{restricted}</span>
        </li>
      </ul>

      <dl className="ov-split">
        <div>
          <dt>{t('ov.locked', 'Locked')}</dt>
          <dd>{locked}</dd>
        </div>
        <div>
          <dt>{t('ov.disabled', 'Disabled')}</dt>
          <dd>{disabled}</dd>
        </div>
        <div>
          <dt>{t('ov.totalUsers', 'Total')}</dt>
          <dd>{total}</dd>
        </div>
      </dl>
    </section>
  );
}

/** Today's sign-in split. A clean binary, so a donut is honest here. */
function SignInsCard({
  attempts,
  failed,
}: {
  attempts: number;
  failed: number;
}) {
  const { t } = useI18n();
  const [grown, setGrown] = useState(prefersReducedMotion());

  useEffect(() => {
    if (prefersReducedMotion()) {
      return;
    }

    const id = requestAnimationFrame(() => setGrown(true));
    return () => cancelAnimationFrame(id);
  }, []);

  const succeeded = Math.max(attempts - failed, 0);
  const total = succeeded + failed;
  const failedShare = total > 0 ? failed / total : 0;
  const succeededShare = total > 0 ? succeeded / total : 0;

  const radius = 54;
  const circumference = 2 * Math.PI * radius;
  const failedPct = Math.round(failedShare * 100);

  const successLen = grown ? circumference * succeededShare : 0;
  const failedLen = grown ? circumference * failedShare : 0;

  return (
    <section className="card ov-card">
      <header className="ov-head">
        <span className="ov-eyebrow">{t('ov.signIns', 'Sign-ins today')}</span>
        <span className="badge">{t('ov.today', 'Today')}</span>
      </header>

      {total === 0 ? (
        <div className="ov-donut__empty">
          <Empty message={t('ov.noSignIns', 'No sign-in attempts yet today.')} />
        </div>
      ) : (
        <div className="ov-donutwrap">
          <svg
            className="ov-donut"
            viewBox="0 0 140 140"
            role="img"
            aria-label={`${succeeded} ${t('ov.succeeded', 'succeeded')}, ${failed} ${t('ov.failed', 'failed')}`}
          >
            <circle
              cx="70"
              cy="70"
              r={radius}
              fill="none"
              stroke="var(--ov-track)"
              strokeWidth="16"
            />
            <circle
              cx="70"
              cy="70"
              r={radius}
              fill="none"
              stroke="var(--brand)"
              strokeWidth="16"
              strokeLinecap="round"
              strokeDasharray={`${successLen} ${circumference - successLen}`}
              transform="rotate(-90 70 70)"
              className="ov-donut__arc"
            />
            <circle
              cx="70"
              cy="70"
              r={radius}
              fill="none"
              stroke="var(--danger)"
              strokeWidth="16"
              strokeLinecap="round"
              strokeDasharray={`${failedLen} ${circumference - failedLen}`}
              transform={`rotate(${-90 + succeededShare * 360} 70 70)`}
              className="ov-donut__arc"
            />
            <text x="70" y="68" textAnchor="middle" className="ov-donut__value">
              {failedPct}%
            </text>
            <text x="70" y="86" textAnchor="middle" className="ov-donut__label">
              {t('ov.failed', 'failed')}
            </text>
          </svg>

          <ul className="ov-legend ov-legend--stack">
            <li>
              <span className="ov-dot ov-dot--active" aria-hidden="true" />
              <span className="ov-legend__label">{t('ov.succeeded', 'Succeeded')}</span>
              <span className="ov-legend__value">{succeeded}</span>
            </li>
            <li>
              <span className="ov-dot ov-dot--restricted" aria-hidden="true" />
              <span className="ov-legend__label">{t('ov.failed', 'Failed')}</span>
              <span className="ov-legend__value">{failed}</span>
            </li>
            <li>
              <span className="ov-dot ov-dot--muted" aria-hidden="true" />
              <span className="ov-legend__label">{t('ov.attempts', 'Attempts')}</span>
              <span className="ov-legend__value">{attempts}</span>
            </li>
          </ul>
        </div>
      )}
    </section>
  );
}

type EventTone = 'auth' | 'access' | 'session' | 'account';

/** Groups events by what they touch, so the timeline colour carries meaning. */
function toneOf(type: string): EventTone {
  if (type.startsWith('LOGIN_') || type.startsWith('PASSWORD_')
      || type.startsWith('IDENTITY_')) {
    return 'auth';
  }

  if (type.startsWith('ROLE_') || type.startsWith('PERMISSION_')) {
    return 'access';
  }

  if (type.startsWith('SESSION_')) {
    return 'session';
  }

  return 'account';
}

const TONE_LEGEND: { tone: EventTone; key: string; fallback: string }[] = [
  { tone: 'auth', key: 'ov.toneAuth', fallback: 'Sign-in & credentials' },
  { tone: 'access', key: 'ov.toneAccess', fallback: 'Roles & permissions' },
  { tone: 'session', key: 'ov.toneSession', fallback: 'Sessions' },
  { tone: 'account', key: 'ov.toneAccount', fallback: 'Account changes' },
];

export function AdminOverviewPage() {
  const { t } = useI18n();
  const [data, setData] = useState<AdminOverview | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    adminApi
      .overview()
      .then(setData)
      .catch((cause: Error) => setError(cause.message))
      .finally(() => setLoading(false));
  }, []);

  const facts = useMemo(
    () => [
      { key: 'roles', label: t('admin.roles', 'Roles'), value: data?.totalRoles ?? 0, group: 'access' },
      { key: 'permissions', label: t('admin.permissions', 'Permissions'), value: data?.totalPermissions ?? 0, group: 'access' },
      { key: 'clients', label: t('admin.clients', 'Clients'), value: data?.totalClients ?? 0, group: 'access' },
      { key: 'sessions', label: t('admin.sessions', 'Active sessions'), value: data?.activeSessions ?? 0, group: 'live' },
    ],
    [data, t],
  );

  if (loading) {
    return <Loading />;
  }

  return (
    <div className="ov">
      <header className="ov-intro">
        <span className="ov-eyebrow">{t('admin.overview', 'Admin overview')}</span>
        <h1 className="ov-title">{t('ov.title', 'Platform health')}</h1>
        <p className="subtitle">
          {t('ov.subtitle', 'Who can sign in, what they can reach, and what just happened.')}
        </p>
      </header>

      {error ? <Alert kind="error">{error}</Alert> : null}

      {data ? (
        <>
          <div className="ov-hero">
            <PostureCard
              total={data.totalUsers}
              active={data.activeUsers}
              locked={data.lockedUsers}
              disabled={data.disabledUsers}
            />
            <SignInsCard
              attempts={data.loginAttemptsToday}
              failed={data.failedLoginsToday}
            />
          </div>

          <section className="card ov-card">
            <header className="ov-head">
              <span className="ov-eyebrow">{t('ov.access', 'Access control')}</span>
            </header>

            <div className="ov-facts">
              {facts.map((fact) => (
                <div className={`ov-fact ov-fact--${fact.group}`} key={fact.key}>
                  <Figure value={fact.value} size="md" />
                  <span className="ov-fact__label">{fact.label}</span>
                </div>
              ))}
            </div>
          </section>

          <section className="card ov-card">
            <header className="ov-head">
              <span className="ov-eyebrow">{t('admin.recentActivity', 'Recent activity')}</span>
              <span className="ov-tones">
                {TONE_LEGEND.map((entry) => (
                  <span className="ov-tones__item" key={entry.tone}>
                    <span className={`ov-dot ov-dot--${entry.tone}`} aria-hidden="true" />
                    {t(entry.key, entry.fallback)}
                  </span>
                ))}
              </span>
            </header>

            {data.recentEvents.length === 0 ? (
              <Empty message={t('ov.noEvents', 'No activity recorded yet.')} />
            ) : (
              <ol className="ov-timeline">
                {data.recentEvents.map((event, index) => (
                  <li className="ov-timeline__item" key={`${event.createdAt}-${index}`}>
                    <span
                      className={`ov-dot ov-dot--${toneOf(event.type)}`}
                      aria-hidden="true"
                    />
                    <div className="ov-timeline__body">
                      <span className="ov-timeline__type">{event.type}</span>
                      <span className="ov-timeline__desc">
                        {event.description ?? '—'}
                      </span>
                    </div>
                    <span className="ov-timeline__meta">
                      {event.actor}
                      <time dateTime={event.createdAt}>
                        {formatDateTime(event.createdAt)}
                      </time>
                    </span>
                  </li>
                ))}
              </ol>
            )}
          </section>
        </>
      ) : null}
    </div>
  );
}

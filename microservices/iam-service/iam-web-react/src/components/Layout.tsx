import { NavLink, Outlet, useNavigate } from 'react-router-dom';
import { useI18n } from '../i18n';
import { config } from '../lib/config';
import { getTokens, logout } from '../lib/oidc';
import { decodeJwt } from '../lib/oidc';

/**
 * Admin navigation is only shown when the token carries an administrative authority.
 * The API enforces the same rules regardless.
 */
function canAdmin(): boolean {
  const tokens = getTokens();
  if (!tokens) {
    return false;
  }
  const claims = decodeJwt(tokens.accessToken);
  return (claims.authorities ?? []).includes('user:assign')
    || (claims.roles ?? []).includes('ADMIN');
}

const accountLinks = [
  { to: '/profile', key: 'nav.profile' },
  { to: '/security', key: 'nav.security' },
  { to: '/accounts', key: 'nav.accounts' },
  { to: '/sessions', key: 'nav.sessions' },
  { to: '/login-history', key: 'nav.history' },
  { to: '/activity', key: 'nav.activity' },
  { to: '/deactivate', key: 'nav.deactivate' },
];

const adminLinks = [
  { to: '/admin/overview', key: 'nav.overview', fallback: 'Admin overview' },
  { to: '/admin/users', key: 'nav.users', fallback: 'Users' },
  { to: '/admin/roles', key: 'nav.roles', fallback: 'Roles' },
  { to: '/admin/permissions', key: 'nav.permissions', fallback: 'Permissions' },
  { to: '/admin/clients', key: 'nav.clients', fallback: 'Clients' },
  { to: '/admin/audit-logs', key: 'nav.auditLogs', fallback: 'Audit logs' },
  { to: '/admin/settings', key: 'nav.settings', fallback: 'System configuration' },
];

export function Layout() {
  const { t, lang, setLang } = useI18n();
  const navigate = useNavigate();

  return (
    <div className="shell">
      <header className="topbar">
        <div className="topbar__inner">
          <div className="brand">
            <span className="brand__mark">D</span>
            <span>{t('app.name')}</span>
          </div>

          <div className="spacer" />

          <div className="lang-switch" role="group" aria-label={t('common.language')}>
            <button
              type="button"
              className={lang === 'en' ? 'is-active' : ''}
              onClick={() => setLang('en')}
            >
              {t('common.english')}
            </button>
            <button
              type="button"
              className={lang === 'km' ? 'is-active' : ''}
              onClick={() => setLang('km')}
            >
              {t('common.khmer')}
            </button>
          </div>

          <button
            type="button"
            className="btn btn--sm btn--secondary"
            onClick={() => {
              logout();
              navigate('/');
            }}
          >
            {t('common.signOut')}
          </button>
        </div>
      </header>

      <div className="body">
        <nav className="sidenav">
          <div className="sidenav__title">{t('nav.account')}</div>
          {accountLinks.map((link) => (
            <NavLink
              key={link.to}
              to={link.to}
              className={({ isActive }) => (isActive ? 'is-active' : '')}
            >
              {t(link.key)}
            </NavLink>
          ))}

          {canAdmin() ? (
            <>
              <div className="sidenav__title" style={{ marginTop: 10 }}>
                {t('nav.management', 'Management')}
              </div>
              {adminLinks.map((link) => (
                <NavLink
                  key={link.to}
                  to={link.to}
                  className={({ isActive }) => (isActive ? 'is-active' : '')}
                >
                  {t(link.key, link.fallback)}
                </NavLink>
              ))}
            </>
          ) : null}
        </nav>

        <main className="content">
          <Outlet />
        </main>
      </div>
    </div>
  );
}

export function useIssuer(): string {
  return config.issuer;
}

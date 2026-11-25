
/**
 * Runtime configuration. Values come from Vite env vars so deployments do not need a
 * rebuild of the source.
 */
const env = import.meta.env;

export const config = {
  // The authorization server renders its own login page, so the browser is sent
  // there directly rather than to a form inside the SPA.
  issuer: env.VITE_ISSUER ?? 'http://localhost:9026',
  clientId: env.VITE_CLIENT_ID ?? 'oidc-client',
  redirectUri: env.VITE_REDIRECT_URI ?? 'http://localhost:5173/callback',
  postLogoutRedirectUri:
    env.VITE_POST_LOGOUT_REDIRECT_URI ?? 'http://localhost:5173/',
  apiBaseUrl: env.VITE_API_BASE_URL ?? 'http://localhost:9026',
  scopes: 'openid profile email',
};

import { config } from './config';
import { createPkcePair, createState } from './pkce';

const STORAGE_KEY = 'iam.tokens';
const PKCE_KEY = 'iam.pkce';

export interface Tokens {
  accessToken: string;
  refreshToken: string | null;
  idToken: string | null;
  expiresAt: number;
}

export interface Claims {
  sub?: string;
  preferred_username?: string;
  name?: string;
  email?: string;
  roles?: string[];
  permissions?: string[];
  authorities?: string[];
}

interface TokenResponse {
  access_token: string;
  refresh_token?: string;
  id_token?: string;
  expires_in?: number;
}

export function decodeJwt(token: string): Claims {
  const payload = token.split('.')[1];

  if (!payload) {
    return {};
  }

  const normalised = payload.replace(/-/g, '+').replace(/_/g, '/');
  const padded = normalised.padEnd(
    normalised.length + ((4 - (normalised.length % 4)) % 4),
    '=',
  );

  try {
    return JSON.parse(decodeURIComponent(escape(atob(padded)))) as Claims;
  } catch {
    return {};
  }
}

export function getTokens(): Tokens | null {
  const raw = sessionStorage.getItem(STORAGE_KEY);

  if (!raw) {
    return null;
  }

  try {
    return JSON.parse(raw) as Tokens;
  } catch {
    return null;
  }
}

function storeTokens(tokens: Tokens): void {
  sessionStorage.setItem(STORAGE_KEY, JSON.stringify(tokens));
}

export function clearTokens(): void {
  sessionStorage.removeItem(STORAGE_KEY);
  sessionStorage.removeItem(PKCE_KEY);
}

export function isAuthenticated(): boolean {
  const tokens = getTokens();
  return tokens !== null && tokens.expiresAt > Date.now();
}

/**
 * Starts the authorization-code + PKCE flow.
 */
export async function beginLogin(): Promise<void> {
  const { verifier, challenge } = await createPkcePair();
  const state = createState();

  sessionStorage.setItem(PKCE_KEY, JSON.stringify({ verifier, state }));

  const params = new URLSearchParams({
    response_type: 'code',
    client_id: config.clientId,
    redirect_uri: config.redirectUri,
    scope: config.scopes,
    state,
    code_challenge: challenge,
    code_challenge_method: 'S256',
  });

  window.location.assign(`${config.issuer}/oauth2/authorize?${params.toString()}`);
}

/**
 * Completes the flow: validates the state, exchanges the code and stores the tokens.
 */
export async function completeLogin(
  code: string,
  state: string,
): Promise<void> {
  const raw = sessionStorage.getItem(PKCE_KEY);

  if (!raw) {
    throw new Error('Login session expired. Please sign in again.');
  }

  const { verifier, state: expectedState } = JSON.parse(raw) as {
    verifier: string;
    state: string;
  };

  if (state !== expectedState) {
    throw new Error('Invalid login response.');
  }

  const body = new URLSearchParams({
    grant_type: 'authorization_code',
    code,
    redirect_uri: config.redirectUri,
    client_id: config.clientId,
    code_verifier: verifier,
  });

  const response = await fetch(`${config.issuer}/oauth2/token`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
    body,
  });

  if (!response.ok) {
    throw new Error('Unable to exchange the authorization code.');
  }

  const token = (await response.json()) as TokenResponse;
  storeTokens(toTokens(token));
}

export async function refresh(): Promise<boolean> {
  const current = getTokens();

  if (!current?.refreshToken) {
    return false;
  }

  const body = new URLSearchParams({
    grant_type: 'refresh_token',
    refresh_token: current.refreshToken,
    client_id: config.clientId,
  });

  const response = await fetch(`${config.issuer}/oauth2/token`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
    body,
  });

  if (!response.ok) {
    clearTokens();
    return false;
  }

  const token = (await response.json()) as TokenResponse;
  storeTokens(toTokens(token, current.refreshToken));
  return true;
}

export function logout(): void {
  const tokens = getTokens();
  const idToken = tokens?.idToken;

  clearTokens();

  const params = new URLSearchParams({
    post_logout_redirect_uri: config.postLogoutRedirectUri,
  });

  if (idToken) {
    params.set('id_token_hint', idToken);
  }

  window.location.assign(`${config.issuer}/connect/logout?${params.toString()}`);
}

function toTokens(token: TokenResponse, fallbackRefresh?: string): Tokens {
  const lifetime = token.expires_in ?? 3600;

  return {
    accessToken: token.access_token,
    refreshToken: token.refresh_token ?? fallbackRefresh ?? null,
    idToken: token.id_token ?? null,
    expiresAt: Date.now() + lifetime * 1000 - 30_000,
  };
}

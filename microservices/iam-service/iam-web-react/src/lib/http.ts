import { config } from './config';
import { clearTokens, getTokens, refresh } from './oidc';
import type { ProblemDetail } from './types';

export class ApiError extends Error {
  readonly status: number;
  readonly problem: ProblemDetail | null;

  constructor(status: number, problem: ProblemDetail | null, message: string) {
    super(message);
    this.status = status;
    this.problem = problem;
  }
}

/**
 * Authenticated JSON request shared by the account and admin APIs.
 */
export async function apiRequest<T>(
  path: string,
  init: RequestInit = {},
  retried = false,
): Promise<T> {
  const tokens = getTokens();

  const headers = new Headers(init.headers);
  headers.set('Accept', 'application/json');

  if (init.body) {
    headers.set('Content-Type', 'application/json');
  }

  if (tokens) {
    headers.set('Authorization', `Bearer ${tokens.accessToken}`);
  }

  const response = await fetch(`${config.apiBaseUrl}${path}`, { ...init, headers });

  if (response.status === 401 && !retried && (await refresh())) {
    return apiRequest<T>(path, init, true);
  }

  if (response.status === 401) {
    clearTokens();
    throw new ApiError(401, null, 'Your session expired. Please sign in again.');
  }

  if (!response.ok) {
    throw await toApiError(response);
  }

  if (response.status === 204) {
    return undefined as T;
  }

  const text = await response.text();
  return (text ? JSON.parse(text) : undefined) as T;
}

async function toApiError(response: Response): Promise<ApiError> {
  let problem: ProblemDetail | null = null;

  try {
    problem = (await response.json()) as ProblemDetail;
  } catch {
    problem = null;
  }

  const message = problem?.errors?.length
    ? problem.errors.map((error) => `${error.field}: ${error.message}`).join(', ')
    : problem?.title ?? problem?.detail ?? response.statusText;

  return new ApiError(response.status, problem, message);
}

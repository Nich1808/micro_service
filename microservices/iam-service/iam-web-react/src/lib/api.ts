import { apiRequest } from './http';
import type {
  AccountActivity,
  Identity,
  LoginAttempt,
  Page,
  UserProfile,
  UserSession,
} from './types';

const request = apiRequest;

export const api = {
  profile: () => request<UserProfile>('/api/me'),

  updateProfile: (body: Record<string, unknown>) =>
    request<UserProfile>('/api/me', {
      method: 'PATCH',
      body: JSON.stringify(body),
    }),

  changePassword: (body: {
    currentPassword: string;
    newPassword: string;
    confirmPassword: string;
  }) =>
    request<void>('/api/me/password', {
      method: 'PUT',
      body: JSON.stringify(body),
    }),

  identities: () => request<Identity[]>('/api/me/identities'),

  unlinkIdentity: (uuid: string) =>
    request<void>(`/api/me/identities/${uuid}`, { method: 'DELETE' }),

  sessions: () => request<UserSession[]>('/api/me/sessions'),

  terminateSession: (uuid: string) =>
    request<void>(`/api/me/sessions/${uuid}`, { method: 'DELETE' }),

  terminateAllSessions: () =>
    request<{ revoked: number }>('/api/me/sessions', { method: 'DELETE' }),

  loginHistory: (page = 0, size = 20) =>
    request<Page<LoginAttempt>>(
      `/api/me/login-history?page=${page}&size=${size}`,
    ),

  activity: (page = 0, size = 20) =>
    request<Page<AccountActivity>>(`/api/me/activity?page=${page}&size=${size}`),

  deactivate: (reason: string) =>
    request<void>('/api/me/deactivate', {
      method: 'POST',
      body: JSON.stringify({ reason }),
    }),
};

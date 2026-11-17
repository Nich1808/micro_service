import { apiRequest } from './http';
import type {
  AccountActivity,
  LoginAttempt,
  Page,
  RoleSummary,
  UserProfile,
  UserSession,
} from './types';

export interface AdminOverview {
  totalUsers: number;
  activeUsers: number;
  lockedUsers: number;
  disabledUsers: number;
  totalRoles: number;
  totalPermissions: number;
  totalClients: number;
  activeSessions: number;
  loginAttemptsToday: number;
  failedLoginsToday: number;
  recentEvents: {
    type: string;
    description: string | null;
    actor: string;
    createdAt: string;
  }[];
}

export interface RoleDetail {
  uuid: string;
  name: string;
  description: string | null;
  system: boolean;
  active: boolean;
  createdAt: string;
  updatedAt: string;
}

export interface PermissionDetail {
  uuid: string;
  name: string;
  resource: string;
  action: string;
  description: string | null;
  system: boolean;
  active: boolean;
  createdAt: string;
  updatedAt: string;
}

export interface ClientRecord {
  clientId: string;
  clientName: string;
  authenticationMethods: string[];
  grantTypes: string[];
  redirectUris: string[];
  postLogoutRedirectUris: string[];
  scopes: string[];
  requireProofKey: boolean;
  requireConsent: boolean;
  accessTokenTtlSeconds: number | null;
  refreshTokenTtlSeconds: number | null;
  confidential: boolean;
}

export interface SystemSettings {
  passwordMinLength: number;
  passwordRequireUppercase: boolean;
  passwordRequireDigit: boolean;
  passwordRequireSymbol: boolean;
  maxFailedLoginAttempts: number;
  lockoutMinutes: number;
  sessionIdleTimeoutMinutes: number;
  sessionAbsoluteTimeoutHours: number;
  accessTokenTtlMinutes: number;
  refreshTokenTtlDays: number;
  registrationEnabled: boolean;
  socialLoginEnabled: boolean;
}

export interface UserSummaryRow {
  uuid: string;
  username: string;
  email: string;
  name: string;
  enabled: boolean;
  createdAt: string;
}

const q = (page: number, size = 20) => `?page=${page}&size=${size}`;

export const adminApi = {
  overview: () => apiRequest<AdminOverview>('/api/admin/overview'),
  auditLogs: (page = 0, size = 20, type?: string) =>
    apiRequest<Page<AccountActivity>>(
      `/api/admin/audit-logs${q(page, size)}${type && type !== 'ALL' ? `&type=${type}` : ''}`,
    ),

  users: (page = 0, size = 20) =>
    apiRequest<Page<UserSummaryRow>>(`/api/users${q(page, size)}&sort=createdAt,desc`),
  user: (uuid: string) => apiRequest<UserProfile>(`/api/users/${uuid}`),
  createUser: (body: Record<string, unknown>) =>
    apiRequest<UserProfile>('/api/users', {
      method: 'POST',
      body: JSON.stringify(body),
    }),
  updateUser: (uuid: string, body: Record<string, unknown>) =>
    apiRequest<UserProfile>(`/api/users/${uuid}`, {
      method: 'PATCH',
      body: JSON.stringify(body),
    }),
  deleteUser: (uuid: string) =>
    apiRequest<void>(`/api/users/${uuid}`, { method: 'DELETE' }),
  enableUser: (uuid: string) =>
    apiRequest<void>(`/api/users/${uuid}/enable`, { method: 'POST' }),
  disableUser: (uuid: string) =>
    apiRequest<void>(`/api/users/${uuid}/disable`, { method: 'POST' }),
  lockUser: (uuid: string) =>
    apiRequest<void>(`/api/users/${uuid}/lock`, { method: 'POST' }),
  unlockUser: (uuid: string) =>
    apiRequest<void>(`/api/users/${uuid}/unlock`, { method: 'POST' }),
  resetPassword: (uuid: string, newPassword: string) =>
    apiRequest<void>(`/api/users/${uuid}/password`, {
      method: 'PUT',
      body: JSON.stringify({ newPassword }),
    }),
  assignRoles: (uuid: string, roleUuids: string[], reason?: string) =>
    apiRequest<unknown>(`/api/users/${uuid}/roles`, {
      method: 'POST',
      body: JSON.stringify({ roleUuids, reason }),
    }),
  revokeRole: (uuid: string, roleUuid: string, reason?: string) =>
    apiRequest<void>(
      `/api/users/${uuid}/roles/${roleUuid}${reason ? `?reason=${encodeURIComponent(reason)}` : ''}`,
      { method: 'DELETE' },
    ),
  grantPermissions: (
    uuid: string,
    permissionUuids: string[],
    effect: 'GRANT' | 'DENY',
    reason?: string,
  ) =>
    apiRequest<unknown>(`/api/users/${uuid}/permissions`, {
      method: 'POST',
      body: JSON.stringify({ permissionUuids, effect, reason }),
    }),
  revokePermission: (uuid: string, permissionUuid: string, reason?: string) =>
    apiRequest<void>(
      `/api/users/${uuid}/permissions/${permissionUuid}${reason ? `?reason=${encodeURIComponent(reason)}` : ''}`,
      { method: 'DELETE' },
    ),
  userSessions: (uuid: string) =>
    apiRequest<UserSession[]>(`/api/users/${uuid}/sessions`),
  terminateUserSession: (uuid: string, sessionUuid: string) =>
    apiRequest<void>(`/api/users/${uuid}/sessions/${sessionUuid}`, {
      method: 'DELETE',
    }),
  terminateAllUserSessions: (uuid: string) =>
    apiRequest<{ revoked: number }>(`/api/users/${uuid}/sessions`, {
      method: 'DELETE',
    }),
  userAuthorities: (uuid: string) =>
    apiRequest<{
      roles: { uuid: string; name: string; expiresAt: string | null }[];
      directPermissions: { name: string; effect: 'GRANT' | 'DENY' }[];
      effectivePermissions: string[];
    }>(`/api/users/${uuid}/authorities`),
  userLoginHistory: (uuid: string, page = 0, size = 20) =>
    apiRequest<Page<LoginAttempt>>(
      `/api/users/${uuid}/login-history${q(page, size)}`,
    ),

  roles: (page = 0, size = 100) =>
    apiRequest<Page<RoleSummary>>(`/api/roles${q(page, size)}`),
  rolesDetail: async (): Promise<RoleDetail[]> => {
    const page = await apiRequest<Page<RoleDetail>>(`/api/roles${q(0, 100)}`);
    return page.content;
  },
  createRole: (body: Record<string, unknown>) =>
    apiRequest<RoleDetail>('/api/roles', {
      method: 'POST',
      body: JSON.stringify(body),
    }),
  updateRole: (uuid: string, body: Record<string, unknown>) =>
    apiRequest<RoleDetail>(`/api/roles/${uuid}`, {
      method: 'PATCH',
      body: JSON.stringify(body),
    }),
  deleteRole: (uuid: string) =>
    apiRequest<void>(`/api/roles/${uuid}`, { method: 'DELETE' }),
  activateRole: (uuid: string) =>
    apiRequest<void>(`/api/roles/${uuid}/activate`, { method: 'POST' }),
  deactivateRole: (uuid: string) =>
    apiRequest<void>(`/api/roles/${uuid}/deactivate`, { method: 'POST' }),
  assignPermissionsToRole: (
    uuid: string,
    permissionUuids: string[],
    reason?: string,
  ) =>
    apiRequest<unknown>(`/api/roles/${uuid}/permissions`, {
      method: 'POST',
      body: JSON.stringify({ permissionUuids, reason }),
    }),
  revokePermissionFromRole: (
    uuid: string,
    permissionUuid: string,
    reason?: string,
  ) =>
    apiRequest<void>(
      `/api/roles/${uuid}/permissions/${permissionUuid}${reason ? `?reason=${encodeURIComponent(reason)}` : ''}`,
      { method: 'DELETE' },
    ),

  permissions: (page = 0, size = 100) =>
    apiRequest<Page<PermissionDetail>>(`/api/permissions${q(page, size)}`),
  createPermission: (body: Record<string, unknown>) =>
    apiRequest<PermissionDetail>('/api/permissions', {
      method: 'POST',
      body: JSON.stringify(body),
    }),
  updatePermission: (uuid: string, body: Record<string, unknown>) =>
    apiRequest<PermissionDetail>(`/api/permissions/${uuid}`, {
      method: 'PATCH',
      body: JSON.stringify(body),
    }),
  deletePermission: (uuid: string) =>
    apiRequest<void>(`/api/permissions/${uuid}`, { method: 'DELETE' }),
  activatePermission: (uuid: string) =>
    apiRequest<void>(`/api/permissions/${uuid}/activate`, { method: 'POST' }),
  deactivatePermission: (uuid: string) =>
    apiRequest<void>(`/api/permissions/${uuid}/deactivate`, { method: 'POST' }),

  clients: () => apiRequest<ClientRecord[]>('/api/admin/clients'),
  createClient: (body: Record<string, unknown>) =>
    apiRequest<ClientRecord>('/api/admin/clients', {
      method: 'POST',
      body: JSON.stringify(body),
    }),
  updateClient: (clientId: string, body: Record<string, unknown>) =>
    apiRequest<ClientRecord>(`/api/admin/clients/${clientId}`, {
      method: 'PUT',
      body: JSON.stringify(body),
    }),
  deleteClient: (clientId: string) =>
    apiRequest<void>(`/api/admin/clients/${clientId}`, { method: 'DELETE' }),

  settings: () => apiRequest<SystemSettings>('/api/admin/settings'),
  updateSettings: (body: Partial<SystemSettings>) =>
    apiRequest<SystemSettings>('/api/admin/settings', {
      method: 'PATCH',
      body: JSON.stringify(body),
    }),
};

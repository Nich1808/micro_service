export interface RoleSummary {
  uuid: string;
  name: string;
  active: boolean;
}

export interface UserProfile {
  uuid: string;
  username: string;
  email: string;
  emailVerified: boolean;
  name: string;
  locale: string;
  enabled: boolean;
  roles: RoleSummary[];
  createdAt: string;
  updatedAt: string;
}

export interface Identity {
  uuid: string;
  provider: string;
  email: string | null;
  displayName: string | null;
  linkedAt: string;
}

export interface UserSession {
  uuid: string;
  device: string | null;
  browser: string | null;
  operatingSystem: string | null;
  ipAddress: string | null;
  location: string | null;
  createdAt: string;
  lastActivityAt: string;
  expiresAt: string | null;
  current: boolean;
}

export interface LoginAttempt {
  uuid: string;
  successful: boolean;
  failureReason: string | null;
  identifier: string;
  ipAddress: string | null;
  device: string | null;
  browser: string | null;
  operatingSystem: string | null;
  location: string | null;
  attemptedAt: string;
  suspicious: boolean;
}

export type ActivityType =
  | 'PASSWORD_CHANGED'
  | 'PASSWORD_RESET'
  | 'PROFILE_UPDATED'
  | 'IDENTITY_LINKED'
  | 'IDENTITY_UNLINKED'
  | 'ROLE_ASSIGNED'
  | 'ROLE_REVOKED'
  | 'PERMISSION_GRANTED'
  | 'PERMISSION_REVOKED'
  | 'ACCOUNT_ENABLED'
  | 'ACCOUNT_DISABLED'
  | 'ACCOUNT_LOCKED'
  | 'ACCOUNT_UNLOCKED'
  | 'ACCOUNT_DEACTIVATED'
  | 'SESSION_REVOKED'
  | 'SESSION_REVOKED_ALL'
  | 'LOGIN_SUCCEEDED'
  | 'LOGIN_FAILED';

export interface AccountActivity {
  uuid: string;
  type: ActivityType;
  description: string | null;
  ipAddress: string | null;
  userAgent: string | null;
  actor: string;
  createdAt: string;
}

export interface Page<T> {
  content: T[];
  page: {
    number: number;
    size: number;
    totalElements: number;
    totalPages: number;
  };
}

export interface ProblemDetail {
  type?: string;
  title?: string;
  status?: number;
  detail?: string;
  errors?: { field: string; message: string }[];
}

package kh.edu.istad.platform.iamdomaincore.enums;

/**
 * Security-relevant account events shown in the user's activity feed and in audit logs.
 */
public enum ActivityType {
    PASSWORD_CHANGED,
    PASSWORD_RESET,
    PROFILE_UPDATED,
    IDENTITY_LINKED,
    IDENTITY_UNLINKED,
    ROLE_ASSIGNED,
    ROLE_REVOKED,
    PERMISSION_GRANTED,
    PERMISSION_REVOKED,
    ACCOUNT_ENABLED,
    ACCOUNT_DISABLED,
    ACCOUNT_LOCKED,
    ACCOUNT_UNLOCKED,
    ACCOUNT_DEACTIVATED,
    SESSION_REVOKED,
    SESSION_REVOKED_ALL,
    LOGIN_SUCCEEDED,
    LOGIN_FAILED
}

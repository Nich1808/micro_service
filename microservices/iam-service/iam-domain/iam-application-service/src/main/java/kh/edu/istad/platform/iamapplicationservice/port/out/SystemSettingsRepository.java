package kh.edu.istad.platform.iamapplicationservice.port.out;

/**
 * Persisted security configuration. A single row holds the active policy, so the
 * authorization server and the password flows read one consistent set of values.
 */
public interface SystemSettingsRepository {

    SystemSettings load();

    SystemSettings save(SystemSettings settings);

    record SystemSettings(
            int passwordMinLength,
            boolean passwordRequireUppercase,
            boolean passwordRequireDigit,
            boolean passwordRequireSymbol,
            int maxFailedLoginAttempts,
            int lockoutMinutes,
            int sessionIdleTimeoutMinutes,
            int sessionAbsoluteTimeoutHours,
            long accessTokenTtlMinutes,
            long refreshTokenTtlDays,
            boolean registrationEnabled,
            boolean socialLoginEnabled
    ) {
        public static SystemSettings defaults() {
            return new SystemSettings(
                    8, true, true, false,
                    5, 15,
                    60, 12,
                    60, 7,
                    true, true
            );
        }
    }
}

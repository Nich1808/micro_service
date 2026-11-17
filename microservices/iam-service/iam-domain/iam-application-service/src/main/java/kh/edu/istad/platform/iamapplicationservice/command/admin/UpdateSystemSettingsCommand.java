package kh.edu.istad.platform.iamapplicationservice.command.admin;

public record UpdateSystemSettingsCommand(
        Integer passwordMinLength,
        Boolean passwordRequireUppercase,
        Boolean passwordRequireDigit,
        Boolean passwordRequireSymbol,
        Integer maxFailedLoginAttempts,
        Integer lockoutMinutes,
        Integer sessionIdleTimeoutMinutes,
        Integer sessionAbsoluteTimeoutHours,
        Long accessTokenTtlMinutes,
        Long refreshTokenTtlDays,
        Boolean registrationEnabled,
        Boolean socialLoginEnabled
) {
}

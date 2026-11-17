package kh.edu.istad.platform.iamapplicationservice.service.admin;

import kh.edu.istad.platform.iamapplicationservice.command.admin.UpdateSystemSettingsCommand;
import kh.edu.istad.platform.iamapplicationservice.port.out.SystemSettingsRepository;
import kh.edu.istad.platform.iamapplicationservice.usecase.admin.AdminUseCases;

/**
 * Reads and updates the security policy. Every field is optional on update so the admin
 * UI can patch a single setting.
 */
public final class SystemSettingsServices {

    private SystemSettingsServices() {
    }

    public static final class Get implements AdminUseCases.GetSystemSettings {

        private final SystemSettingsRepository repository;

        public Get(SystemSettingsRepository repository) {
            this.repository = repository;
        }

        @Override
        public SystemSettingsRepository.SystemSettings execute() {
            return repository.load();
        }
    }

    public static final class Update implements AdminUseCases.UpdateSystemSettings {

        private final SystemSettingsRepository repository;

        public Update(SystemSettingsRepository repository) {
            this.repository = repository;
        }

        @Override
        public SystemSettingsRepository.SystemSettings execute(
                UpdateSystemSettingsCommand command
        ) {
            if (command == null) {
                throw new IllegalArgumentException("Settings command is required");
            }

            SystemSettingsRepository.SystemSettings current = repository.load();

            SystemSettingsRepository.SystemSettings next =
                    new SystemSettingsRepository.SystemSettings(
                            positive(command.passwordMinLength(),
                                    current.passwordMinLength(), "passwordMinLength"),
                            command.passwordRequireUppercase() != null
                                    ? command.passwordRequireUppercase()
                                    : current.passwordRequireUppercase(),
                            command.passwordRequireDigit() != null
                                    ? command.passwordRequireDigit()
                                    : current.passwordRequireDigit(),
                            command.passwordRequireSymbol() != null
                                    ? command.passwordRequireSymbol()
                                    : current.passwordRequireSymbol(),
                            positive(command.maxFailedLoginAttempts(),
                                    current.maxFailedLoginAttempts(),
                                    "maxFailedLoginAttempts"),
                            nonNegative(command.lockoutMinutes(),
                                    current.lockoutMinutes(), "lockoutMinutes"),
                            positive(command.sessionIdleTimeoutMinutes(),
                                    current.sessionIdleTimeoutMinutes(),
                                    "sessionIdleTimeoutMinutes"),
                            positive(command.sessionAbsoluteTimeoutHours(),
                                    current.sessionAbsoluteTimeoutHours(),
                                    "sessionAbsoluteTimeoutHours"),
                            positiveLong(command.accessTokenTtlMinutes(),
                                    current.accessTokenTtlMinutes(),
                                    "accessTokenTtlMinutes"),
                            positiveLong(command.refreshTokenTtlDays(),
                                    current.refreshTokenTtlDays(),
                                    "refreshTokenTtlDays"),
                            command.registrationEnabled() != null
                                    ? command.registrationEnabled()
                                    : current.registrationEnabled(),
                            command.socialLoginEnabled() != null
                                    ? command.socialLoginEnabled()
                                    : current.socialLoginEnabled()
                    );

            return repository.save(next);
        }

        private static int positive(Integer value, int fallback, String field) {
            if (value == null) {
                return fallback;
            }

            if (value < 1) {
                throw new IllegalArgumentException(field + " must be at least 1");
            }

            return value;
        }

        private static int nonNegative(Integer value, int fallback, String field) {
            if (value == null) {
                return fallback;
            }

            if (value < 0) {
                throw new IllegalArgumentException(field + " cannot be negative");
            }

            return value;
        }

        private static long positiveLong(Long value, long fallback, String field) {
            if (value == null) {
                return fallback;
            }

            if (value < 1) {
                throw new IllegalArgumentException(field + " must be at least 1");
            }

            return value;
        }
    }
}

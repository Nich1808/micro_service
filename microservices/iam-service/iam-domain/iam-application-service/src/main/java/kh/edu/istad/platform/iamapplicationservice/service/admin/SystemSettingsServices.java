package kh.edu.istad.platform.iamapplicationservice.service.admin;

import kh.edu.istad.platform.iamapplicationservice.command.admin.UpdateSystemSettingsCommand;
import kh.edu.istad.platform.iamapplicationservice.port.out.ClientAdminRepository;
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
        private final ClientAdminRepository clientAdminRepository;

        public Update(
                SystemSettingsRepository repository,
                ClientAdminRepository clientAdminRepository
        ) {
            this.repository = repository;
            this.clientAdminRepository = clientAdminRepository;
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

            SystemSettingsRepository.SystemSettings saved = repository.save(next);

            applyTokenLifetimes(saved);

            return saved;
        }

        /**
         * Token lifetimes are enforced by the authorization server per client, so the new
         * policy is pushed onto every registered client; otherwise the setting would look
         * saved while issued tokens kept the old lifetime.
         */
        private void applyTokenLifetimes(
                SystemSettingsRepository.SystemSettings settings
        ) {
            if (clientAdminRepository == null) {
                return;
            }

            long accessSeconds = settings.accessTokenTtlMinutes() * 60;
            long refreshSeconds = settings.refreshTokenTtlDays() * 86_400;

            for (ClientAdminRepository.ClientRecord client
                    : clientAdminRepository.findAll()) {
                Long currentAccess = client.accessTokenTtlSeconds();
                Long currentRefresh = client.refreshTokenTtlSeconds();

                if (currentAccess != null && currentAccess == accessSeconds
                        && currentRefresh != null && currentRefresh == refreshSeconds) {
                    continue;
                }

                clientAdminRepository.save(new ClientAdminRepository.ClientRecord(
                        client.internalId(),
                        client.clientId(),
                        client.clientName(),
                        client.authenticationMethods(),
                        client.grantTypes(),
                        client.redirectUris(),
                        client.postLogoutRedirectUris(),
                        client.scopes(),
                        client.requireProofKey(),
                        client.requireConsent(),
                        accessSeconds,
                        refreshSeconds,
                        client.clientSecretHash()
                ));
            }
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

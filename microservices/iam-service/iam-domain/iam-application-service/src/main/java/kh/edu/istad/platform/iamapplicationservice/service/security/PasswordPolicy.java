package kh.edu.istad.platform.iamapplicationservice.service.security;

import kh.edu.istad.platform.iamapplicationservice.port.out.SystemSettingsRepository;

/**
 * Applies the password rules configured in the admin console. Kept as a collaborator so
 * every use case that sets a password (register, create, reset, change) enforces the same
 * policy instead of duplicating it.
 */
public final class PasswordPolicy {

    private final SystemSettingsRepository settingsRepository;

    public PasswordPolicy(SystemSettingsRepository settingsRepository) {
        this.settingsRepository = settingsRepository;
    }

    /**
     * @throws IllegalArgumentException when the password violates the configured policy
     */
    public void validate(String rawPassword) {
        if (rawPassword == null || rawPassword.isBlank()) {
            throw new IllegalArgumentException("Password is required");
        }

        SystemSettingsRepository.SystemSettings settings =
                settingsRepository.load();

        if (rawPassword.length() < settings.passwordMinLength()) {
            throw new IllegalArgumentException(
                    "Password must be at least " + settings.passwordMinLength()
                            + " characters"
            );
        }

        if (settings.passwordRequireUppercase()
                && rawPassword.chars().noneMatch(Character::isUpperCase)) {
            throw new IllegalArgumentException(
                    "Password must contain an uppercase letter"
            );
        }

        if (settings.passwordRequireDigit()
                && rawPassword.chars().noneMatch(Character::isDigit)) {
            throw new IllegalArgumentException("Password must contain a digit");
        }

        if (settings.passwordRequireSymbol()
                && rawPassword.chars().allMatch(Character::isLetterOrDigit)) {
            throw new IllegalArgumentException(
                    "Password must contain a symbol"
            );
        }
    }
}

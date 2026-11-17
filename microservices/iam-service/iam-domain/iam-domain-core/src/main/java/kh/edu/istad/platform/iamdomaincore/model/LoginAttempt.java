package kh.edu.istad.platform.iamdomaincore.model;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * A single sign-in attempt. Failed attempts are kept even when no user matches the
 * submitted identifier, which is what makes brute-force detection possible.
 */
public record LoginAttempt(
        Long id,
        UUID uuid,
        Long userId,
        String identifier,
        boolean successful,
        String failureReason,
        String ipAddress,
        String device,
        String browser,
        String operatingSystem,
        String location,
        String userAgent,
        OffsetDateTime attemptedAt
) {

    public LoginAttempt {
        if (uuid == null) {
            uuid = UUID.randomUUID();
        }

        if (identifier == null || identifier.isBlank()) {
            throw new IllegalArgumentException("Identifier is required");
        }

        if (attemptedAt == null) {
            throw new IllegalArgumentException("Attempt time is required");
        }

        if (!successful && (failureReason == null || failureReason.isBlank())) {
            failureReason = "INVALID_CREDENTIALS";
        }
    }
}

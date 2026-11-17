package kh.edu.istad.platform.iamapplicationservice.result.security;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * One sign-in attempt. {@code suspicious} marks a failure that belongs to a burst of
 * failed attempts, which is what an administrator needs to notice quickly.
 */
public record LoginAttemptResult(
        UUID uuid,
        boolean successful,
        String failureReason,
        String identifier,
        String ipAddress,
        String device,
        String browser,
        String operatingSystem,
        String location,
        OffsetDateTime attemptedAt,
        boolean suspicious
) {
}

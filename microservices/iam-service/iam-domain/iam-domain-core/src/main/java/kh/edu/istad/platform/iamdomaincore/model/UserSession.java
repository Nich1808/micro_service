package kh.edu.istad.platform.iamdomaincore.model;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * One signed-in device for a user. A session is terminated by revoking it, never by
 * deleting the row, so the history stays auditable.
 */
public record UserSession(
        Long id,
        UUID uuid,
        Long userId,
        String sessionKey,
        String device,
        String browser,
        String operatingSystem,
        String ipAddress,
        String location,
        String userAgent,
        OffsetDateTime createdAt,
        OffsetDateTime lastActivityAt,
        OffsetDateTime expiresAt,
        OffsetDateTime revokedAt,
        String revokedBy,
        String revokedReason
) {

    public UserSession {
        if (uuid == null) {
            uuid = UUID.randomUUID();
        }

        if (userId == null || userId <= 0) {
            throw new IllegalArgumentException("User ID is required");
        }

        if (sessionKey == null || sessionKey.isBlank()) {
            throw new IllegalArgumentException("Session key is required");
        }

        if (createdAt == null) {
            throw new IllegalArgumentException("Created time is required");
        }

        if (lastActivityAt == null) {
            lastActivityAt = createdAt;
        }

        if ((revokedAt == null) != (revokedBy == null)) {
            throw new IllegalArgumentException(
                    "Revocation time and actor must both be provided"
            );
        }
    }

    public boolean isActiveAt(OffsetDateTime now) {
        return revokedAt == null
                && (expiresAt == null || expiresAt.isAfter(now));
    }

    public UserSession touch(OffsetDateTime now) {
        if (!isActiveAt(now)) {
            return this;
        }

        return new UserSession(
                id, uuid, userId, sessionKey,
                device, browser, operatingSystem,
                ipAddress, location, userAgent,
                createdAt, now, expiresAt,
                revokedAt, revokedBy, revokedReason
        );
    }

    public UserSession revoke(
            String actor,
            OffsetDateTime now,
            String reason
    ) {
        if (revokedAt != null) {
            throw new IllegalStateException("Session is already revoked");
        }

        if (actor == null || actor.isBlank()) {
            throw new IllegalArgumentException("Actor is required");
        }

        if (now == null) {
            throw new IllegalArgumentException("Timestamp is required");
        }

        return new UserSession(
                id, uuid, userId, sessionKey,
                device, browser, operatingSystem,
                ipAddress, location, userAgent,
                createdAt, lastActivityAt, expiresAt,
                now, actor, reason
        );
    }
}

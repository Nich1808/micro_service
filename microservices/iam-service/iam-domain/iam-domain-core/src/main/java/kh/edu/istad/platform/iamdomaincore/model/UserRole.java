package kh.edu.istad.platform.iamdomaincore.model;

import java.time.OffsetDateTime;

public record UserRole(
        Long id,
        Long userId,
        Long roleId,
        OffsetDateTime assignedAt,
        String assignedBy,
        OffsetDateTime expiresAt,
        OffsetDateTime revokedAt,
        String revokedBy,
        String reason
) {

    public UserRole {
        if (userId == null || userId <= 0) {
            throw new IllegalArgumentException("User ID is required");
        }

        if (roleId == null || roleId <= 0) {
            throw new IllegalArgumentException("Role ID is required");
        }

        validateAssignment(assignedAt, assignedBy, expiresAt);
        validateRevocation(revokedAt, revokedBy);
        validateReason(reason);
    }

    public boolean isActiveAt(OffsetDateTime now) {
        return revokedAt == null
                && (expiresAt == null || expiresAt.isAfter(now));
    }

    public UserRole revoke(String actor, OffsetDateTime now, String reason) {
        if (revokedAt != null) {
            throw new IllegalStateException("Role assignment is already revoked");
        }

        if (actor == null || actor.isBlank() || now == null) {
            throw new IllegalArgumentException("Actor and timestamp are required");
        }

        if (now.isBefore(assignedAt)) {
            throw new IllegalArgumentException("Revocation cannot precede assignment");
        }

        return new UserRole(
                id, userId, roleId,
                assignedAt, assignedBy, expiresAt,
                now, actor, reason
        );
    }

    private static void validateAssignment(
            OffsetDateTime assignedAt,
            String assignedBy,
            OffsetDateTime expiresAt
    ) {
        if (assignedAt == null || assignedBy == null || assignedBy.isBlank()) {
            throw new IllegalArgumentException("Assignment time and actor are required");
        }

        if (expiresAt != null && !expiresAt.isAfter(assignedAt)) {
            throw new IllegalArgumentException("Expiry must be after assignment");
        }
    }

    private static void validateRevocation(
            OffsetDateTime revokedAt,
            String revokedBy
    ) {
        if ((revokedAt == null) != (revokedBy == null)) {
            throw new IllegalArgumentException("Revocation time and actor must both be provided");
        }

        if (revokedBy != null && revokedBy.isBlank()) {
            throw new IllegalArgumentException("Revocation actor cannot be blank");
        }
    }

    private static void validateReason(String reason) {
        if (reason != null && reason.length() > 255) {
            throw new IllegalArgumentException("Reason must not exceed 255 characters");
        }
    }
}
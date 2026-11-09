package kh.edu.istad.platform.iamdomaincore.model;

import kh.edu.istad.platform.iamdomaincore.enums.PermissionEffect;

import java.time.OffsetDateTime;

public record UserPermission(
        Long id,
        Long userId,
        Long permissionId,
        PermissionEffect effect,
        OffsetDateTime assignedAt,
        String assignedBy,
        OffsetDateTime expiresAt,
        OffsetDateTime revokedAt,
        String revokedBy,
        String reason
) {

    public UserPermission {
        if (userId == null || userId <= 0) {
            throw new IllegalArgumentException("User ID is required");
        }

        if (permissionId == null || permissionId <= 0) {
            throw new IllegalArgumentException("Permission ID is required");
        }

        if (effect == null) {
            throw new IllegalArgumentException("Permission effect is required");
        }

        if (assignedAt == null || assignedBy == null || assignedBy.isBlank()) {
            throw new IllegalArgumentException("Assignment time and actor are required");
        }

        if (expiresAt != null && !expiresAt.isAfter(assignedAt)) {
            throw new IllegalArgumentException("Expiry must be after assignment");
        }

        if ((revokedAt == null) != (revokedBy == null)) {
            throw new IllegalArgumentException("Revocation time and actor must both be provided");
        }

        if (revokedBy != null && revokedBy.isBlank()) {
            throw new IllegalArgumentException("Revocation actor cannot be blank");
        }

        if (reason != null && reason.length() > 255) {
            throw new IllegalArgumentException("Reason must not exceed 255 characters");
        }
    }

    public boolean isActiveAt(OffsetDateTime now) {
        return revokedAt == null
                && (expiresAt == null || expiresAt.isAfter(now));
    }

    public UserPermission revoke(
            String actor,
            OffsetDateTime now,
            String reason
    ) {
        if (revokedAt != null) {
            throw new IllegalStateException("Permission assignment is already revoked");
        }

        if (actor == null || actor.isBlank() || now == null) {
            throw new IllegalArgumentException("Actor and timestamp are required");
        }

        if (now.isBefore(assignedAt)) {
            throw new IllegalArgumentException("Revocation cannot precede assignment");
        }

        return new UserPermission(
                id, userId, permissionId, effect,
                assignedAt, assignedBy, expiresAt,
                now, actor, reason
        );
    }
}
package kh.edu.istad.platform.iamdomaincore.model;

import kh.edu.istad.platform.iamdomaincore.enums.IdentityProvider;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;

public record Identity(
        Long id,
        UUID uuid,
        Long userId,
        IdentityProvider provider,
        String providerSubject,
        String email,
        boolean emailVerified,
        String displayName,
        String username,
        String pictureUrl,
        Map<String, Object> attributes,
        OffsetDateTime linkedAt,
        OffsetDateTime lastLoginAt,
        boolean deleted,
        OffsetDateTime deletedAt,
        String deletedBy,
        OffsetDateTime createdAt,
        String createdBy,
        OffsetDateTime updatedAt,
        String updatedBy,
        long version
) {

    public Identity {
        if (uuid == null) {
            uuid = UUID.randomUUID();
        }

        if (userId == null || userId <= 0) {
            throw new IllegalArgumentException(
                    "User ID is required"
            );
        }

        if (provider == null) {
            throw new IllegalArgumentException(
                    "Identity provider is required"
            );
        }

        if (providerSubject == null || providerSubject.isBlank()) {
            throw new IllegalArgumentException(
                    "Provider subject is required"
            );
        }

        providerSubject = providerSubject.trim();

        if (providerSubject.length() > 255) {
            throw new IllegalArgumentException(
                    "Provider subject must not exceed 255 characters"
            );
        }

        if (linkedAt == null) {
            throw new IllegalArgumentException(
                    "Linked time is required"
            );
        }

        if (createdAt == null || updatedAt == null) {
            throw new IllegalArgumentException(
                    "Created time and updated time are required"
            );
        }

        if (createdBy == null || createdBy.isBlank()
                || updatedBy == null || updatedBy.isBlank()) {
            throw new IllegalArgumentException(
                    "Created by and updated by are required"
            );
        }

        if (version < 0) {
            throw new IllegalArgumentException(
                    "Version cannot be negative"
            );
        }

        if (email != null) {
            email = email.trim().toLowerCase();
        }

        if (deleted) {
            if (deletedAt == null) {
                throw new IllegalArgumentException(
                        "Deleted time is required"
                );
            }

            if (deletedBy == null || deletedBy.isBlank()) {
                throw new IllegalArgumentException(
                        "Deleted by is required"
                );
            }
        } else if (deletedAt != null || deletedBy != null) {
            throw new IllegalArgumentException(
                    "Active identity cannot have deletion metadata"
            );
        }

        attributes = attributes == null
                ? Map.of()
                : Map.copyOf(attributes);
    }

    public void validateUsable() {
        if (deleted) {
            throw new IllegalStateException(
                    "Deleted identity cannot be used"
            );
        }
    }

    public Identity recordLogin(OffsetDateTime now) {
        validateUsable();

        if (now == null) {
            throw new IllegalArgumentException(
                    "Login timestamp is required"
            );
        }

        return new Identity(
                id, uuid, userId, provider, providerSubject,
                email, emailVerified, displayName, username,
                pictureUrl, attributes,
                linkedAt, now,
                false, null, null,
                createdAt, createdBy,
                now, updatedBy,
                version + 1
        );
    }

    public Identity softDelete(
            String actor,
            OffsetDateTime now
    ) {
        validateUsable();
        validateActorAndTime(actor, now);

        return new Identity(
                id, uuid, userId, provider, providerSubject,
                email, emailVerified, displayName, username,
                pictureUrl, attributes,
                linkedAt, lastLoginAt,
                true, now, actor,
                createdAt, createdBy,
                now, actor,
                version + 1
        );
    }

    private static void validateActorAndTime(
            String actor,
            OffsetDateTime now
    ) {
        if (actor == null || actor.isBlank()) {
            throw new IllegalArgumentException(
                    "Actor is required"
            );
        }

        if (now == null) {
            throw new IllegalArgumentException(
                    "Timestamp is required"
            );
        }
    }
}
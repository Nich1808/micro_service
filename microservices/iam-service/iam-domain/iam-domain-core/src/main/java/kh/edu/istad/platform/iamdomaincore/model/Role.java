
package kh.edu.istad.platform.iamdomaincore.model;

import java.time.OffsetDateTime;
import java.util.Locale;
import java.util.UUID;

public record Role(
        Long id,
        UUID uuid,
        String name,
        String description,
        boolean system,
        boolean active,
        boolean deleted,
        OffsetDateTime deletedAt,
        String deletedBy,
        OffsetDateTime createdAt,
        String createdBy,
        OffsetDateTime updatedAt,
        String updatedBy,
        long version
) {

    public Role {
        if (uuid == null) {
            uuid = UUID.randomUUID();
        }

        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Role name is required");
        }

        name = name.trim().toUpperCase(Locale.ROOT);

        if (name.length() > 50) {
            throw new IllegalArgumentException(
                    "Role name must not exceed 50 characters"
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

        if (deleted && deletedAt == null) {
            throw new IllegalArgumentException(
                    "Deleted time is required"
            );
        }
    }

    public String authority() {
        return "ROLE_" + name;
    }

    public boolean isAssignable() {
        return active && !deleted;
    }

    public void validateAssignable() {
        if (!isAssignable()) {
            throw new IllegalStateException(
                    "Inactive or deleted role cannot be assigned"
            );
        }
    }

    public void validateModifiable() {
        if (system) {
            throw new IllegalStateException(
                    "System role cannot be modified"
            );
        }

        if (deleted) {
            throw new IllegalStateException(
                    "Deleted role cannot be modified"
            );
        }
    }

    public void validateDeletable() {
        validateModifiable();
    }

    public Role activate(String actor, OffsetDateTime now) {
        validateModifiable();
        validateActorAndTime(actor, now);

        return new Role(
                id, uuid, name, description,
                system, true, false,
                null, null,
                createdAt, createdBy,
                now, actor,
                version + 1
        );
    }

    public Role deactivate(String actor, OffsetDateTime now) {
        validateModifiable();
        validateActorAndTime(actor, now);

        return new Role(
                id, uuid, name, description,
                system, false, false,
                null, null,
                createdAt, createdBy,
                now, actor,
                version + 1
        );
    }

    public Role withDetails(
            String newName,
            String description,
            String actor,
            OffsetDateTime now
    ) {
        validateModifiable();
        validateActorAndTime(actor, now);

        return new Role(
                id, uuid, newName, description,
                system, active, false,
                null, null,
                createdAt, createdBy,
                now, actor,
                version + 1
        );
    }

    public Role softDelete(String actor, OffsetDateTime now) {
        validateDeletable();
        validateActorAndTime(actor, now);

        return new Role(
                id, uuid, name, description,
                system, false, true,
                now, actor,
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
            throw new IllegalArgumentException("Actor is required");
        }

        if (now == null) {
            throw new IllegalArgumentException("Timestamp is required");
        }
    }
}


package kh.edu.istad.platform.iamdomaincore.model;

import java.time.OffsetDateTime;
import java.util.Locale;
import java.util.UUID;

public record Permission(
        Long id,
        UUID uuid,
        String name,
        String resource,
        String action,
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

    public Permission {
        if (uuid == null) {
            uuid = UUID.randomUUID();
        }

        resource = normalize(resource, "Resource");
        action = normalize(action, "Action");

        if (resource.length() > 50 || action.length() > 50) {
            throw new IllegalArgumentException(
                    "Resource and action must not exceed 50 characters"
            );
        }

        String expectedName = resource + ":" + action;

        if (name == null || name.isBlank()) {
            name = expectedName;
        } else {
            name = name.trim().toLowerCase(Locale.ROOT);
        }

        if (!name.equals(expectedName)) {
            throw new IllegalArgumentException(
                    "Permission name must follow resource:action format"
            );
        }

        if (name.length() > 100) {
            throw new IllegalArgumentException(
                    "Permission name must not exceed 100 characters"
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
        return name;
    }

    public boolean isAssignable() {
        return active && !deleted;
    }

    public void validateAssignable() {
        if (!isAssignable()) {
            throw new IllegalStateException(
                    "Inactive or deleted permission cannot be assigned"
            );
        }
    }

    public void validateModifiable() {
        if (system) {
            throw new IllegalStateException(
                    "System permission cannot be modified"
            );
        }

        if (deleted) {
            throw new IllegalStateException(
                    "Deleted permission cannot be modified"
            );
        }
    }

    public void validateDeletable() {
        validateModifiable();
    }

    public Permission activate(String actor, OffsetDateTime now) {
        validateModifiable();
        validateActorAndTime(actor, now);

        return new Permission(
                id, uuid, name, resource, action, description,
                system, true, false,
                null, null,
                createdAt, createdBy,
                now, actor,
                version + 1
        );
    }

    public Permission deactivate(String actor, OffsetDateTime now) {
        validateModifiable();
        validateActorAndTime(actor, now);

        return new Permission(
                id, uuid, name, resource, action, description,
                system, false, false,
                null, null,
                createdAt, createdBy,
                now, actor,
                version + 1
        );
    }

    public Permission softDelete(String actor, OffsetDateTime now) {
        validateDeletable();
        validateActorAndTime(actor, now);

        return new Permission(
                id, uuid, name, resource, action, description,
                system, false, true,
                now, actor,
                createdAt, createdBy,
                now, actor,
                version + 1
        );
    }

    private static String normalize(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(field + " is required");
        }

        return value.trim().toLowerCase(Locale.ROOT);
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

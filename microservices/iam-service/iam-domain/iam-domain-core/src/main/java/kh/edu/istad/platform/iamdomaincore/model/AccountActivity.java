package kh.edu.istad.platform.iamdomaincore.model;

import kh.edu.istad.platform.iamdomaincore.enums.ActivityType;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Security-relevant event on a user account (password change, profile update, role
 * assignment, ...). Written by the application services, never edited afterwards.
 */
public record AccountActivity(
        Long id,
        UUID uuid,
        Long userId,
        ActivityType type,
        String description,
        String ipAddress,
        String userAgent,
        String actor,
        OffsetDateTime createdAt
) {

    public AccountActivity {
        if (uuid == null) {
            uuid = UUID.randomUUID();
        }

        if (userId == null || userId <= 0) {
            throw new IllegalArgumentException("User ID is required");
        }

        if (type == null) {
            throw new IllegalArgumentException("Activity type is required");
        }

        if (createdAt == null) {
            createdAt = OffsetDateTime.now();
        }

        if (actor == null || actor.isBlank()) {
            actor = "system";
        }

        if (description != null && description.length() > 255) {
            description = description.substring(0, 255);
        }
    }
}

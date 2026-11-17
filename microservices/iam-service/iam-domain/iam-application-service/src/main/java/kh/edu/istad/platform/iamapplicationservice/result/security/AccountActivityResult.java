package kh.edu.istad.platform.iamapplicationservice.result.security;

import kh.edu.istad.platform.iamdomaincore.enums.ActivityType;

import java.time.OffsetDateTime;
import java.util.UUID;

public record AccountActivityResult(
        UUID uuid,
        ActivityType type,
        String description,
        String ipAddress,
        String userAgent,
        String actor,
        OffsetDateTime createdAt
) {
}

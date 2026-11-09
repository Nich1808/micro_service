package kh.edu.istad.platform.iamapplicationservice.result.permission;

import java.time.OffsetDateTime;
import java.util.UUID;

public record PermissionResult(
        UUID uuid,
        String name,
        String resource,
        String action,
        String description,
        boolean system,
        boolean active,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {}

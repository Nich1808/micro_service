package kh.edu.istad.platform.iamapplicationservice.result.role;

import java.time.OffsetDateTime;
import java.util.UUID;

public record RoleResult(
        UUID uuid,
        String name,
        String description,
        boolean system,
        boolean active,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {}

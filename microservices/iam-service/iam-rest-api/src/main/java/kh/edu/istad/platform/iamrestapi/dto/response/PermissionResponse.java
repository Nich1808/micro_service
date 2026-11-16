package kh.edu.istad.platform.iamrestapi.dto.response;

import java.time.OffsetDateTime;
import java.util.UUID;

public record PermissionResponse(
        UUID uuid,
        String name,
        String resource,
        String action,
        String description,
        boolean system,
        boolean active,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
}

package kh.edu.istad.platform.iamrestapi.dto.response;

import java.time.OffsetDateTime;
import java.util.UUID;

public record PermissionAssignmentResponse(
        UUID permissionUuid,
        String permissionName,
        OffsetDateTime assignedAt,
        String assignedBy,
        OffsetDateTime expiresAt
) {
}

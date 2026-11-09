package kh.edu.istad.platform.iamapplicationservice.result.role;

import java.time.OffsetDateTime;
import java.util.UUID;

public record PermissionAssignmentResult(
        UUID permissionUuid,
        String permissionName,
        OffsetDateTime assignedAt,
        String assignedBy,
        OffsetDateTime expiresAt
) {}

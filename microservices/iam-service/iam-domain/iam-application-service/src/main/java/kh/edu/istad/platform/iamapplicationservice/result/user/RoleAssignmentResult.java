package kh.edu.istad.platform.iamapplicationservice.result.user;

import java.time.OffsetDateTime;
import java.util.UUID;

public record RoleAssignmentResult(
        UUID roleUuid,
        String roleName,
        OffsetDateTime assignedAt,
        String assignedBy,
        OffsetDateTime expiresAt
) {}

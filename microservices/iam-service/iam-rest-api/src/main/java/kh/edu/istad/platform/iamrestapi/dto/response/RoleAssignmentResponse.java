package kh.edu.istad.platform.iamrestapi.dto.response;

import java.time.OffsetDateTime;
import java.util.UUID;

public record RoleAssignmentResponse(
        UUID roleUuid,
        String roleName,
        OffsetDateTime assignedAt,
        String assignedBy,
        OffsetDateTime expiresAt
) {
}

package kh.edu.istad.platform.iamrestapi.dto.response;

import kh.edu.istad.platform.iamdomaincore.enums.PermissionEffect;

import java.time.OffsetDateTime;
import java.util.UUID;

public record PermissionGrantResponse(
        UUID permissionUuid,
        String permissionName,
        PermissionEffect effect,
        OffsetDateTime assignedAt,
        String assignedBy,
        OffsetDateTime expiresAt
) {
}

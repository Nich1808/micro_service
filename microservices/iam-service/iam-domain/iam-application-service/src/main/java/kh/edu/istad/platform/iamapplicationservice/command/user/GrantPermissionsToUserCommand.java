package kh.edu.istad.platform.iamapplicationservice.command.user;

import kh.edu.istad.platform.iamdomaincore.enums.PermissionEffect;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public record GrantPermissionsToUserCommand(
        UUID userUuid,
        List<UUID> permissionUuids,
        PermissionEffect effect,
        OffsetDateTime expiresAt,
        String reason
) {
    public GrantPermissionsToUserCommand {
        permissionUuids = List.copyOf(permissionUuids);
    }
}

package kh.edu.istad.platform.iamapplicationservice.command.role;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public record AssignPermissionsToRoleCommand(
        UUID roleUuid,
        List<UUID> permissionUuids,
        OffsetDateTime expiresAt,
        String reason
) {
    public AssignPermissionsToRoleCommand {
        permissionUuids = List.copyOf(permissionUuids);
    }
}

package kh.edu.istad.platform.iamapplicationservice.command.user;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public record AssignRolesToUserCommand(
        UUID userUuid,
        List<UUID> roleUuids,
        OffsetDateTime expiresAt,
        String reason
) {
    public AssignRolesToUserCommand {
        roleUuids = List.copyOf(roleUuids);
    }
}

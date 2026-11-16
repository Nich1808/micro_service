package kh.edu.istad.platform.iamrestapi.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public record AssignPermissionsToRoleRequest(
        @NotEmpty
        List<UUID> permissionUuids,

        OffsetDateTime expiresAt,

        @Size(max = 255)
        String reason
) {
    public AssignPermissionsToRoleRequest {
        permissionUuids = permissionUuids == null
                ? List.of()
                : List.copyOf(permissionUuids);
    }
}

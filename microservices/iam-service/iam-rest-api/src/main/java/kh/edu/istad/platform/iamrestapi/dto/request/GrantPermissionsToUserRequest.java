package kh.edu.istad.platform.iamrestapi.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import kh.edu.istad.platform.iamdomaincore.enums.PermissionEffect;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public record GrantPermissionsToUserRequest(
        @NotEmpty
        List<UUID> permissionUuids,

        @NotNull
        PermissionEffect effect,

        OffsetDateTime expiresAt,

        @Size(max = 255)
        String reason
) {
    public GrantPermissionsToUserRequest {
        permissionUuids = permissionUuids == null
                ? List.of()
                : List.copyOf(permissionUuids);
    }
}

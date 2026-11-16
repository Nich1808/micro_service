package kh.edu.istad.platform.iamrestapi.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.List;
import java.util.UUID;

public record CreateRoleRequest(
        @NotBlank
        @Size(max = 50)
        String name,

        @Size(max = 255)
        String description,

        List<UUID> permissionUuids
) {
    public CreateRoleRequest {
        permissionUuids = permissionUuids == null
                ? List.of()
                : List.copyOf(permissionUuids);
    }
}

package kh.edu.istad.platform.iamrestapi.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public record AssignRolesToUserRequest(
        @NotEmpty
        List<UUID> roleUuids,

        OffsetDateTime expiresAt,

        @Size(max = 255)
        String reason
) {
    public AssignRolesToUserRequest {
        roleUuids = roleUuids == null ? List.of() : List.copyOf(roleUuids);
    }
}

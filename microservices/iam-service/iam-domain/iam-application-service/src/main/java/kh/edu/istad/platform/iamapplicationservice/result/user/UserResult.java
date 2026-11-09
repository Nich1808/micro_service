package kh.edu.istad.platform.iamapplicationservice.result.user;

import kh.edu.istad.platform.iamapplicationservice.result.role.RoleSummary;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public record UserResult(
        UUID uuid,
        String username,
        String email,
        boolean emailVerified,
        String name,
        String locale,
        boolean enabled,
        List<RoleSummary> roles,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
    public UserResult {
        roles = List.copyOf(roles);
    }
}

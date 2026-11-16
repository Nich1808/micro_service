package kh.edu.istad.platform.iamrestapi.dto.response;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public record UserResponse(
        UUID uuid,
        String username,
        String email,
        boolean emailVerified,
        String name,
        String locale,
        boolean enabled,
        List<RoleSummaryResponse> roles,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
}

package kh.edu.istad.platform.iamrestapi.dto.response;

import java.time.OffsetDateTime;
import java.util.UUID;

public record UserSummaryResponse(
        UUID uuid,
        String username,
        String email,
        String name,
        boolean enabled,
        OffsetDateTime createdAt
) {
}

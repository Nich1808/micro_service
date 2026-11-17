package kh.edu.istad.platform.iamrestapi.dto.response;

import java.time.OffsetDateTime;
import java.util.UUID;

public record UserSessionResponse(
        UUID uuid,
        String device,
        String browser,
        String operatingSystem,
        String ipAddress,
        String location,
        OffsetDateTime createdAt,
        OffsetDateTime lastActivityAt,
        OffsetDateTime expiresAt,
        boolean current
) {
}

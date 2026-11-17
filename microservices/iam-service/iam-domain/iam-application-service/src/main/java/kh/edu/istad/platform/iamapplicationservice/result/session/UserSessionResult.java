package kh.edu.istad.platform.iamapplicationservice.result.session;

import java.time.OffsetDateTime;
import java.util.UUID;

public record UserSessionResult(
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

package kh.edu.istad.platform.iamrestapi.dto.response;

import java.time.OffsetDateTime;
import java.util.UUID;

public record LoginAttemptResponse(
        UUID uuid,
        boolean successful,
        String failureReason,
        String identifier,
        String ipAddress,
        String device,
        String browser,
        String operatingSystem,
        String location,
        OffsetDateTime attemptedAt,
        boolean suspicious
) {
}

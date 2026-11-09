package kh.edu.istad.platform.iamapplicationservice.result.user;

import java.time.OffsetDateTime;
import java.util.UUID;

public record UserSummary(
        UUID uuid,
        String username,
        String email,
        String name,
        boolean enabled,
        OffsetDateTime createdAt
) {}

package kh.edu.istad.platform.iamapplicationservice.result.role;

import java.util.UUID;

public record RoleSummary(
        UUID uuid,
        String name,
        boolean active
) {
}

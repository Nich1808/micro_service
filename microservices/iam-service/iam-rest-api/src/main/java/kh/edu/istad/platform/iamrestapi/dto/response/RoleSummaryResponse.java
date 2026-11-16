package kh.edu.istad.platform.iamrestapi.dto.response;

import java.util.UUID;

public record RoleSummaryResponse(
        UUID uuid,
        String name,
        boolean active
) {
}

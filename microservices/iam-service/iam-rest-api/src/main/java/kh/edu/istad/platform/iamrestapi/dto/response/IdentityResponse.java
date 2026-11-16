package kh.edu.istad.platform.iamrestapi.dto.response;

import kh.edu.istad.platform.iamdomaincore.enums.IdentityProvider;

import java.time.OffsetDateTime;
import java.util.UUID;

public record IdentityResponse(
        UUID uuid,
        IdentityProvider provider,
        String email,
        String displayName,
        OffsetDateTime linkedAt
) {
}

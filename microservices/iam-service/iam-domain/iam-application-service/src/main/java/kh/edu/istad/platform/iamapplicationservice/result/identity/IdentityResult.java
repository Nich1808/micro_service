package kh.edu.istad.platform.iamapplicationservice.result.identity;

import kh.edu.istad.platform.iamdomaincore.enums.IdentityProvider;

import java.time.OffsetDateTime;
import java.util.UUID;

public record IdentityResult(
        UUID uuid,
        IdentityProvider provider,
        String email,
        String displayName,
        OffsetDateTime linkedAt
) {
}

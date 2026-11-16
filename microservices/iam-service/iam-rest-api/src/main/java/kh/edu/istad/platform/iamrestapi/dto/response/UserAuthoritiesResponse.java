package kh.edu.istad.platform.iamrestapi.dto.response;

import kh.edu.istad.platform.iamdomaincore.enums.PermissionEffect;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public record UserAuthoritiesResponse(
        List<RoleAuthority> roles,
        List<DirectPermission> directPermissions,
        List<String> effectivePermissions
) {
    public record RoleAuthority(
            UUID uuid,
            String name,
            OffsetDateTime expiresAt
    ) {
    }

    public record DirectPermission(
            String name,
            PermissionEffect effect
    ) {
    }
}

package kh.edu.istad.platform.iamapplicationservice.result.user;

import kh.edu.istad.platform.iamdomaincore.enums.PermissionEffect;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public record UserAuthoritiesResult(
        List<RoleAuthority> roles,
        List<DirectPermission> directPermissions,
        List<String> effectivePermissions
) {
    public UserAuthoritiesResult {
        roles = List.copyOf(roles);
        directPermissions = List.copyOf(directPermissions);
        effectivePermissions = List.copyOf(effectivePermissions);
    }

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

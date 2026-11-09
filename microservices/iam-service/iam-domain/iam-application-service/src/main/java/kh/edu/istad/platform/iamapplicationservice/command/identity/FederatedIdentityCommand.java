package kh.edu.istad.platform.iamapplicationservice.command.identity;

import kh.edu.istad.platform.iamdomaincore.enums.IdentityProvider;

import java.util.Map;

public record FederatedIdentityCommand(
        IdentityProvider provider,
        String providerSubject,
        String email,
        boolean emailVerified,
        String displayName,
        String username,
        String pictureUrl,
        Map<String, Object> attributes
) {
    public FederatedIdentityCommand {
        attributes = attributes == null ? Map.of() : Map.copyOf(attributes);
    }
}

package kh.edu.istad.platform.iamapplicationservice.command.admin;

import java.util.List;

/**
 * Create/update payload for an OAuth2/OIDC client. {@code clientSecret} is only used on
 * create (and on explicit rotation); leave it null to keep the current secret.
 */
public record ClientCommand(
        String clientId,
        String clientName,
        String clientSecret,
        List<String> authenticationMethods,
        List<String> grantTypes,
        List<String> redirectUris,
        List<String> postLogoutRedirectUris,
        List<String> scopes,
        boolean requireProofKey,
        boolean requireConsent,
        Long accessTokenTtlSeconds,
        Long refreshTokenTtlSeconds
) {
    public ClientCommand {
        authenticationMethods = copy(authenticationMethods);
        grantTypes = copy(grantTypes);
        redirectUris = copy(redirectUris);
        postLogoutRedirectUris = copy(postLogoutRedirectUris);
        scopes = copy(scopes);
    }

    private static List<String> copy(List<String> values) {
        return values == null ? List.of() : List.copyOf(values);
    }
}

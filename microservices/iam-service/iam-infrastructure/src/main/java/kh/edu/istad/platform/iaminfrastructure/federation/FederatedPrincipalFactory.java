package kh.edu.istad.platform.iaminfrastructure.federation;

import kh.edu.istad.platform.iamapplicationservice.command.identity.FederatedIdentityCommand;
import kh.edu.istad.platform.iamapplicationservice.result.user.AuthUserResult;
import kh.edu.istad.platform.iamapplicationservice.usecase.identity.FindOrProvisionFederatedUserUseCase;
import kh.edu.istad.platform.iamdomaincore.enums.IdentityProvider;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Converts a provider profile into an IAM user (find or provision) and exposes the
 * resulting authorities to Spring Security.
 */
public class FederatedPrincipalFactory {

    private final FindOrProvisionFederatedUserUseCase findOrProvisionFederatedUserUseCase;

    public FederatedPrincipalFactory(
            FindOrProvisionFederatedUserUseCase findOrProvisionFederatedUserUseCase
    ) {
        this.findOrProvisionFederatedUserUseCase =
                findOrProvisionFederatedUserUseCase;
    }

    /**
     * Attribute that carries the IAM username; it is used as the principal name so the
     * authorization server stores a stable {@code principal_name} and the consent and
     * connected-apps pages can match records.
     */
    public static final String IAM_USERNAME_ATTRIBUTE = "iam_username";

    public OAuth2User createOAuth2User(
            ClientRegistration registration,
            Map<String, Object> attributes
    ) {
        AuthUserResult result = provision(registration, attributes);

        return new DefaultOAuth2User(
                authoritiesOf(result),
                attributesWithIdentity(attributes, result),
                IAM_USERNAME_ATTRIBUTE
        );
    }

    public OAuth2User createOidcUser(
            ClientRegistration registration,
            Map<String, Object> attributes,
            OidcIdToken idToken,
            OidcUserInfo userInfo
    ) {
        AuthUserResult result = provision(registration, attributes);

        // The principal name comes from the ID token claims, so the IAM identity is
        // added to an enriched copy of the token.
        Map<String, Object> claims = new LinkedHashMap<>(idToken.getClaims());
        claims.put(IAM_USERNAME_ATTRIBUTE, result.username());
        claims.put("iam_user_uuid", result.uuid().toString());

        OidcIdToken enriched = new OidcIdToken(
                idToken.getTokenValue(),
                idToken.getIssuedAt(),
                idToken.getExpiresAt(),
                claims
        );

        return new DefaultOidcUser(
                authoritiesOf(result),
                enriched,
                userInfo,
                IAM_USERNAME_ATTRIBUTE
        );
    }

    private AuthUserResult provision(
            ClientRegistration registration,
            Map<String, Object> attributes
    ) {
        return findOrProvisionFederatedUserUseCase.execute(
                toCommand(providerOf(registration.getRegistrationId()), attributes)
        );
    }

    /**
     * Maps raw provider attributes onto the application command. Used by both the login
     * adapters and the account-linking flow.
     */
    public static FederatedIdentityCommand toCommand(
            IdentityProvider provider,
            Map<String, Object> attributes
    ) {
        String subject = subjectOf(provider, attributes);

        return new FederatedIdentityCommand(
                provider,
                subject,
                text(attributes, "email"),
                bool(attributes, "email_verified"),
                text(attributes, "name", "display_name", "login"),
                text(attributes, "preferred_username", "login", "username"),
                text(attributes, "picture", "avatar_url"),
                attributes
        );
    }

    public static IdentityProvider providerOf(String registrationId) {
        if (registrationId == null) {
            throw new IllegalStateException("Client registration is required");
        }

        return switch (registrationId.toLowerCase(Locale.ROOT)) {
            case "google" -> IdentityProvider.GOOGLE;
            case "github" -> IdentityProvider.GITHUB;
            case "facebook" -> IdentityProvider.FACEBOOK;
            case "telegram" -> IdentityProvider.TELEGRAM;
            default -> throw new IllegalStateException(
                    "Unsupported identity provider: " + registrationId
            );
        };
    }

    private static String subjectOf(
            IdentityProvider provider,
            Map<String, Object> attributes
    ) {
        String subject = text(attributes, "sub", "id", "user_id");

        if (subject == null || subject.isBlank()) {
            throw new IllegalStateException(
                    "Provider " + provider + " did not return a subject"
            );
        }

        return subject;
    }

    private static String text(Map<String, Object> attributes, String... keys) {
        for (String key : keys) {
            Object value = attributes.get(key);

            if (value != null && !value.toString().isBlank()) {
                return value.toString();
            }
        }

        return null;
    }

    private static boolean bool(Map<String, Object> attributes, String key) {
        Object value = attributes.get(key);
        return value instanceof Boolean parsed ? parsed : false;
    }

    private static List<GrantedAuthority> authoritiesOf(AuthUserResult result) {
        return result.authorities().stream()
                .map(SimpleGrantedAuthority::new)
                .map(authority -> (GrantedAuthority) authority)
                .toList();
    }

    private static Map<String, Object> attributesWithIdentity(
            Map<String, Object> attributes,
            AuthUserResult result
    ) {
        Map<String, Object> merged = new LinkedHashMap<>(attributes);
        merged.put(IAM_USERNAME_ATTRIBUTE, result.username());
        merged.put("iam_user_uuid", result.uuid().toString());
        return merged;
    }
}

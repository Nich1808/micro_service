package kh.edu.istad.platform.iaminfrastructure.token;

import kh.edu.istad.platform.iamapplicationservice.port.out.UserRepository;
import kh.edu.istad.platform.iamdomaincore.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class IamTokenCustomizerTest {

    private static final UUID USER_UUID =
            UUID.fromString("7f3d2a10-91c4-4b0e-8d6f-2a9c5e1b7d40");

    private static final User USER = new User(
            1L, USER_UUID,
            "sokdara", "dara.sok@istad.co", true, "hash",
            "Dara", "Sok", null, null, "male", LocalDate.parse("2003-05-20"),
            null, "https://cdn/p.png", null, "Asia/Phnom_Penh", "km",
            "+85512345678", false,
            "No. 24, St. 562", "Toul Kork", "Phnom Penh", "120408", "KH",
            true, true, true, true,
            0, null, null,
            false, null, null,
            OffsetDateTime.parse("2026-11-05T02:15:00Z"),
            "system",
            OffsetDateTime.parse("2026-11-05T02:15:00Z"),
            "system",
            0
    );

    @Test
    void subjectIsThePublicUserUuid() {
        JwtClaimsSet claims = customize(accessTokenContext(Set.of(OidcScopes.OPENID)));

        assertEquals(USER_UUID.toString(), claims.getSubject());
    }

    @Test
    void accessTokenCarriesRolesAndPermissions() {
        JwtClaimsSet claims = customize(accessTokenContext(Set.of(OidcScopes.OPENID)));

        assertEquals(List.of("ADMIN"), claims.getClaim("roles"));
        assertEquals(List.of("user:read", "role:read"), claims.getClaim("permissions"));
        assertEquals(
                List.of("ROLE_ADMIN", "user:read", "role:read"),
                claims.getClaim("authorities")
        );
    }

    @Test
    void authenticationFactorMarkersAreNotPublished() {
        Authentication principal = new UsernamePasswordAuthenticationToken(
                "sokdara",
                "n/a",
                List.of(
                        new SimpleGrantedAuthority("ROLE_ADMIN"),
                        new SimpleGrantedAuthority("FACTOR_PASSWORD"),
                        new SimpleGrantedAuthority("user:read")
                )
        );

        JwtClaimsSet claims = customize(accessTokenContext(
                Set.of(OidcScopes.OPENID),
                principal
        ));

        assertEquals(List.of("ADMIN"), claims.getClaim("roles"));
        assertEquals(List.of("user:read"), claims.getClaim("permissions"));
    }

    @Test
    void profileScopeAddsOidcProfileClaims() {
        JwtClaimsSet claims = customize(accessTokenContext(
                Set.of(OidcScopes.OPENID, OidcScopes.PROFILE)
        ));

        assertEquals("Dara Sok", claims.getClaim("name"));
        assertEquals("Dara", claims.getClaim("given_name"));
        assertEquals("Sok", claims.getClaim("family_name"));
        assertEquals("sokdara", claims.getClaim("preferred_username"));
        assertEquals("km", claims.getClaim("locale"));
        assertEquals("2003-05-20", claims.getClaim("birthdate"));
        assertEquals("Asia/Phnom_Penh", claims.getClaim("zoneinfo"));
    }

    @Test
    void emailScopeAddsEmailClaims() {
        JwtClaimsSet claims = customize(accessTokenContext(
                Set.of(OidcScopes.OPENID, OidcScopes.EMAIL)
        ));

        assertEquals("dara.sok@istad.co", claims.getClaim("email"));
        assertEquals(true, claims.getClaim("email_verified"));
    }

    @Test
    void addressScopeAddsAddressObject() {
        JwtClaimsSet claims = customize(accessTokenContext(
                Set.of(OidcScopes.OPENID, OidcScopes.ADDRESS)
        ));

        @SuppressWarnings("unchecked")
        Map<String, Object> address = (Map<String, Object>) claims.getClaim("address");

        assertEquals("No. 24, St. 562", address.get("street_address"));
        assertEquals("Toul Kork", address.get("locality"));
        assertEquals("KH", address.get("country"));
    }

    private static JwtClaimsSet customize(JwtEncodingContext context) {
        UserRepository repository = mock(UserRepository.class);
        when(repository.findByUsernameOrEmail("sokdara"))
                .thenReturn(Optional.of(USER));

        new IamTokenCustomizer(repository).customize(context);

        return context.getClaims().build();
    }

    private static JwtEncodingContext accessTokenContext(Set<String> scopes) {
        Authentication principal = new UsernamePasswordAuthenticationToken(
                "sokdara",
                "n/a",
                List.of(
                        new SimpleGrantedAuthority("ROLE_ADMIN"),
                        new SimpleGrantedAuthority("user:read"),
                        new SimpleGrantedAuthority("role:read")
                )
        );

        return accessTokenContext(scopes, principal);
    }

    private static JwtEncodingContext accessTokenContext(
            Set<String> scopes,
            Authentication principal
    ) {
        RegisteredClient client = RegisteredClient.withId("client-1")
                .clientId("oidc-client")
                .clientAuthenticationMethod(ClientAuthenticationMethod.NONE)
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .redirectUri("http://127.0.0.1:8080/login/oauth2/code/oidc-client")
                .scope(OidcScopes.OPENID)
                .build();

        return JwtEncodingContext
                .with(
                        JwsHeader.with(MacAlgorithm.HS256),
                        JwtClaimsSet.builder()
                )
                .registeredClient(client)
                .principal(principal)
                .authorizedScopes(scopes)
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .tokenType(OAuth2TokenType.ACCESS_TOKEN)
                .build();
    }
}

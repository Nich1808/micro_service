package kh.edu.istad.platform.iaminfrastructure.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.oauth2.server.authorization.settings.OAuth2TokenFormat;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.UUID;

/**
 * Seeds the clients required by the assignment:
 * <ul>
 *   <li>{@code oidc-client} – public PKCE client used by the web app.</li>
 *   <li>{@code dcr-initial-access} – confidential client with the
 *       {@code client.create} scope, used as the initial access token for
 *       Dynamic Client Registration.</li>
 * </ul>
 */
@Component
public class RegisteredClientSeeder implements ApplicationRunner {

    private final RegisteredClientRepository registeredClientRepository;
    private final PasswordEncoder passwordEncoder;
    private final String webAppRedirectUri;
    private final String webAppPostLogoutRedirectUri;
    private final String dcrClientSecret;

    public RegisteredClientSeeder(
            RegisteredClientRepository registeredClientRepository,
            PasswordEncoder passwordEncoder,
            @Value("${iam.bootstrap.web-client.redirect-uri:"
                    + "http://127.0.0.1:8080/login/oauth2/code/oidc-client}")
            String webAppRedirectUri,
            @Value("${iam.bootstrap.web-client.post-logout-redirect-uri:"
                    + "http://127.0.0.1:8080/}")
            String webAppPostLogoutRedirectUri,
            @Value("${IAM_DCR_CLIENT_SECRET:dcr-secret}")
            String dcrClientSecret
    ) {
        this.registeredClientRepository = registeredClientRepository;
        this.passwordEncoder = passwordEncoder;
        this.webAppRedirectUri = webAppRedirectUri;
        this.webAppPostLogoutRedirectUri = webAppPostLogoutRedirectUri;
        this.dcrClientSecret = dcrClientSecret;
    }

    @Override
    public void run(ApplicationArguments args) {
        seedWebClient();
        seedDynamicClientRegistrationClient();
    }

    private void seedWebClient() {
        if (registeredClientRepository.findByClientId("oidc-client") != null) {
            return;
        }

        registeredClientRepository.save(RegisteredClient
                .withId(UUID.randomUUID().toString())
                .clientId("oidc-client")
                .clientName("ISTAD IAM Web")
                .clientAuthenticationMethod(ClientAuthenticationMethod.NONE)
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
                .redirectUri(webAppRedirectUri)
                .postLogoutRedirectUri(webAppPostLogoutRedirectUri)
                .scope(OidcScopes.OPENID)
                .scope(OidcScopes.PROFILE)
                .scope(OidcScopes.EMAIL)
                .scope(OidcScopes.PHONE)
                .scope(OidcScopes.ADDRESS)
                .clientSettings(ClientSettings.builder()
                        .requireProofKey(true)
                        .requireAuthorizationConsent(true)
                        .build())
                .tokenSettings(tokenSettings(Duration.ofHours(1)))
                .build());
    }

    private void seedDynamicClientRegistrationClient() {
        if (registeredClientRepository.findByClientId("dcr-initial-access") != null) {
            return;
        }

        registeredClientRepository.save(RegisteredClient
                .withId(UUID.randomUUID().toString())
                .clientId("dcr-initial-access")
                .clientName("Dynamic Client Registration")
                .clientSecret(passwordEncoder.encode(dcrClientSecret))
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
                .scope("client.create")
                .clientSettings(ClientSettings.builder()
                        .requireProofKey(false)
                        .requireAuthorizationConsent(false)
                        .build())
                .tokenSettings(tokenSettings(Duration.ofMinutes(30)))
                .build());
    }

    private TokenSettings tokenSettings(Duration accessTokenTtl) {
        return TokenSettings.builder()
                .accessTokenFormat(OAuth2TokenFormat.SELF_CONTAINED)
                .accessTokenTimeToLive(accessTokenTtl)
                .refreshTokenTimeToLive(Duration.ofDays(7))
                .authorizationCodeTimeToLive(Duration.ofMinutes(3))
                .reuseRefreshTokens(false)
                .build();
    }
}

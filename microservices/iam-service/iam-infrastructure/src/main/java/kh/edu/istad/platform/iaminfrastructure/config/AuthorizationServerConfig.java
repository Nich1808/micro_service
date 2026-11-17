package kh.edu.istad.platform.iaminfrastructure.config;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import kh.edu.istad.platform.iamapplicationservice.port.out.UserRepository;
import kh.edu.istad.platform.iamapplicationservice.port.out.SystemSettingsRepository;
import kh.edu.istad.platform.iamapplicationservice.port.out.UserSessionRepository;
import kh.edu.istad.platform.iamapplicationservice.usecase.identity.FindOrProvisionFederatedUserUseCase;
import kh.edu.istad.platform.iamapplicationservice.usecase.identity.LinkIdentityUseCase;
import kh.edu.istad.platform.iamapplicationservice.usecase.user.LoadUserForAuthenticationUseCase;
import kh.edu.istad.platform.iamapplicationservice.usecase.user.RecordLoginAttemptUseCase;
import kh.edu.istad.platform.iaminfrastructure.federation.FederatedOidcUserService;
import kh.edu.istad.platform.iaminfrastructure.federation.FederatedOAuth2UserService;
import kh.edu.istad.platform.iaminfrastructure.federation.FederatedPrincipalFactory;
import kh.edu.istad.platform.iaminfrastructure.federation.LinkAccountSuccessHandler;
import kh.edu.istad.platform.iaminfrastructure.security.LoginFailureListener;
import kh.edu.istad.platform.iaminfrastructure.security.LoginSuccessListener;
import kh.edu.istad.platform.iaminfrastructure.security.SessionTrackingFilter;
import kh.edu.istad.platform.iaminfrastructure.security.UseCaseUserDetailsService;
import kh.edu.istad.platform.iaminfrastructure.token.IamTokenCustomizer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.authorization.JdbcOAuth2AuthorizationConsentService;
import org.springframework.security.oauth2.server.authorization.JdbcOAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsentService;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.client.JdbcRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.UUID;

/**
 * Spring Authorization Server adapter: JDBC-backed clients, authorizations and
 * consents, token signing keys and the OIDC token customizer.
 */
@Configuration
public class AuthorizationServerConfig {

    @Bean
    public RegisteredClientRepository registeredClientRepository(
            JdbcTemplate jdbcTemplate
    ) {
        return new JdbcRegisteredClientRepository(jdbcTemplate);
    }

    @Bean
    public OAuth2AuthorizationService authorizationService(
            JdbcTemplate jdbcTemplate,
            RegisteredClientRepository registeredClientRepository
    ) {
        return new JdbcOAuth2AuthorizationService(
                jdbcTemplate,
                registeredClientRepository
        );
    }

    @Bean
    public OAuth2AuthorizationConsentService authorizationConsentService(
            JdbcTemplate jdbcTemplate,
            RegisteredClientRepository registeredClientRepository
    ) {
        return new JdbcOAuth2AuthorizationConsentService(
                jdbcTemplate,
                registeredClientRepository
        );
    }

    @Bean
    public JWKSource<SecurityContext> jwkSource() {
        KeyPair keyPair = generateRsaKey();

        RSAKey rsaKey = new RSAKey.Builder((RSAPublicKey) keyPair.getPublic())
                .privateKey((RSAPrivateKey) keyPair.getPrivate())
                .keyID(UUID.randomUUID().toString())
                .build();

        return new ImmutableJWKSet<>(new JWKSet(rsaKey));
    }

    @Bean
    public JwtDecoder jwtDecoder(JWKSource<SecurityContext> jwkSource) {
        return OAuth2AuthorizationServerConfiguration.jwtDecoder(jwkSource);
    }

    @Bean
    public AuthorizationServerSettings authorizationServerSettings(
            @Value("${iam.issuer:http://localhost:9026}") String issuer
    ) {
        return AuthorizationServerSettings.builder()
                .issuer(issuer)
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public OAuth2TokenCustomizer<JwtEncodingContext> oauth2TokenCustomizer(
            UserRepository userRepository
    ) {
        return new IamTokenCustomizer(userRepository);
    }

    @Bean
    public UseCaseUserDetailsService userDetailsService(
            LoadUserForAuthenticationUseCase loadUserForAuthenticationUseCase
    ) {
        return new UseCaseUserDetailsService(loadUserForAuthenticationUseCase);
    }

    @Bean
    public FederatedPrincipalFactory federatedPrincipalFactory(
            FindOrProvisionFederatedUserUseCase findOrProvisionFederatedUserUseCase
    ) {
        return new FederatedPrincipalFactory(findOrProvisionFederatedUserUseCase);
    }

    @Bean
    public FederatedOAuth2UserService federatedOAuth2UserService(
            FederatedPrincipalFactory federatedPrincipalFactory
    ) {
        return new FederatedOAuth2UserService(federatedPrincipalFactory);
    }

    @Bean
    public FederatedOidcUserService federatedOidcUserService(
            FederatedPrincipalFactory federatedPrincipalFactory
    ) {
        return new FederatedOidcUserService(federatedPrincipalFactory);
    }

    @Bean
    public LinkAccountSuccessHandler linkAccountSuccessHandler(
            LinkIdentityUseCase linkIdentityUseCase,
            @Value("${iam.link.success-redirect:"
                    + "${iam.bootstrap.spa.base-url:http://localhost:5173}"
                    + "/accounts?linked={provider}}")
            String successRedirect,
            @Value("${iam.link.error-redirect:"
                    + "${iam.bootstrap.spa.base-url:http://localhost:5173}"
                    + "/accounts?error={provider}}")
            String errorRedirect
    ) {
        return new LinkAccountSuccessHandler(
                linkIdentityUseCase,
                successRedirect,
                errorRedirect
        );
    }

    // ------------------------------------------------------------------
    // Account security: active sessions and login history
    // ------------------------------------------------------------------

    @Bean
    public SessionTrackingFilter sessionTrackingFilter(
            UserRepository userRepository,
            UserSessionRepository userSessionRepository,
            SystemSettingsRepository systemSettingsRepository
    ) {
        return new SessionTrackingFilter(
                userRepository,
                userSessionRepository,
                systemSettingsRepository
        );
    }

    @Bean
    public LoginSuccessListener loginSuccessListener(
            RecordLoginAttemptUseCase recordLoginAttemptUseCase
    ) {
        return new LoginSuccessListener(recordLoginAttemptUseCase);
    }

    @Bean
    public LoginFailureListener loginFailureListener(
            RecordLoginAttemptUseCase recordLoginAttemptUseCase
    ) {
        return new LoginFailureListener(recordLoginAttemptUseCase);
    }

    private static KeyPair generateRsaKey() {
        try {
            KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
            generator.initialize(2048);
            return generator.generateKeyPair();
        } catch (Exception ex) {
            throw new IllegalStateException("Unable to generate RSA key", ex);
        }
    }
}

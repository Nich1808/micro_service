package kh.edu.istad.platform.iaminfrastructure.config;

import kh.edu.istad.platform.iaminfrastructure.federation.FederatedOidcUserService;
import kh.edu.istad.platform.iaminfrastructure.federation.FederatedOAuth2UserService;
import kh.edu.istad.platform.iaminfrastructure.federation.LinkAccountSuccessHandler;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.authorization.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.authorization.oidc.authentication.OidcClientRegistrationAuthenticationContext;
import org.springframework.security.oauth2.server.authorization.oidc.authentication.OidcClientRegistrationAuthenticationProvider;
import org.springframework.security.oauth2.server.authorization.oidc.authentication.OidcClientRegistrationAuthenticationValidator;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.util.matcher.MediaTypeRequestMatcher;

import java.util.List;
import java.util.function.Consumer;

/**
 * Authorization server endpoint chain (OAuth 2.1 / OIDC 1.0, including Dynamic Client
 * Registration) plus the browser security chain used for form and social login.
 */
@Configuration
@EnableWebSecurity
public class AuthorizationServerSecurityConfig {

    @Bean
    @Order(1)
    public SecurityFilterChain authorizationServerSecurityFilterChain(
            HttpSecurity http,
            JwtDecoder jwtDecoder
    ) throws Exception {
        OAuth2AuthorizationServerConfigurer authorizationServer =
                new OAuth2AuthorizationServerConfigurer();

        http
                .securityMatcher(authorizationServer.getEndpointsMatcher())
                // The browser dashboard exchanges the authorization code from its own
                // origin, so the token/revocation endpoints must answer CORS preflights.
                .cors(Customizer.withDefaults())
                .with(authorizationServer, server -> server
                        .authorizationEndpoint(endpoint -> endpoint
                                .consentPage("/oauth2/consent"))
                        .oidc(oidc -> oidc.clientRegistrationEndpoint(registration ->
                                registration.authenticationProviders(
                                        AuthorizationServerSecurityConfig
                                                ::acceptRequestedScopes
                                ))))
                .authorizeHttpRequests(authorize -> authorize
                        .anyRequest().authenticated())
                .oauth2ResourceServer(resourceServer -> resourceServer
                        .jwt(jwt -> jwt.decoder(jwtDecoder)))
                .exceptionHandling(exceptions -> exceptions
                        .defaultAuthenticationEntryPointFor(
                                new LoginUrlAuthenticationEntryPoint("/login"),
                                new MediaTypeRequestMatcher(MediaType.TEXT_HTML)
                        ));

        return http.build();
    }

    @Bean
    @Order(3)
    public SecurityFilterChain defaultSecurityFilterChain(
            HttpSecurity http,
            ObjectProvider<ClientRegistrationRepository> clientRegistrations,
            ObjectProvider<FederatedOAuth2UserService> oauth2UserService,
            ObjectProvider<FederatedOidcUserService> oidcUserService,
            LinkAccountSuccessHandler linkAccountSuccessHandler
    ) throws Exception {
        http.authorizeHttpRequests(authorize -> authorize
                .requestMatchers(
                        "/login",
                        "/register",
                        "/css/**",
                        "/js/**",
                        "/assets/**",
                        "/error",
                        "/favicon.ico"
                ).permitAll()
                .anyRequest().authenticated());

        if (clientRegistrations.getIfAvailable() != null) {
            FederatedOAuth2UserService userService =
                    oauth2UserService.getIfAvailable();
            FederatedOidcUserService oidcService =
                    oidcUserService.getIfAvailable();

            http.oauth2Login(oauth2 -> {
                oauth2.loginPage("/login");
                oauth2.successHandler(linkAccountSuccessHandler);

                if (userService != null && oidcService != null) {
                    oauth2.userInfoEndpoint(userInfo -> userInfo
                            .userService(userService)
                            .oidcUserService(oidcService));
                } else if (userService != null) {
                    oauth2.userInfoEndpoint(userInfo ->
                            userInfo.userService(userService));
                }
            });
        }

        http.formLogin(form -> form
                .loginPage("/login")
                .permitAll());

        http.logout(logout -> logout
                .logoutSuccessUrl("/login?logout"));

        return http.build();
    }

    /**
     * The assignment registers clients with explicit scopes (for example
     * {@code openid profile email}). Spring Authorization Server 7 rejects any scope
     * during Dynamic Client Registration by default, so the scope rule is relaxed while
     * the strict redirect-URI and JWK-set checks stay in place.
     */
    private static void acceptRequestedScopes(List<AuthenticationProvider> providers) {
        Consumer<OidcClientRegistrationAuthenticationContext> validator =
                OidcClientRegistrationAuthenticationValidator
                        .DEFAULT_REDIRECT_URI_VALIDATOR
                        .andThen(OidcClientRegistrationAuthenticationValidator
                                .DEFAULT_POST_LOGOUT_REDIRECT_URI_VALIDATOR)
                        .andThen(OidcClientRegistrationAuthenticationValidator
                                .DEFAULT_JWK_SET_URI_VALIDATOR)
                        .andThen(OidcClientRegistrationAuthenticationValidator
                                .SIMPLE_SCOPE_VALIDATOR);

        providers.stream()
                .filter(OidcClientRegistrationAuthenticationProvider.class::isInstance)
                .map(OidcClientRegistrationAuthenticationProvider.class::cast)
                .forEach(provider -> provider.setAuthenticationValidator(validator));
    }
}

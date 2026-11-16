package kh.edu.istad.platform.iaminfrastructure.federation;

import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * Social login entry point (Google, GitHub, Facebook, Telegram) that delegates the
 * IAM decision to {@code FindOrProvisionFederatedUserUseCase}.
 *
 * <p>When the browser is in the "link an account" flow the provider profile is returned
 * untouched: the identity is attached by {@link LinkAccountSuccessHandler}, so no
 * second IAM user is provisioned.</p>
 */
public class FederatedOAuth2UserService
        implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate =
            new DefaultOAuth2UserService();

    private final FederatedPrincipalFactory factory;

    public FederatedOAuth2UserService(FederatedPrincipalFactory factory) {
        this.factory = factory;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest)
            throws OAuth2AuthenticationException {
        OAuth2User providerUser = delegate.loadUser(userRequest);

        if (isLinking()) {
            return providerUser;
        }

        return factory.createOAuth2User(
                userRequest.getClientRegistration(),
                providerUser.getAttributes()
        );
    }

    private boolean isLinking() {
        if (!(RequestContextHolder.getRequestAttributes()
                instanceof ServletRequestAttributes attributes)) {
            return false;
        }

        return LinkAccountSession.isActive(attributes.getRequest().getSession(false));
    }
}

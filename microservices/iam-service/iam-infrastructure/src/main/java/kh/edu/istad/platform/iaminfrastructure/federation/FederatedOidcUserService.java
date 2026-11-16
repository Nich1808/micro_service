package kh.edu.istad.platform.iaminfrastructure.federation;

import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * OIDC variant of the social login adapter (used by Google and any OpenID provider).
 * In the account-linking flow the provider profile is returned untouched.
 */
public class FederatedOidcUserService
        implements OAuth2UserService<OidcUserRequest, OidcUser> {

    private final OidcUserService delegate = new OidcUserService();
    private final FederatedPrincipalFactory factory;

    public FederatedOidcUserService(FederatedPrincipalFactory factory) {
        this.factory = factory;
    }

    @Override
    public OidcUser loadUser(OidcUserRequest userRequest)
            throws OAuth2AuthenticationException {
        OidcUser providerUser = delegate.loadUser(userRequest);

        if (isLinking()) {
            return providerUser;
        }

        return (OidcUser) factory.createOidcUser(
                userRequest.getClientRegistration(),
                providerUser.getAttributes(),
                providerUser.getIdToken(),
                providerUser.getUserInfo()
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

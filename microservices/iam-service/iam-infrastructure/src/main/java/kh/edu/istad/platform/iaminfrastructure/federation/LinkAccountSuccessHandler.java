package kh.edu.istad.platform.iaminfrastructure.federation;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import kh.edu.istad.platform.iamapplicationservice.command.identity.FederatedIdentityCommand;
import kh.edu.istad.platform.iamapplicationservice.usecase.identity.LinkIdentityUseCase;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

/**
 * Completes the link flow started from the profile page: the provider identity is
 * attached to the signed-in user and the original session is restored, so linking a
 * social account never switches who is logged in.
 */
public class LinkAccountSuccessHandler implements AuthenticationSuccessHandler {

    private static final Log LOG = LogFactory.getLog(LinkAccountSuccessHandler.class);

    private final LinkIdentityUseCase linkIdentityUseCase;
    private final AuthenticationSuccessHandler delegate =
            new SavedRequestAwareAuthenticationSuccessHandler();

    public LinkAccountSuccessHandler(LinkIdentityUseCase linkIdentityUseCase) {
        this.linkIdentityUseCase = linkIdentityUseCase;
    }

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException, ServletException {
        HttpSession session = request.getSession(false);

        if (!LinkAccountSession.isActive(session)) {
            delegate.onAuthenticationSuccess(request, response, authentication);
            return;
        }

        UUID userUuid = LinkAccountSession.linkedUserUuid(session);
        String provider = LinkAccountSession.provider(session);
        Authentication original = LinkAccountSession.originalAuthentication(session);

        try {
            if (!(authentication.getPrincipal() instanceof OAuth2User providerUser)) {
                throw new IllegalStateException("Provider did not return a profile");
            }

            Map<String, Object> attributes = providerUser.getAttributes();

            linkIdentityUseCase.execute(
                    userUuid,
                    FederatedPrincipalFactory.toCommand(
                            FederatedPrincipalFactory.providerOf(provider),
                            attributes
                    )
            );

            redirect(response, "/profile/accounts?linked=" + provider);
        } catch (RuntimeException ex) {
            LOG.warn("Unable to link " + provider + " account", ex);
            redirect(response, "/profile/accounts?error=" + provider);
        } finally {
            LinkAccountSession.clear(session);

            if (original != null) {
                SecurityContext context = SecurityContextHolder.createEmptyContext();
                context.setAuthentication(original);
                SecurityContextHolder.setContext(context);

                new HttpSessionSecurityContextRepository()
                        .saveContext(context, request, response);
            }
        }
    }

    private void redirect(HttpServletResponse response, String location)
            throws IOException {
        response.sendRedirect(location);
    }

    /**
     * Convenience for the user service so the same attribute mapping is used in both
     * the login and the link flows.
     */
    public static FederatedIdentityCommand toCommand(String registrationId,
                                                     Map<String, Object> attributes) {
        return FederatedPrincipalFactory.toCommand(
                FederatedPrincipalFactory.providerOf(registrationId),
                attributes
        );
    }
}

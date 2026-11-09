package kh.edu.istad.platform.iaminfrastructure.security;

import kh.edu.istad.platform.iamapplicationservice.port.out.CurrentActorProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class SecurityCurrentActorProvider implements CurrentActorProvider {

    @Override
    public String getCurrentActor() {
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null
                || !authentication.isAuthenticated()
                || "anonymousUser".equals(authentication.getName())) {
            return "system";
        }

        String actor = authentication.getName();

        return actor == null || actor.isBlank()
                ? "system"
                : actor;
    }
}

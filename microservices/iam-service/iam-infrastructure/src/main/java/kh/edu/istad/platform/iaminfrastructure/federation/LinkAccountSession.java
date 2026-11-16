package kh.edu.istad.platform.iaminfrastructure.federation;

import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.Authentication;

import java.util.UUID;

/**
 * Session state for the "link a social account to my profile" flow. While a link is
 * in progress the OAuth2 login must not sign the browser in as a different user, so the
 * original authentication is parked here until the callback completes.
 */
public final class LinkAccountSession {

    static final String LINK_UUID = "IAM_LINK_UUID";
    static final String LINK_PROVIDER = "IAM_LINK_PROVIDER";
    static final String ORIGINAL_AUTH = "IAM_LINK_ORIGINAL_AUTH";

    private LinkAccountSession() {
    }

    public static void start(
            HttpSession session,
            UUID userUuid,
            String provider,
            Authentication original
    ) {
        session.setAttribute(LINK_UUID, userUuid.toString());
        session.setAttribute(LINK_PROVIDER, provider);
        session.setAttribute(ORIGINAL_AUTH, original);
    }

    public static boolean isActive(HttpSession session) {
        return session != null && session.getAttribute(LINK_UUID) != null;
    }

    public static UUID linkedUserUuid(HttpSession session) {
        Object value = session.getAttribute(LINK_UUID);
        return value == null ? null : UUID.fromString(value.toString());
    }

    public static String provider(HttpSession session) {
        Object value = session.getAttribute(LINK_PROVIDER);
        return value == null ? null : value.toString();
    }

    public static Authentication originalAuthentication(HttpSession session) {
        Object value = session.getAttribute(ORIGINAL_AUTH);
        return value instanceof Authentication authentication ? authentication : null;
    }

    public static void clear(HttpSession session) {
        if (session == null) {
            return;
        }

        session.removeAttribute(LINK_UUID);
        session.removeAttribute(LINK_PROVIDER);
        session.removeAttribute(ORIGINAL_AUTH);
    }
}

package kh.edu.istad.platform.iaminfrastructure.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kh.edu.istad.platform.iamapplicationservice.port.out.UserRepository;
import kh.edu.istad.platform.iamapplicationservice.port.out.SystemSettingsRepository;
import kh.edu.istad.platform.iamapplicationservice.port.out.UserSessionRepository;
import kh.edu.istad.platform.iamdomaincore.model.User;
import kh.edu.istad.platform.iamdomaincore.model.UserSession;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.Optional;

/**
 * Keeps the "active sessions" list up to date. Every authenticated request is matched to a
 * session row keyed by user + device + address; a new row is created the first time a
 * device appears and the last-activity timestamp is refreshed at most once a minute.
 *
 * <p>Works for both session-based logins and bearer tokens, so the REST client and the
 * browser produce the same data.
 */
public class SessionTrackingFilter extends OncePerRequestFilter {

    private static final Log LOG = LogFactory.getLog(SessionTrackingFilter.class);
    private static final Duration REFRESH_INTERVAL = Duration.ofMinutes(1);

    /**
     * Request attribute carrying the caller's own session key, so controllers can flag
     * "this device" and revoke every other session.
     */
    public static final String SESSION_KEY_ATTRIBUTE = "iam.sessionKey";

    private final UserRepository userRepository;
    private final UserSessionRepository userSessionRepository;
    private final SystemSettingsRepository systemSettingsRepository;

    public SessionTrackingFilter(
            UserRepository userRepository,
            UserSessionRepository userSessionRepository,
            SystemSettingsRepository systemSettingsRepository
    ) {
        this.userRepository = userRepository;
        this.userSessionRepository = userSessionRepository;
        this.systemSettingsRepository = systemSettingsRepository;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();

        return path.startsWith("/css/")
                || path.startsWith("/js/")
                || path.startsWith("/assets/")
                || path.startsWith("/actuator/")
                || path.equals("/login")
                || path.equals("/register");
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        try {
            track(request);
        } catch (RuntimeException ex) {
            // Session bookkeeping must never break the request.
            LOG.warn("Unable to record session activity", ex);
        }

        filterChain.doFilter(request, response);
    }

    private boolean isExpired(UserSession session, OffsetDateTime now) {
        SystemSettingsRepository.SystemSettings settings =
                systemSettingsRepository.load();

        if (settings.sessionIdleTimeoutMinutes() > 0
                && session.lastActivityAt() != null
                && session.lastActivityAt()
                        .plusMinutes(settings.sessionIdleTimeoutMinutes())
                        .isBefore(now)) {
            return true;
        }

        return settings.sessionAbsoluteTimeoutHours() > 0
                && session.createdAt()
                        .plusHours(settings.sessionAbsoluteTimeoutHours())
                        .isBefore(now);
    }

    /**
     * Resolves the user from the principal name, falling back to the token subject so a
     * rename does not break tracking for tokens issued before the change.
     */
    private Optional<User> resolveUser(Authentication authentication, String username) {
        Optional<User> byName = userRepository.findByUsernameOrEmail(username);

        if (byName.isPresent()) {
            return byName;
        }

        if (authentication.getPrincipal() instanceof Jwt jwt
                && jwt.getSubject() != null) {
            try {
                return userRepository.findByUuid(
                        java.util.UUID.fromString(jwt.getSubject())
                );
            } catch (IllegalArgumentException ignored) {
                return Optional.empty();
            }
        }

        return Optional.empty();
    }

    private void track(HttpServletRequest request) {
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return;
        }

        String username = authentication.getName();

        if (username == null || username.isBlank()
                || "anonymousUser".equals(username)) {
            return;
        }

        Optional<User> found = resolveUser(authentication, username);

        if (found.isEmpty() || found.get().deleted()) {
            return;
        }

        User user = found.get();
        ClientDeviceInfo device = ClientDeviceInfo.from(request);
        String sessionKey = device.sessionKey(username);
        OffsetDateTime now = OffsetDateTime.now();

        request.setAttribute(SESSION_KEY_ATTRIBUTE, sessionKey);

        Optional<UserSession> existing =
                userSessionRepository.findActiveBySessionKey(sessionKey);

        if (existing.isPresent()) {
            UserSession session = existing.get();

            // Enforce the configured idle and absolute timeouts before refreshing.
            if (isExpired(session, now)) {
                userSessionRepository.save(session.revoke(
                        "system",
                        now,
                        "Session expired (idle or absolute timeout)"
                ));
                return;
            }

            if (session.lastActivityAt() == null
                    || session.lastActivityAt().isBefore(now.minus(REFRESH_INTERVAL))) {
                userSessionRepository.save(session.touch(now));
            }

            return;
        }

        userSessionRepository.save(new UserSession(
                null,
                null,
                user.id(),
                sessionKey,
                device.device(),
                device.browser(),
                device.operatingSystem(),
                device.ipAddress(),
                device.location(),
                device.userAgent(),
                now,
                now,
                null,
                null,
                null,
                null
        ));
    }
}

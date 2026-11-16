package kh.edu.istad.platform.iamrestapi.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.util.NoSuchElementException;
import java.util.UUID;

/**
 * Resolves the caller's public identifier. For bearer tokens the {@code sub} claim
 * carries the {@code users.uuid} value (see the OIDC claim mapping).
 */
public final class AuthenticatedUser {

    private AuthenticatedUser() {
    }

    public static UUID currentUuid() {
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication instanceof JwtAuthenticationToken token) {
            return uuidOf(token.getToken());
        }

        if (authentication != null
                && authentication.getPrincipal() instanceof Jwt jwt) {
            return uuidOf(jwt);
        }

        throw new NoSuchElementException("Authenticated user is required");
    }

    private static UUID uuidOf(Jwt jwt) {
        String subject = jwt.getSubject();

        if (subject == null || subject.isBlank()) {
            throw new NoSuchElementException("Token subject is missing");
        }

        try {
            return UUID.fromString(subject);
        } catch (IllegalArgumentException ex) {
            throw new IllegalStateException(
                    "Token subject is not a user identifier", ex
            );
        }
    }
}

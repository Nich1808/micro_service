package kh.edu.istad.platform.iamrestapi.security;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The dashboard is a browser SPA: besides {@code /api/**} it also calls the authorization
 * server endpoints cross-origin (token exchange, refresh, discovery). A missing CORS rule
 * there surfaces as "Failed to fetch" and breaks sign-in completely.
 */
class CorsConfigTest {

    private static final String ORIGIN = "http://localhost:5173";

    private final CorsConfigurationSource source =
            new ApiSecurityConfig().corsConfigurationSource(ORIGIN + ",http://localhost:3000");

    @Test
    void tokenEndpointAllowsTheDashboardOrigin() {
        CorsConfiguration config = configFor("/oauth2/token");

        assertNotNull(config, "CORS must be configured for /oauth2/token");
        assertNotNull(config.checkOrigin(ORIGIN),
                "The dashboard origin must be allowed to exchange the code");
        assertTrue(config.getAllowedMethods().contains("POST"));
    }

    @Test
    void apiAllowsTheDashboardOrigin() {
        CorsConfiguration config = configFor("/api/me");

        assertNotNull(config);
        assertNotNull(config.checkOrigin(ORIGIN));
        assertTrue(config.getAllowedHeaders().contains("Authorization"));
    }

    @Test
    void discoveryDocumentIsReadable() {
        CorsConfiguration config = configFor("/.well-known/openid-configuration");

        assertNotNull(config);
        assertNotNull(config.checkOrigin(ORIGIN));
    }

    @Test
    void unknownOriginsAreRejected() {
        CorsConfiguration config = configFor("/api/me");

        assertNotNull(config);
        assertNull(config.checkOrigin("http://evil.example.com"));
    }

    @Test
    void userinfoIsReadable() {
        CorsConfiguration config = configFor("/userinfo");

        assertNotNull(config);
        assertNotNull(config.checkOrigin(ORIGIN));
    }

    private CorsConfiguration configFor(String path) {
        MockHttpServletRequest request = new MockHttpServletRequest("POST", path);
        return source.getCorsConfiguration(request);
    }
}

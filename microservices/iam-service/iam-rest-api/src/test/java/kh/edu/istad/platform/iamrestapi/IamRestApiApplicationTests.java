package kh.edu.istad.platform.iamrestapi;

import kh.edu.istad.platform.iamrestapi.security.JwtAuthoritiesConverter;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.time.Instant;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class IamRestApiApplicationTests {

    @Test
    void authoritiesClaimBecomesGrantedAuthorities() {
        Jwt jwt = Jwt.withTokenValue("token")
                .header("alg", "RS256")
                .subject("7f3d2a10-91c4-4b0e-8d6f-2a9c5e1b7d40")
                .claim("authorities", List.of("ROLE_ADMIN", "user:read", "role:read"))
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(600))
                .build();

        Collection<GrantedAuthority> authorities =
                new JwtAuthoritiesConverter().convert(jwt);

        assertEquals(3, authorities.size());
        assertTrue(authorities.stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")));
        assertTrue(authorities.stream()
                .anyMatch(a -> a.getAuthority().equals("user:read")));
    }

    @Test
    void missingClaimYieldsNoAuthorities() {
        Jwt jwt = Jwt.withTokenValue("token")
                .header("alg", "RS256")
                .subject("subject")
                .issuedAt(Instant.now())
                .build();

        assertTrue(new JwtAuthoritiesConverter().convert(jwt).isEmpty());
    }
}

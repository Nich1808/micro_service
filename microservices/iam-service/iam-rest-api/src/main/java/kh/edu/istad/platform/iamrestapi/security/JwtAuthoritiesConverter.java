package kh.edu.istad.platform.iamrestapi.security;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Reads the {@code authorities} claim produced by the authorization server, which
 * contains {@code ROLE_<name>} entries plus the effective permission names.
 */
public class JwtAuthoritiesConverter
        implements Converter<Jwt, Collection<GrantedAuthority>> {

    @Override
    public Collection<GrantedAuthority> convert(Jwt jwt) {
        Set<GrantedAuthority> authorities = new LinkedHashSet<>();

        List<String> claims = jwt.getClaimAsStringList("authorities");

        if (claims != null) {
            claims.stream()
                    .filter(value -> value != null && !value.isBlank())
                    .map(SimpleGrantedAuthority::new)
                    .forEach(authorities::add);
        }

        return authorities;
    }
}

package kh.edu.istad.platform.iaminfrastructure.token;

import kh.edu.istad.platform.iamapplicationservice.port.out.UserRepository;
import kh.edu.istad.platform.iamdomaincore.model.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Maps IAM domain data into OIDC claims and adds the authorization model
 * (roles and effective permissions) to access tokens.
 */
public class IamTokenCustomizer implements OAuth2TokenCustomizer<JwtEncodingContext> {

    private final UserRepository userRepository;

    public IamTokenCustomizer(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void customize(JwtEncodingContext context) {
        Authentication authentication = context.getPrincipal();

        if (authentication == null || !authentication.isAuthenticated()) {
            return;
        }

        User user = userRepository
                .findByUsernameOrEmail(authentication.getName())
                .orElse(null);

        if (user == null) {
            return;
        }

        // OIDC sub = users.uuid
        context.getClaims().subject(user.uuid().toString());

        if (OAuth2TokenType.ACCESS_TOKEN.equals(context.getTokenType())) {
            addAuthorities(context, authentication);
        }

        Set<String> scopes = context.getAuthorizedScopes();

        if (scopes.contains(OidcScopes.PROFILE)) {
            addProfileClaims(context, user);
        }

        if (scopes.contains(OidcScopes.EMAIL)) {
            context.getClaims()
                    .claim("email", user.email())
                    .claim("email_verified", user.emailVerified());
        }

        if (scopes.contains(OidcScopes.PHONE)) {
            context.getClaims()
                    .claim("phone_number", user.phoneNumber())
                    .claim("phone_number_verified", user.phoneNumberVerified());
        }

        if (scopes.contains(OidcScopes.ADDRESS)) {
            context.getClaims().claim("address", addressOf(user));
        }
    }

    private void addAuthorities(
            JwtEncodingContext context,
            Authentication authentication
    ) {
        List<String> roles = new ArrayList<>();
        List<String> permissions = new ArrayList<>();

        for (GrantedAuthority authority : authentication.getAuthorities()) {
            String value = authority.getAuthority();

            // "FACTOR_*" entries are authentication-factor markers added by Spring
            // Security, not business permissions, so they are not published.
            if (value == null || value.isBlank() || value.startsWith("FACTOR_")) {
                continue;
            }

            if (value.startsWith("ROLE_")) {
                roles.add(value.substring("ROLE_".length()));
            } else {
                permissions.add(value);
            }
        }

        LinkedHashSet<String> authorities = new LinkedHashSet<>();
        roles.forEach(role -> authorities.add("ROLE_" + role));
        authorities.addAll(permissions);

        context.getClaims()
                .claim("roles", roles)
                .claim("permissions", permissions)
                .claim("authorities", new ArrayList<>(authorities));
    }

    private void addProfileClaims(JwtEncodingContext context, User user) {
        Map<String, Object> claims = new LinkedHashMap<>();

        put(claims, "name", user.name());
        put(claims, "given_name", user.givenName());
        put(claims, "family_name", user.familyName());
        put(claims, "middle_name", user.middleName());
        put(claims, "nickname", user.nickname());
        put(claims, "preferred_username", user.username());
        put(claims, "profile", user.profileUrl());
        put(claims, "picture", user.pictureUrl());
        put(claims, "website", user.websiteUrl());
        put(claims, "gender", user.gender());
        put(claims, "birthdate", user.birthdate() == null
                ? null
                : user.birthdate().toString());
        put(claims, "zoneinfo", user.zoneinfo());
        put(claims, "locale", user.locale());
        put(claims, "updated_at", user.updatedAt() == null
                ? null
                : user.updatedAt().toEpochSecond());

        context.getClaims().claims(existing -> existing.putAll(claims));
    }

    private Map<String, Object> addressOf(User user) {
        Map<String, Object> address = new LinkedHashMap<>();
        put(address, "street_address", user.streetAddress());
        put(address, "locality", user.locality());
        put(address, "region", user.region());
        put(address, "postal_code", user.postalCode());
        put(address, "country", user.country());
        return address;
    }

    /**
     * JWT claims cannot hold null values, so absent OIDC fields are simply omitted.
     */
    private static void put(Map<String, Object> claims, String key, Object value) {
        if (value != null) {
            claims.put(key, value);
        }
    }
}

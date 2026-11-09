
package kh.edu.istad.platform.iamdomaincore.model;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Locale;
import java.util.UUID;

public record User(
        Long id,
        UUID uuid,
        String username,
        String email,
        boolean emailVerified,
        String passwordHash,

        String givenName,
        String familyName,
        String middleName,
        String nickname,
        String gender,
        LocalDate birthdate,
        String profileUrl,
        String pictureUrl,
        String websiteUrl,
        String zoneinfo,
        String locale,

        String phoneNumber,
        boolean phoneNumberVerified,

        String streetAddress,
        String locality,
        String region,
        String postalCode,
        String country,

        boolean enabled,
        boolean accountNonExpired,
        boolean accountNonLocked,
        boolean credentialsNonExpired,

        int failedLoginAttempts,
        OffsetDateTime lastLoginAt,
        OffsetDateTime passwordChangedAt,

        boolean deleted,
        OffsetDateTime deletedAt,
        String deletedBy,

        OffsetDateTime createdAt,
        String createdBy,
        OffsetDateTime updatedAt,
        String updatedBy,

        long version
) {

    public User {
        // UUID
        if (uuid == null) {
            uuid = UUID.randomUUID();
        }

        // Username
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Username is required");
        }

        username = username.trim();

        if (username.length() > 50) {
            throw new IllegalArgumentException(
                    "Username must not exceed 50 characters"
            );
        }

        // Email
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email is required");
        }

        email = email.trim().toLowerCase(Locale.ROOT);

        if (email.length() > 255 ||
                !email.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
            throw new IllegalArgumentException("Invalid email address");
        }

        // Password
        if (passwordHash != null && passwordHash.isBlank()) {
            passwordHash = null;
        }

        // Profile
        if (givenName == null || givenName.isBlank()) {
            throw new IllegalArgumentException("Given name is required");
        }

        if (familyName == null || familyName.isBlank()) {
            throw new IllegalArgumentException("Family name is required");
        }

        givenName = givenName.trim();
        familyName = familyName.trim();

        // Locale
        if (locale == null || locale.isBlank()) {
            locale = "en";
        } else {
            locale = locale.trim().toLowerCase(Locale.ROOT);
        }

        if (!locale.equals("en") && !locale.equals("km")) {
            throw new IllegalArgumentException(
                    "Locale must be en or km"
            );
        }

        // Security
        if (failedLoginAttempts < 0) {
            throw new IllegalArgumentException(
                    "Failed login attempts cannot be negative"
            );
        }

        if (version < 0) {
            throw new IllegalArgumentException(
                    "Version cannot be negative"
            );
        }

        // Soft delete
        if (deleted) {
            if (deletedAt == null) {
                throw new IllegalArgumentException(
                        "Deleted time is required"
                );
            }

            if (deletedBy == null || deletedBy.isBlank()) {
                throw new IllegalArgumentException(
                        "Deleted by is required"
                );
            }
        } else if (deletedAt != null || deletedBy != null) {
            throw new IllegalArgumentException(
                    "Active user cannot have deletion metadata"
            );
        }
    }

    // Full name
    public String name() {
        return (givenName + " " + familyName).trim();
    }

    // Account status
    public boolean isAccountUsable() {
        return !deleted
                && enabled
                && accountNonExpired
                && accountNonLocked
                && credentialsNonExpired;
    }

    public boolean isEnabled() {
        return enabled && !deleted;
    }

    public void validateAccountUsable() {
        if (!isAccountUsable()) {
            throw new IllegalStateException(
                    "User account is not usable"
            );
        }
    }

    // Password validation
    public void validatePasswordAvailable() {
        if (passwordHash == null || passwordHash.isBlank()) {
            throw new IllegalStateException(
                    "User has no password configured"
            );
        }
    }

    // Spring Security compatible accessor names.
    // No Spring dependency is required.
    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return passwordHash;
    }

    public boolean isAccountNonExpired() {
        return accountNonExpired;
    }

    public boolean isAccountNonLocked() {
        return accountNonLocked;
    }

    public boolean isCredentialsNonExpired() {
        return credentialsNonExpired;
    }

    // Soft delete
    public User softDelete(
            String actor,
            OffsetDateTime now
    ) {
        if (deleted) {
            throw new IllegalStateException(
                    "User is already deleted"
            );
        }

        if (actor == null || actor.isBlank()) {
            throw new IllegalArgumentException(
                    "Deleted by is required"
            );
        }

        if (now == null) {
            throw new IllegalArgumentException(
                    "Deletion time is required"
            );
        }

        return new User(
                id,
                uuid,
                username,
                email,
                emailVerified,
                passwordHash,
                givenName,
                familyName,
                middleName,
                nickname,
                gender,
                birthdate,
                profileUrl,
                pictureUrl,
                websiteUrl,
                zoneinfo,
                locale,
                phoneNumber,
                phoneNumberVerified,
                streetAddress,
                locality,
                region,
                postalCode,
                country,
                enabled,
                accountNonExpired,
                accountNonLocked,
                credentialsNonExpired,
                failedLoginAttempts,
                lastLoginAt,
                passwordChangedAt,
                true,
                now,
                actor,
                createdAt,
                createdBy,
                now,
                actor,
                version + 1
        );
    }
}

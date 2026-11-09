package kh.edu.istad.platform.iamapplicationservice.service.user;

import kh.edu.istad.platform.iamdomaincore.model.User;

import java.time.OffsetDateTime;

final class UserPasswordSupport {

    private UserPasswordSupport() {}

    static User updatePassword(
            User user,
            String passwordHash,
            String actor,
            OffsetDateTime now
    ) {
        return new User(
                user.id(), user.uuid(),
                user.username(), user.email(), user.emailVerified(),
                passwordHash,
                user.givenName(), user.familyName(),
                user.middleName(), user.nickname(), user.gender(),
                user.birthdate(), user.profileUrl(), user.pictureUrl(),
                user.websiteUrl(), user.zoneinfo(), user.locale(),
                user.phoneNumber(), user.phoneNumberVerified(),
                user.streetAddress(), user.locality(), user.region(),
                user.postalCode(), user.country(),
                user.enabled(), user.accountNonExpired(),
                user.accountNonLocked(), user.credentialsNonExpired(),
                user.failedLoginAttempts(),
                user.lastLoginAt(), now,
                user.deleted(), user.deletedAt(), user.deletedBy(),
                user.createdAt(), user.createdBy(),
                now, actor,
                user.version() + 1
        );
    }
}

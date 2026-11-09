package kh.edu.istad.platform.iamapplicationservice.service.user;

import kh.edu.istad.platform.iamdomaincore.model.User;

import java.time.OffsetDateTime;
import java.util.UUID;

final class UserStateSupport {

    private UserStateSupport() {}

    static User requireActiveUser(
            kh.edu.istad.platform.iamapplicationservice.port.out.UserRepository repository,
            UUID uuid
    ) {
        if (uuid == null) {
            throw new IllegalArgumentException("User UUID is required");
        }

        return repository.findByUuid(uuid)
                .filter(user -> !user.deleted())
                .orElseThrow(() ->
                        new IllegalArgumentException("User not found"));
    }

    static User changeStatus(
            User user,
            boolean enabled,
            boolean accountNonLocked,
            String actor,
            OffsetDateTime now
    ) {
        return new User(
                user.id(), user.uuid(),
                user.username(), user.email(), user.emailVerified(),
                user.passwordHash(),
                user.givenName(), user.familyName(),
                user.middleName(), user.nickname(), user.gender(),
                user.birthdate(), user.profileUrl(), user.pictureUrl(),
                user.websiteUrl(), user.zoneinfo(), user.locale(),
                user.phoneNumber(), user.phoneNumberVerified(),
                user.streetAddress(), user.locality(), user.region(),
                user.postalCode(), user.country(),
                enabled,
                user.accountNonExpired(),
                accountNonLocked,
                user.credentialsNonExpired(),
                user.failedLoginAttempts(),
                user.lastLoginAt(), user.passwordChangedAt(),
                user.deleted(), user.deletedAt(), user.deletedBy(),
                user.createdAt(), user.createdBy(),
                now, actor,
                user.version() + 1
        );
    }
}

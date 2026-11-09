package kh.edu.istad.platform.iamapplicationservice.service.user;

import kh.edu.istad.platform.iamapplicationservice.port.out.CurrentActorProvider;
import kh.edu.istad.platform.iamapplicationservice.port.out.UserRepository;
import kh.edu.istad.platform.iamapplicationservice.usecase.user.RecordLoginAttemptUseCase;
import kh.edu.istad.platform.iamdomaincore.model.User;

import java.time.OffsetDateTime;

public class RecordLoginAttemptService
        implements RecordLoginAttemptUseCase {

    private final UserRepository userRepository;
    private final CurrentActorProvider currentActorProvider;

    public RecordLoginAttemptService(
            UserRepository userRepository,
            CurrentActorProvider currentActorProvider
    ) {
        this.userRepository = userRepository;
        this.currentActorProvider = currentActorProvider;
    }

    @Override
    public void execute(String usernameOrEmail, boolean success) {
        if (usernameOrEmail == null || usernameOrEmail.isBlank()) {
            throw new IllegalArgumentException(
                    "Username or email is required"
            );
        }

        userRepository.findByUsernameOrEmail(usernameOrEmail.trim())
                .filter(user -> !user.deleted())
                .ifPresent(user -> {
                    OffsetDateTime now = OffsetDateTime.now();
                    String actor = currentActorProvider.getCurrentActor();

                    int failedAttempts = success
                            ? 0
                            : Math.addExact(user.failedLoginAttempts(), 1);

                    OffsetDateTime lastLoginAt = success
                            ? now
                            : user.lastLoginAt();

                    User updatedUser = new User(
                            user.id(), user.uuid(),
                            user.username(), user.email(),
                            user.emailVerified(), user.passwordHash(),
                            user.givenName(), user.familyName(),
                            user.middleName(), user.nickname(),
                            user.gender(), user.birthdate(),
                            user.profileUrl(), user.pictureUrl(),
                            user.websiteUrl(), user.zoneinfo(),
                            user.locale(),
                            user.phoneNumber(), user.phoneNumberVerified(),
                            user.streetAddress(), user.locality(),
                            user.region(), user.postalCode(),
                            user.country(),
                            user.enabled(),
                            user.accountNonExpired(),
                            user.accountNonLocked(),
                            user.credentialsNonExpired(),
                            failedAttempts,
                            lastLoginAt,
                            user.passwordChangedAt(),
                            user.deleted(),
                            user.deletedAt(),
                            user.deletedBy(),
                            user.createdAt(),
                            user.createdBy(),
                            now,
                            actor,
                            user.version() + 1
                    );

                    userRepository.save(updatedUser);
                });
    }
}

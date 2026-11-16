package kh.edu.istad.platform.iamapplicationservice.service.user;

import kh.edu.istad.platform.iamapplicationservice.command.user.UpdateUserProfileCommand;
import kh.edu.istad.platform.iamapplicationservice.port.out.CurrentActorProvider;
import kh.edu.istad.platform.iamapplicationservice.port.out.UserRepository;
import kh.edu.istad.platform.iamapplicationservice.result.user.UserResult;
import kh.edu.istad.platform.iamapplicationservice.usecase.user.UpdateUserProfileUseCase;
import kh.edu.istad.platform.iamdomaincore.model.User;

import java.time.OffsetDateTime;
import java.util.List;

public class UpdateUserProfileService implements UpdateUserProfileUseCase {

    private final UserRepository userRepository;
    private final CurrentActorProvider actorProvider;

    public UpdateUserProfileService(
            UserRepository userRepository,
            CurrentActorProvider actorProvider
    ) {
        this.userRepository = userRepository;
        this.actorProvider = actorProvider;
    }

    @Override
    public UserResult execute(UpdateUserProfileCommand command) {
        if (command == null) {
            throw new IllegalArgumentException("Update user profile command is required");
        }

        if (command.userUuid() == null) {
            throw new IllegalArgumentException("User UUID is required");
        }

        User user = UserStateSupport.requireActiveUser(userRepository, command.userUuid());

        String actor = actorProvider.getCurrentActor();

        if (actor == null || actor.isBlank()) {
            throw new IllegalStateException("Current actor is required");
        }

        OffsetDateTime now = OffsetDateTime.now();

        User updated = new User(
                user.id(), user.uuid(),
                user.username(), user.email(), user.emailVerified(), user.passwordHash(),
                or(command.givenName(), user.givenName()),
                or(command.familyName(), user.familyName()),
                or(command.middleName(), user.middleName()),
                or(command.nickname(), user.nickname()),
                or(command.gender(), user.gender()),
                or(command.birthdate(), user.birthdate()),
                or(command.profileUrl(), user.profileUrl()),
                or(command.pictureUrl(), user.pictureUrl()),
                or(command.websiteUrl(), user.websiteUrl()),
                or(command.zoneinfo(), user.zoneinfo()),
                or(command.locale(), user.locale()),
                or(command.phoneNumber(), user.phoneNumber()),
                user.phoneNumberVerified(),
                or(command.streetAddress(), user.streetAddress()),
                or(command.locality(), user.locality()),
                or(command.region(), user.region()),
                or(command.postalCode(), user.postalCode()),
                or(command.country(), user.country()),
                user.enabled(),
                user.accountNonExpired(),
                user.accountNonLocked(),
                user.credentialsNonExpired(),
                user.failedLoginAttempts(),
                user.lastLoginAt(),
                user.passwordChangedAt(),
                user.deleted(), user.deletedAt(), user.deletedBy(),
                user.createdAt(), user.createdBy(),
                now, actor,
                user.version() + 1
        );

        User saved = userRepository.save(updated);

        return new UserResult(
                saved.uuid(),
                saved.username(),
                saved.email(),
                saved.emailVerified(),
                saved.name(),
                saved.locale(),
                saved.enabled(),
                List.of(),
                saved.createdAt(),
                saved.updatedAt()
        );
    }

    private static <T> T or(T value, T fallback) {
        return value != null ? value : fallback;
    }
}

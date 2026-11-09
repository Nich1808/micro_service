package kh.edu.istad.platform.iamapplicationservice.service.user;

import kh.edu.istad.platform.iamapplicationservice.command.user.ChangePasswordCommand;
import kh.edu.istad.platform.iamapplicationservice.port.out.CurrentActorProvider;
import kh.edu.istad.platform.iamapplicationservice.port.out.PasswordHasher;
import kh.edu.istad.platform.iamapplicationservice.port.out.UserRepository;
import kh.edu.istad.platform.iamapplicationservice.usecase.user.ChangePasswordUseCase;
import kh.edu.istad.platform.iamdomaincore.model.User;

import java.time.OffsetDateTime;

public class ChangePasswordService implements ChangePasswordUseCase {

    private final UserRepository repository;
    private final PasswordHasher passwordHasher;
    private final CurrentActorProvider actorProvider;

    public ChangePasswordService(
            UserRepository repository,
            PasswordHasher passwordHasher,
            CurrentActorProvider actorProvider
    ) {
        this.repository = repository;
        this.passwordHasher = passwordHasher;
        this.actorProvider = actorProvider;
    }

    @Override
    public void execute(ChangePasswordCommand command) {
        if (command == null) {
            throw new IllegalArgumentException("Change password command is required");
        }

        if (command.currentPassword() == null || command.currentPassword().isBlank()) {
            throw new IllegalArgumentException("Current password is required");
        }

        if (command.newPassword() == null || command.newPassword().isBlank()) {
            throw new IllegalArgumentException("New password is required");
        }

        User user = UserStateSupport.requireActiveUser(
                repository, command.userUuid()
        );

        user.validatePasswordAvailable();

        if (!passwordHasher.matches(
                command.currentPassword(), user.passwordHash()
        )) {
            throw new IllegalArgumentException("Current password is incorrect");
        }

        String passwordHash = passwordHasher.hash(command.newPassword());

        if (passwordHash == null || passwordHash.isBlank()) {
            throw new IllegalStateException("Password hashing failed");
        }

        String actor = actorProvider.getCurrentActor();

        if (actor == null || actor.isBlank()) {
            throw new IllegalStateException("Current actor is required");
        }

        repository.save(UserPasswordSupport.updatePassword(
                user, passwordHash, actor, OffsetDateTime.now()
        ));
    }
}

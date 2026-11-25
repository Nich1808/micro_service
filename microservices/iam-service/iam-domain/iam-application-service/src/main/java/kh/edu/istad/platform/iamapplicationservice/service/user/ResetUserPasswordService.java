package kh.edu.istad.platform.iamapplicationservice.service.user;

import kh.edu.istad.platform.iamapplicationservice.command.user.ResetUserPasswordCommand;
import kh.edu.istad.platform.iamapplicationservice.port.out.AccountActivityRepository;
import kh.edu.istad.platform.iamapplicationservice.port.out.CurrentActorProvider;
import kh.edu.istad.platform.iamapplicationservice.port.out.PasswordHasher;
import kh.edu.istad.platform.iamapplicationservice.port.out.UserRepository;
import kh.edu.istad.platform.iamapplicationservice.service.security.ActivitySupport;
import kh.edu.istad.platform.iamapplicationservice.service.security.PasswordPolicy;
import kh.edu.istad.platform.iamapplicationservice.usecase.user.ResetUserPasswordUseCase;
import kh.edu.istad.platform.iamdomaincore.enums.ActivityType;
import kh.edu.istad.platform.iamdomaincore.model.User;

import java.time.OffsetDateTime;

public class ResetUserPasswordService implements ResetUserPasswordUseCase {

    private final UserRepository repository;
    private final PasswordHasher passwordHasher;
    private final CurrentActorProvider actorProvider;
    private final PasswordPolicy passwordPolicy;
    private final AccountActivityRepository accountActivityRepository;

    public ResetUserPasswordService(
            UserRepository repository,
            PasswordHasher passwordHasher,
            CurrentActorProvider actorProvider,
            PasswordPolicy passwordPolicy,
            AccountActivityRepository accountActivityRepository
    ) {
        this.repository = repository;
        this.passwordHasher = passwordHasher;
        this.actorProvider = actorProvider;
        this.passwordPolicy = passwordPolicy;
        this.accountActivityRepository = accountActivityRepository;
    }

    @Override
    public void execute(ResetUserPasswordCommand command) {
        if (command == null) {
            throw new IllegalArgumentException("Reset password command is required");
        }

        if (command.newPassword() == null || command.newPassword().isBlank()) {
            throw new IllegalArgumentException("New password is required");
        }

        User user = UserStateSupport.requireActiveUser(
                repository, command.userUuid()
        );

        passwordPolicy.validate(command.newPassword());

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

        ActivitySupport.record(
                accountActivityRepository, user.id(),
                ActivityType.PASSWORD_RESET,
                "Password reset by an administrator", actor
        );
    }
}

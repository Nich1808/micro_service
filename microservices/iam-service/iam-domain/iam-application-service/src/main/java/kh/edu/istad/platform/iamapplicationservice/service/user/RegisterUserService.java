package kh.edu.istad.platform.iamapplicationservice.service.user;

import kh.edu.istad.platform.iamapplicationservice.command.user.RegisterUserCommand;
import kh.edu.istad.platform.iamapplicationservice.port.out.CurrentActorProvider;
import kh.edu.istad.platform.iamapplicationservice.port.out.PasswordHasher;
import kh.edu.istad.platform.iamapplicationservice.port.out.UserRepository;
import kh.edu.istad.platform.iamapplicationservice.result.user.UserResult;
import kh.edu.istad.platform.iamapplicationservice.usecase.user.RegisterUserUseCase;
import kh.edu.istad.platform.iamdomaincore.model.User;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Locale;

public class RegisterUserService implements RegisterUserUseCase {

    private final UserRepository userRepository;
    private final PasswordHasher passwordHasher;
    private final CurrentActorProvider currentActorProvider;

    public RegisterUserService(
            UserRepository userRepository,
            PasswordHasher passwordHasher,
            CurrentActorProvider currentActorProvider
    ) {
        this.userRepository = userRepository;
        this.passwordHasher = passwordHasher;
        this.currentActorProvider = currentActorProvider;
    }

    @Override
    public UserResult execute(RegisterUserCommand command) {

        if (command == null) {
            throw new IllegalArgumentException("Register command is required");
        }

        String username = command.username().trim();
        String email = command.email().trim().toLowerCase(Locale.ROOT);

        if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("Username already exists");
        }

        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email already exists");
        }

        if (command.rawPassword() == null || command.rawPassword().isBlank()) {
            throw new IllegalArgumentException("Password is required");
        }

        String passwordHash = passwordHasher.hash(command.rawPassword());

        if (passwordHash == null || passwordHash.isBlank()) {
            throw new IllegalStateException("Password hashing failed");
        }

        String actor = currentActorProvider.getCurrentActor();

        if (actor == null || actor.isBlank()) {
            throw new IllegalStateException("Current actor is required");
        }

        OffsetDateTime now = OffsetDateTime.now();

        User user = new User(
                null, null, username, email, false, passwordHash,
                command.givenName(), command.familyName(),
                null, null, null, null, null, null, null, null,
                "en", null, false, null, null, null, null, null,
                true, true, true, true,
                0, null, null, false, null, null,
                now, actor, now, actor, 0
        );

        User savedUser = userRepository.save(user);

        return new UserResult(
                savedUser.uuid(),
                savedUser.username(),
                savedUser.email(),
                savedUser.emailVerified(),
                savedUser.name(),
                savedUser.locale(),
                savedUser.enabled(),
                List.of(),
                savedUser.createdAt(),
                savedUser.updatedAt()
        );
    }
}

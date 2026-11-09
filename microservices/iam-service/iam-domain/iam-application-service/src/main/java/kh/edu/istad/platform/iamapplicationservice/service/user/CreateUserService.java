package kh.edu.istad.platform.iamapplicationservice.service.user;

import kh.edu.istad.platform.iamapplicationservice.command.user.CreateUserCommand;
import kh.edu.istad.platform.iamapplicationservice.port.out.CurrentActorProvider;
import kh.edu.istad.platform.iamapplicationservice.port.out.PasswordHasher;
import kh.edu.istad.platform.iamapplicationservice.port.out.UserRepository;
import kh.edu.istad.platform.iamapplicationservice.result.user.UserResult;
import kh.edu.istad.platform.iamapplicationservice.usecase.user.CreateUserUseCase;
import kh.edu.istad.platform.iamdomaincore.model.User;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Locale;

public class CreateUserService implements CreateUserUseCase {

    private final UserRepository userRepository;
    private final PasswordHasher passwordHasher;
    private final CurrentActorProvider currentActorProvider;

    public CreateUserService(
            UserRepository userRepository,
            PasswordHasher passwordHasher,
            CurrentActorProvider currentActorProvider
    ) {
        this.userRepository = userRepository;
        this.passwordHasher = passwordHasher;
        this.currentActorProvider = currentActorProvider;
    }

    @Override
    public UserResult execute(CreateUserCommand command) {
        if (command == null) {
            throw new IllegalArgumentException("Create user command is required");
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

        String actor = currentActorProvider.getCurrentActor();
        OffsetDateTime now = OffsetDateTime.now();

        User user = new User(
                null, null,
                username, email, false,
                passwordHasher.hash(command.rawPassword()),
                command.givenName(), command.familyName(),
                null, null, null, null, null, null, null, null,
                "en",
                null, false,
                null, null, null, null, null,
                true, true, true, true,
                0, null, null,
                false, null, null,
                now, actor, now, actor,
                0
        );

        User saved = userRepository.save(user);

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
}

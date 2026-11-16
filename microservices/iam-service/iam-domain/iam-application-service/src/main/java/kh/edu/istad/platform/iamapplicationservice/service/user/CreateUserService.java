package kh.edu.istad.platform.iamapplicationservice.service.user;

import kh.edu.istad.platform.iamapplicationservice.command.user.CreateUserCommand;
import kh.edu.istad.platform.iamapplicationservice.port.out.CurrentActorProvider;
import kh.edu.istad.platform.iamapplicationservice.port.out.PasswordHasher;
import kh.edu.istad.platform.iamapplicationservice.port.out.RoleRepository;
import kh.edu.istad.platform.iamapplicationservice.port.out.UserRepository;
import kh.edu.istad.platform.iamapplicationservice.port.out.UserRoleRepository;
import kh.edu.istad.platform.iamapplicationservice.result.role.RoleSummary;
import kh.edu.istad.platform.iamapplicationservice.result.user.UserResult;
import kh.edu.istad.platform.iamapplicationservice.usecase.user.CreateUserUseCase;
import kh.edu.istad.platform.iamdomaincore.model.Role;
import kh.edu.istad.platform.iamdomaincore.model.User;
import kh.edu.istad.platform.iamdomaincore.model.UserRole;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class CreateUserService implements CreateUserUseCase {

    private final UserRepository userRepository;
    private final PasswordHasher passwordHasher;
    private final CurrentActorProvider currentActorProvider;
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;

    public CreateUserService(
            UserRepository userRepository,
            PasswordHasher passwordHasher,
            CurrentActorProvider currentActorProvider,
            RoleRepository roleRepository,
            UserRoleRepository userRoleRepository
    ) {
        this.userRepository = userRepository;
        this.passwordHasher = passwordHasher;
        this.currentActorProvider = currentActorProvider;
        this.roleRepository = roleRepository;
        this.userRoleRepository = userRoleRepository;
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

        List<Role> roles = resolveRoles(command.roleUuids());

        String actor = currentActorProvider.getCurrentActor();

        if (actor == null || actor.isBlank()) {
            throw new IllegalStateException("Current actor is required");
        }

        OffsetDateTime now = OffsetDateTime.now();

        User user = new User(
                null, null,
                username, email, false,
                passwordHasher.hash(command.rawPassword()),
                command.givenName(), command.familyName(),
                null, null,
                command.gender(), command.birthdate(),
                null, null, null,
                command.zoneinfo(),
                command.locale(),
                command.phoneNumber(), false,
                command.streetAddress(), command.locality(),
                command.region(), command.postalCode(),
                command.country(),
                true, true, true, true,
                0, null, null,
                false, null, null,
                now, actor, now, actor,
                0
        );

        User saved = userRepository.save(user);

        for (Role role : roles) {
            userRoleRepository.save(new UserRole(
                    null,
                    saved.id(),
                    role.id(),
                    now, actor,
                    null,
                    null, null,
                    "Assigned on user creation"
            ));
        }

        return new UserResult(
                saved.uuid(),
                saved.username(),
                saved.email(),
                saved.emailVerified(),
                saved.name(),
                saved.locale(),
                saved.enabled(),
                roles.stream()
                        .map(role -> new RoleSummary(
                                role.uuid(), role.name(), role.active()
                        ))
                        .toList(),
                saved.createdAt(),
                saved.updatedAt()
        );
    }

    private List<Role> resolveRoles(List<UUID> roleUuids) {
        if (roleUuids == null || roleUuids.isEmpty()) {
            return List.of();
        }

        List<Role> roles = new ArrayList<>();

        for (UUID roleUuid : roleUuids) {
            Role role = roleRepository.findByUuid(roleUuid)
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Role not found: " + roleUuid
                    ));
            role.validateAssignable();
            roles.add(role);
        }

        return roles;
    }
}

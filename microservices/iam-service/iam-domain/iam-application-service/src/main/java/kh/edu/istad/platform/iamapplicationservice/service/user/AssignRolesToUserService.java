package kh.edu.istad.platform.iamapplicationservice.service.user;

import kh.edu.istad.platform.iamapplicationservice.command.user.AssignRolesToUserCommand;
import kh.edu.istad.platform.iamapplicationservice.port.out.AccountActivityRepository;
import kh.edu.istad.platform.iamapplicationservice.port.out.CurrentActorProvider;
import kh.edu.istad.platform.iamapplicationservice.port.out.RoleRepository;
import kh.edu.istad.platform.iamapplicationservice.port.out.UserRepository;
import kh.edu.istad.platform.iamapplicationservice.port.out.UserRoleRepository;
import kh.edu.istad.platform.iamapplicationservice.result.user.RoleAssignmentResult;
import kh.edu.istad.platform.iamapplicationservice.service.security.ActivitySupport;
import kh.edu.istad.platform.iamapplicationservice.usecase.user.AssignRolesToUserUseCase;
import kh.edu.istad.platform.iamdomaincore.enums.ActivityType;
import kh.edu.istad.platform.iamdomaincore.model.Role;
import kh.edu.istad.platform.iamdomaincore.model.User;
import kh.edu.istad.platform.iamdomaincore.model.UserRole;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AssignRolesToUserService implements AssignRolesToUserUseCase {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;
    private final CurrentActorProvider actorProvider;
    private final AccountActivityRepository accountActivityRepository;

    public AssignRolesToUserService(
            UserRepository userRepository,
            RoleRepository roleRepository,
            UserRoleRepository userRoleRepository,
            CurrentActorProvider actorProvider,
            AccountActivityRepository accountActivityRepository
    ) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.userRoleRepository = userRoleRepository;
        this.actorProvider = actorProvider;
        this.accountActivityRepository = accountActivityRepository;
    }

    @Override
    public List<RoleAssignmentResult> execute(AssignRolesToUserCommand command) {
        if (command == null) {
            throw new IllegalArgumentException("Assign roles command is required");
        }

        User user = UserStateSupport.requireActiveUser(userRepository, command.userUuid());

        String actor = actorProvider.getCurrentActor();

        if (actor == null || actor.isBlank()) {
            throw new IllegalStateException("Current actor is required");
        }

        OffsetDateTime now = OffsetDateTime.now();
        List<RoleAssignmentResult> results = new ArrayList<>();

        for (UUID roleUuid : command.roleUuids()) {
            Role role = roleRepository.findByUuid(roleUuid)
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Role not found: " + roleUuid
                    ));
            role.validateAssignable();

            boolean alreadyAssigned = userRoleRepository
                    .findActiveByUserIdAndRoleId(user.id(), role.id())
                    .isPresent();

            if (alreadyAssigned) {
                continue;
            }

            UserRole saved = userRoleRepository.save(new UserRole(
                    null,
                    user.id(),
                    role.id(),
                    now, actor,
                    command.expiresAt(),
                    null, null,
                    command.reason()
            ));

            ActivitySupport.record(
                    accountActivityRepository, user.id(),
                    ActivityType.ROLE_ASSIGNED,
                    "Role '" + role.name() + "' assigned to " + user.username(),
                    actor
            );

            results.add(new RoleAssignmentResult(
                    role.uuid(),
                    role.name(),
                    saved.assignedAt(),
                    saved.assignedBy(),
                    saved.expiresAt()
            ));
        }

        return results;
    }
}

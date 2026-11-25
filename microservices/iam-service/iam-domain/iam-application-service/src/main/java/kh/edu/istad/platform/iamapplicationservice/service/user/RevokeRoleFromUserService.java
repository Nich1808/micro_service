package kh.edu.istad.platform.iamapplicationservice.service.user;

import kh.edu.istad.platform.iamapplicationservice.port.out.AccountActivityRepository;
import kh.edu.istad.platform.iamapplicationservice.port.out.CurrentActorProvider;
import kh.edu.istad.platform.iamapplicationservice.port.out.RoleRepository;
import kh.edu.istad.platform.iamapplicationservice.port.out.UserRepository;
import kh.edu.istad.platform.iamapplicationservice.port.out.UserRoleRepository;
import kh.edu.istad.platform.iamapplicationservice.service.security.ActivitySupport;
import kh.edu.istad.platform.iamapplicationservice.usecase.user.RevokeRoleFromUserUseCase;
import kh.edu.istad.platform.iamdomaincore.enums.ActivityType;
import kh.edu.istad.platform.iamdomaincore.model.Role;
import kh.edu.istad.platform.iamdomaincore.model.User;
import kh.edu.istad.platform.iamdomaincore.model.UserRole;

import java.time.OffsetDateTime;
import java.util.UUID;

public class RevokeRoleFromUserService implements RevokeRoleFromUserUseCase {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;
    private final CurrentActorProvider actorProvider;
    private final AccountActivityRepository accountActivityRepository;

    public RevokeRoleFromUserService(
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
    public void execute(UUID userUuid, UUID roleUuid, String reason) {
        if (userUuid == null || roleUuid == null) {
            throw new IllegalArgumentException("User UUID and role UUID are required");
        }

        User user = UserStateSupport.requireActiveUser(userRepository, userUuid);

        Role role = roleRepository.findByUuid(roleUuid)
                .orElseThrow(() -> new IllegalArgumentException("Role not found"));

        UserRole assignment = userRoleRepository
                .findActiveByUserIdAndRoleId(user.id(), role.id())
                .orElseThrow(() -> new IllegalStateException(
                        "Role is not assigned to the user"
                ));

        String actor = actorProvider.getCurrentActor();

        if (actor == null || actor.isBlank()) {
            throw new IllegalStateException("Current actor is required");
        }

        userRoleRepository.save(
                assignment.revoke(actor, OffsetDateTime.now(), reason)
        );

        ActivitySupport.record(
                accountActivityRepository, user.id(),
                ActivityType.ROLE_REVOKED,
                "Role '" + role.name() + "' revoked from " + user.username(),
                actor
        );
    }
}

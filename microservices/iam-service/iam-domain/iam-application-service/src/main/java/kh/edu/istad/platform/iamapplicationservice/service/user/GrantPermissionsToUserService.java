package kh.edu.istad.platform.iamapplicationservice.service.user;

import kh.edu.istad.platform.iamapplicationservice.command.user.GrantPermissionsToUserCommand;
import kh.edu.istad.platform.iamapplicationservice.port.out.AccountActivityRepository;
import kh.edu.istad.platform.iamapplicationservice.port.out.CurrentActorProvider;
import kh.edu.istad.platform.iamapplicationservice.port.out.PermissionRepository;
import kh.edu.istad.platform.iamapplicationservice.port.out.UserPermissionRepository;
import kh.edu.istad.platform.iamapplicationservice.port.out.UserRepository;
import kh.edu.istad.platform.iamapplicationservice.result.user.PermissionGrantResult;
import kh.edu.istad.platform.iamapplicationservice.service.security.ActivitySupport;
import kh.edu.istad.platform.iamapplicationservice.usecase.user.GrantPermissionsToUserUseCase;
import kh.edu.istad.platform.iamdomaincore.enums.ActivityType;
import kh.edu.istad.platform.iamdomaincore.enums.PermissionEffect;
import kh.edu.istad.platform.iamdomaincore.model.Permission;
import kh.edu.istad.platform.iamdomaincore.model.User;
import kh.edu.istad.platform.iamdomaincore.model.UserPermission;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class GrantPermissionsToUserService implements GrantPermissionsToUserUseCase {

    private final UserRepository userRepository;
    private final PermissionRepository permissionRepository;
    private final UserPermissionRepository userPermissionRepository;
    private final CurrentActorProvider actorProvider;
    private final AccountActivityRepository accountActivityRepository;

    public GrantPermissionsToUserService(
            UserRepository userRepository,
            PermissionRepository permissionRepository,
            UserPermissionRepository userPermissionRepository,
            CurrentActorProvider actorProvider,
            AccountActivityRepository accountActivityRepository
    ) {
        this.userRepository = userRepository;
        this.permissionRepository = permissionRepository;
        this.userPermissionRepository = userPermissionRepository;
        this.actorProvider = actorProvider;
        this.accountActivityRepository = accountActivityRepository;
    }

    @Override
    public List<PermissionGrantResult> execute(GrantPermissionsToUserCommand command) {
        if (command == null) {
            throw new IllegalArgumentException("Grant permissions command is required");
        }

        if (command.effect() == null) {
            throw new IllegalArgumentException("Permission effect is required");
        }

        User user = UserStateSupport.requireActiveUser(userRepository, command.userUuid());

        String actor = actorProvider.getCurrentActor();

        if (actor == null || actor.isBlank()) {
            throw new IllegalStateException("Current actor is required");
        }

        OffsetDateTime now = OffsetDateTime.now();
        List<PermissionGrantResult> results = new ArrayList<>();

        for (UUID permissionUuid : command.permissionUuids()) {
            Permission permission = permissionRepository.findByUuid(permissionUuid)
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Permission not found: " + permissionUuid
                    ));
            permission.validateAssignable();

            Optional<UserPermission> active = userPermissionRepository
                    .findActiveByUserIdAndPermissionId(user.id(), permission.id());

            if (active.isPresent()) {
                UserPermission existing = active.get();

                if (existing.effect() == command.effect()) {
                    continue;
                }

                userPermissionRepository.save(
                        existing.revoke(actor, now, "Replaced by " + command.effect())
                );
            }

            UserPermission saved = userPermissionRepository.save(new UserPermission(
                    null,
                    user.id(),
                    permission.id(),
                    command.effect(),
                    now, actor,
                    command.expiresAt(),
                    null, null,
                    command.reason()
            ));

            ActivitySupport.record(
                    accountActivityRepository, user.id(),
                    ActivityType.PERMISSION_GRANTED,
                    "Permission '" + permission.name() + "' " + saved.effect()
                            + " for " + user.username(),
                    actor
            );

            results.add(new PermissionGrantResult(
                    permission.uuid(),
                    permission.name(),
                    saved.effect(),
                    saved.assignedAt(),
                    saved.assignedBy(),
                    saved.expiresAt()
            ));
        }

        return results;
    }
}

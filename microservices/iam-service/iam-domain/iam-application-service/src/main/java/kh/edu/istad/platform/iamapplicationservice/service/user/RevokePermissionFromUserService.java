package kh.edu.istad.platform.iamapplicationservice.service.user;

import kh.edu.istad.platform.iamapplicationservice.port.out.CurrentActorProvider;
import kh.edu.istad.platform.iamapplicationservice.port.out.PermissionRepository;
import kh.edu.istad.platform.iamapplicationservice.port.out.UserPermissionRepository;
import kh.edu.istad.platform.iamapplicationservice.port.out.UserRepository;
import kh.edu.istad.platform.iamapplicationservice.usecase.user.RevokePermissionFromUserUseCase;
import kh.edu.istad.platform.iamdomaincore.model.Permission;
import kh.edu.istad.platform.iamdomaincore.model.User;
import kh.edu.istad.platform.iamdomaincore.model.UserPermission;

import java.time.OffsetDateTime;
import java.util.UUID;

public class RevokePermissionFromUserService implements RevokePermissionFromUserUseCase {

    private final UserRepository userRepository;
    private final PermissionRepository permissionRepository;
    private final UserPermissionRepository userPermissionRepository;
    private final CurrentActorProvider actorProvider;

    public RevokePermissionFromUserService(
            UserRepository userRepository,
            PermissionRepository permissionRepository,
            UserPermissionRepository userPermissionRepository,
            CurrentActorProvider actorProvider
    ) {
        this.userRepository = userRepository;
        this.permissionRepository = permissionRepository;
        this.userPermissionRepository = userPermissionRepository;
        this.actorProvider = actorProvider;
    }

    @Override
    public void execute(UUID userUuid, UUID permissionUuid, String reason) {
        if (userUuid == null || permissionUuid == null) {
            throw new IllegalArgumentException("User UUID and permission UUID are required");
        }

        User user = UserStateSupport.requireActiveUser(userRepository, userUuid);

        Permission permission = permissionRepository.findByUuid(permissionUuid)
                .orElseThrow(() -> new IllegalArgumentException("Permission not found"));

        UserPermission assignment = userPermissionRepository
                .findActiveByUserIdAndPermissionId(user.id(), permission.id())
                .orElseThrow(() -> new IllegalStateException(
                        "Permission is not granted to the user"
                ));

        String actor = actorProvider.getCurrentActor();

        if (actor == null || actor.isBlank()) {
            throw new IllegalStateException("Current actor is required");
        }

        userPermissionRepository.save(
                assignment.revoke(actor, OffsetDateTime.now(), reason)
        );
    }
}

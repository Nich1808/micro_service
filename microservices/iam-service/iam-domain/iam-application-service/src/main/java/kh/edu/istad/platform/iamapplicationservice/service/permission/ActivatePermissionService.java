package kh.edu.istad.platform.iamapplicationservice.service.permission;

import kh.edu.istad.platform.iamapplicationservice.port.out.CurrentActorProvider;
import kh.edu.istad.platform.iamapplicationservice.port.out.PermissionRepository;
import kh.edu.istad.platform.iamapplicationservice.usecase.permission.ActivatePermissionUseCase;
import kh.edu.istad.platform.iamdomaincore.model.Permission;

import java.time.OffsetDateTime;
import java.util.UUID;

public class ActivatePermissionService implements ActivatePermissionUseCase {

    private final PermissionRepository permissionRepository;
    private final CurrentActorProvider actorProvider;

    public ActivatePermissionService(
            PermissionRepository permissionRepository,
            CurrentActorProvider actorProvider
    ) {
        this.permissionRepository = permissionRepository;
        this.actorProvider = actorProvider;
    }

    @Override
    public void execute(UUID permissionUuid) {
        if (permissionUuid == null) {
            throw new IllegalArgumentException("Permission UUID is required");
        }

        Permission permission = permissionRepository.findByUuid(permissionUuid)
                .orElseThrow(() -> new IllegalArgumentException("Permission not found"));

        String actor = actorProvider.getCurrentActor();

        if (actor == null || actor.isBlank()) {
            throw new IllegalStateException("Current actor is required");
        }

        permissionRepository.save(permission.activate(actor, OffsetDateTime.now()));
    }
}

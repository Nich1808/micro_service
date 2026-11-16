package kh.edu.istad.platform.iamapplicationservice.service.permission;

import kh.edu.istad.platform.iamapplicationservice.port.out.CurrentActorProvider;
import kh.edu.istad.platform.iamapplicationservice.port.out.PermissionRepository;
import kh.edu.istad.platform.iamapplicationservice.usecase.permission.DeletePermissionUseCase;
import kh.edu.istad.platform.iamdomaincore.model.Permission;

import java.time.OffsetDateTime;
import java.util.UUID;

public class DeletePermissionService implements DeletePermissionUseCase {

    private final PermissionRepository permissionRepository;
    private final CurrentActorProvider actorProvider;

    public DeletePermissionService(
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
                .filter(found -> !found.deleted())
                .orElseThrow(() -> new IllegalArgumentException("Permission not found"));

        String actor = actorProvider.getCurrentActor();

        if (actor == null || actor.isBlank()) {
            throw new IllegalStateException("Current actor is required");
        }

        permissionRepository.save(permission.softDelete(actor, OffsetDateTime.now()));
    }
}

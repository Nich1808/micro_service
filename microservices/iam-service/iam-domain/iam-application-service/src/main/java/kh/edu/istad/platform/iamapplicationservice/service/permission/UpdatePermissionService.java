package kh.edu.istad.platform.iamapplicationservice.service.permission;

import kh.edu.istad.platform.iamapplicationservice.command.permission.UpdatePermissionCommand;
import kh.edu.istad.platform.iamapplicationservice.port.out.CurrentActorProvider;
import kh.edu.istad.platform.iamapplicationservice.port.out.PermissionRepository;
import kh.edu.istad.platform.iamapplicationservice.result.permission.PermissionResult;
import kh.edu.istad.platform.iamapplicationservice.usecase.permission.UpdatePermissionUseCase;
import kh.edu.istad.platform.iamdomaincore.model.Permission;

import java.time.OffsetDateTime;
import java.util.Locale;

public class UpdatePermissionService implements UpdatePermissionUseCase {

    private final PermissionRepository permissionRepository;
    private final CurrentActorProvider actorProvider;

    public UpdatePermissionService(
            PermissionRepository permissionRepository,
            CurrentActorProvider actorProvider
    ) {
        this.permissionRepository = permissionRepository;
        this.actorProvider = actorProvider;
    }

    @Override
    public PermissionResult execute(UpdatePermissionCommand command) {
        if (command == null) {
            throw new IllegalArgumentException("Update permission command is required");
        }

        if (command.permissionUuid() == null) {
            throw new IllegalArgumentException("Permission UUID is required");
        }

        if (command.resource() == null || command.resource().isBlank()) {
            throw new IllegalArgumentException("Permission resource is required");
        }

        if (command.action() == null || command.action().isBlank()) {
            throw new IllegalArgumentException("Permission action is required");
        }

        Permission permission = permissionRepository.findByUuid(command.permissionUuid())
                .orElseThrow(() -> new IllegalArgumentException("Permission not found"));

        permission.validateModifiable();

        String resource = command.resource().trim().toLowerCase(Locale.ROOT);
        String action = command.action().trim().toLowerCase(Locale.ROOT);
        String name = resource + ":" + action;

        if (!name.equals(permission.name()) && permissionRepository.existsByName(name)) {
            throw new IllegalArgumentException("Permission already exists");
        }

        String actor = actorProvider.getCurrentActor();

        if (actor == null || actor.isBlank()) {
            throw new IllegalStateException("Current actor is required");
        }

        Permission updated = permissionRepository.save(permission.redefine(
                resource,
                action,
                command.description(),
                actor,
                OffsetDateTime.now()
        ));

        return new PermissionResult(
                updated.uuid(),
                updated.name(),
                updated.resource(),
                updated.action(),
                updated.description(),
                updated.system(),
                updated.active(),
                updated.createdAt(),
                updated.updatedAt()
        );
    }
}

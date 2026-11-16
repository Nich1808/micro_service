package kh.edu.istad.platform.iamapplicationservice.service.permission;

import kh.edu.istad.platform.iamapplicationservice.command.permission.CreatePermissionCommand;
import kh.edu.istad.platform.iamapplicationservice.port.out.CurrentActorProvider;
import kh.edu.istad.platform.iamapplicationservice.port.out.PermissionRepository;
import kh.edu.istad.platform.iamapplicationservice.result.permission.PermissionResult;
import kh.edu.istad.platform.iamapplicationservice.usecase.permission.CreatePermissionUseCase;
import kh.edu.istad.platform.iamdomaincore.model.Permission;

import java.time.OffsetDateTime;
import java.util.Locale;

public class CreatePermissionService implements CreatePermissionUseCase {

    private final PermissionRepository permissionRepository;
    private final CurrentActorProvider actorProvider;

    public CreatePermissionService(
            PermissionRepository permissionRepository,
            CurrentActorProvider actorProvider
    ) {
        this.permissionRepository = permissionRepository;
        this.actorProvider = actorProvider;
    }

    @Override
    public PermissionResult execute(CreatePermissionCommand command) {
        if (command == null) {
            throw new IllegalArgumentException("Create permission command is required");
        }

        if (command.resource() == null || command.resource().isBlank()) {
            throw new IllegalArgumentException("Permission resource is required");
        }

        if (command.action() == null || command.action().isBlank()) {
            throw new IllegalArgumentException("Permission action is required");
        }

        String resource = command.resource().trim().toLowerCase(Locale.ROOT);
        String action = command.action().trim().toLowerCase(Locale.ROOT);
        String name = resource + ":" + action;

        if (permissionRepository.existsByName(name)) {
            throw new IllegalArgumentException("Permission already exists");
        }

        String actor = actorProvider.getCurrentActor();

        if (actor == null || actor.isBlank()) {
            throw new IllegalStateException("Current actor is required");
        }

        OffsetDateTime now = OffsetDateTime.now();

        Permission saved = permissionRepository.save(new Permission(
                null, null,
                name, resource, action, command.description(),
                false, true, false,
                null, null,
                now, actor,
                now, actor,
                0
        ));

        return toResult(saved);
    }

    private PermissionResult toResult(Permission permission) {
        return new PermissionResult(
                permission.uuid(),
                permission.name(),
                permission.resource(),
                permission.action(),
                permission.description(),
                permission.system(),
                permission.active(),
                permission.createdAt(),
                permission.updatedAt()
        );
    }
}

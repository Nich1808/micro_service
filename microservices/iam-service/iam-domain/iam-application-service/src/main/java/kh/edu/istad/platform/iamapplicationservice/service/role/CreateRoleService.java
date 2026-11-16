package kh.edu.istad.platform.iamapplicationservice.service.role;

import kh.edu.istad.platform.iamapplicationservice.command.role.CreateRoleCommand;
import kh.edu.istad.platform.iamapplicationservice.port.out.CurrentActorProvider;
import kh.edu.istad.platform.iamapplicationservice.port.out.PermissionRepository;
import kh.edu.istad.platform.iamapplicationservice.port.out.RolePermissionRepository;
import kh.edu.istad.platform.iamapplicationservice.port.out.RoleRepository;
import kh.edu.istad.platform.iamapplicationservice.result.role.RoleResult;
import kh.edu.istad.platform.iamapplicationservice.usecase.role.CreateRoleUseCase;
import kh.edu.istad.platform.iamdomaincore.model.Permission;
import kh.edu.istad.platform.iamdomaincore.model.Role;
import kh.edu.istad.platform.iamdomaincore.model.RolePermission;

import java.time.OffsetDateTime;
import java.util.Locale;
import java.util.UUID;

public class CreateRoleService implements CreateRoleUseCase {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final RolePermissionRepository rolePermissionRepository;
    private final CurrentActorProvider actorProvider;

    public CreateRoleService(
            RoleRepository roleRepository,
            PermissionRepository permissionRepository,
            RolePermissionRepository rolePermissionRepository,
            CurrentActorProvider actorProvider
    ) {
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
        this.rolePermissionRepository = rolePermissionRepository;
        this.actorProvider = actorProvider;
    }

    @Override
    public RoleResult execute(CreateRoleCommand command) {
        if (command == null) {
            throw new IllegalArgumentException("Create role command is required");
        }

        if (command.name() == null || command.name().isBlank()) {
            throw new IllegalArgumentException("Role name is required");
        }

        String name = command.name().trim().toUpperCase(Locale.ROOT);

        if (roleRepository.existsByName(name)) {
            throw new IllegalArgumentException("Role already exists");
        }

        String actor = requireActor();
        OffsetDateTime now = OffsetDateTime.now();

        Role saved = roleRepository.save(new Role(
                null, null,
                name, command.description(),
                false, true, false,
                null, null,
                now, actor,
                now, actor,
                0
        ));

        for (UUID permissionUuid : command.permissionUuids()) {
            Permission permission = permissionRepository.findByUuid(permissionUuid)
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Permission not found: " + permissionUuid
                    ));
            permission.validateAssignable();

            rolePermissionRepository.save(new RolePermission(
                    null,
                    saved.id(),
                    permission.id(),
                    now, actor,
                    null,
                    null, null,
                    null
            ));
        }

        return toResult(saved);
    }

    private String requireActor() {
        String actor = actorProvider.getCurrentActor();

        if (actor == null || actor.isBlank()) {
            throw new IllegalStateException("Current actor is required");
        }

        return actor;
    }

    private RoleResult toResult(Role role) {
        return new RoleResult(
                role.uuid(),
                role.name(),
                role.description(),
                role.system(),
                role.active(),
                role.createdAt(),
                role.updatedAt()
        );
    }
}

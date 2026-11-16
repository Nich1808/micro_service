package kh.edu.istad.platform.iamapplicationservice.service.role;

import kh.edu.istad.platform.iamapplicationservice.command.role.AssignPermissionsToRoleCommand;
import kh.edu.istad.platform.iamapplicationservice.port.out.CurrentActorProvider;
import kh.edu.istad.platform.iamapplicationservice.port.out.PermissionRepository;
import kh.edu.istad.platform.iamapplicationservice.port.out.RolePermissionRepository;
import kh.edu.istad.platform.iamapplicationservice.port.out.RoleRepository;
import kh.edu.istad.platform.iamapplicationservice.result.role.PermissionAssignmentResult;
import kh.edu.istad.platform.iamapplicationservice.usecase.role.AssignPermissionsToRoleUseCase;
import kh.edu.istad.platform.iamdomaincore.model.Permission;
import kh.edu.istad.platform.iamdomaincore.model.Role;
import kh.edu.istad.platform.iamdomaincore.model.RolePermission;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AssignPermissionsToRoleService implements AssignPermissionsToRoleUseCase {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final RolePermissionRepository rolePermissionRepository;
    private final CurrentActorProvider actorProvider;

    public AssignPermissionsToRoleService(
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
    public List<PermissionAssignmentResult> execute(AssignPermissionsToRoleCommand command) {
        if (command == null) {
            throw new IllegalArgumentException("Assign permissions command is required");
        }

        if (command.roleUuid() == null) {
            throw new IllegalArgumentException("Role UUID is required");
        }

        Role role = roleRepository.findByUuid(command.roleUuid())
                .orElseThrow(() -> new IllegalArgumentException("Role not found"));

        role.validateAssignable();

        String actor = actorProvider.getCurrentActor();

        if (actor == null || actor.isBlank()) {
            throw new IllegalStateException("Current actor is required");
        }

        OffsetDateTime now = OffsetDateTime.now();
        List<PermissionAssignmentResult> results = new ArrayList<>();

        for (UUID permissionUuid : command.permissionUuids()) {
            Permission permission = permissionRepository.findByUuid(permissionUuid)
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Permission not found: " + permissionUuid
                    ));
            permission.validateAssignable();

            boolean alreadyAssigned = rolePermissionRepository
                    .findActiveByRoleIdAndPermissionId(role.id(), permission.id())
                    .isPresent();

            if (alreadyAssigned) {
                continue;
            }

            RolePermission saved = rolePermissionRepository.save(new RolePermission(
                    null,
                    role.id(),
                    permission.id(),
                    now, actor,
                    command.expiresAt(),
                    null, null,
                    command.reason()
            ));

            results.add(new PermissionAssignmentResult(
                    permission.uuid(),
                    permission.name(),
                    saved.assignedAt(),
                    saved.assignedBy(),
                    saved.expiresAt()
            ));
        }

        return results;
    }
}

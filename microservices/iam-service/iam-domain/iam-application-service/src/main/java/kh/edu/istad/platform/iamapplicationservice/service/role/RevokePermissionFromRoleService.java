package kh.edu.istad.platform.iamapplicationservice.service.role;

import kh.edu.istad.platform.iamapplicationservice.port.out.CurrentActorProvider;
import kh.edu.istad.platform.iamapplicationservice.port.out.PermissionRepository;
import kh.edu.istad.platform.iamapplicationservice.port.out.RolePermissionRepository;
import kh.edu.istad.platform.iamapplicationservice.port.out.RoleRepository;
import kh.edu.istad.platform.iamapplicationservice.usecase.role.RevokePermissionFromRoleUseCase;
import kh.edu.istad.platform.iamdomaincore.model.Permission;
import kh.edu.istad.platform.iamdomaincore.model.Role;
import kh.edu.istad.platform.iamdomaincore.model.RolePermission;

import java.time.OffsetDateTime;
import java.util.UUID;

public class RevokePermissionFromRoleService implements RevokePermissionFromRoleUseCase {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final RolePermissionRepository rolePermissionRepository;
    private final CurrentActorProvider actorProvider;

    public RevokePermissionFromRoleService(
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
    public void execute(UUID roleUuid, UUID permissionUuid, String reason) {
        if (roleUuid == null || permissionUuid == null) {
            throw new IllegalArgumentException("Role UUID and permission UUID are required");
        }

        Role role = roleRepository.findByUuid(roleUuid)
                .orElseThrow(() -> new IllegalArgumentException("Role not found"));

        Permission permission = permissionRepository.findByUuid(permissionUuid)
                .orElseThrow(() -> new IllegalArgumentException("Permission not found"));

        RolePermission assignment = rolePermissionRepository
                .findActiveByRoleIdAndPermissionId(role.id(), permission.id())
                .orElseThrow(() -> new IllegalStateException(
                        "Permission is not assigned to the role"
                ));

        String actor = actorProvider.getCurrentActor();

        if (actor == null || actor.isBlank()) {
            throw new IllegalStateException("Current actor is required");
        }

        rolePermissionRepository.save(
                assignment.revoke(actor, OffsetDateTime.now(), reason)
        );
    }
}

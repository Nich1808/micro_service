package kh.edu.istad.platform.iamapplicationservice.service.permission;

import kh.edu.istad.platform.iamapplicationservice.port.out.PermissionRepository;
import kh.edu.istad.platform.iamapplicationservice.result.permission.PermissionResult;
import kh.edu.istad.platform.iamapplicationservice.usecase.permission.GetPermissionUseCase;
import kh.edu.istad.platform.iamdomaincore.model.Permission;

import java.util.UUID;

public class GetPermissionService implements GetPermissionUseCase {

    private final PermissionRepository permissionRepository;

    public GetPermissionService(PermissionRepository permissionRepository) {
        this.permissionRepository = permissionRepository;
    }

    @Override
    public PermissionResult execute(UUID permissionUuid) {
        if (permissionUuid == null) {
            throw new IllegalArgumentException("Permission UUID is required");
        }

        Permission permission = permissionRepository.findByUuid(permissionUuid)
                .filter(found -> !found.deleted())
                .orElseThrow(() -> new IllegalArgumentException("Permission not found"));

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

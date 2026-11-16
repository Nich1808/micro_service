package kh.edu.istad.platform.iamapplicationservice.port.out;

import kh.edu.istad.platform.iamdomaincore.model.RolePermission;

import java.util.List;
import java.util.Optional;

public interface RolePermissionRepository {

    RolePermission save(RolePermission rolePermission);

    List<RolePermission> findAllByRoleId(Long roleId);

    Optional<RolePermission> findActiveByRoleIdAndPermissionId(Long roleId, Long permissionId);
}

package kh.edu.istad.platform.iamapplicationservice.port.out;

import kh.edu.istad.platform.iamdomaincore.model.RolePermission;

import java.util.List;

public interface RolePermissionRepository {

    List<RolePermission> findAllByRoleId(Long roleId);
}

package kh.edu.istad.platform.iamapplicationservice.port.out;

import kh.edu.istad.platform.iamdomaincore.model.UserPermission;

import java.util.List;
import java.util.Optional;

public interface UserPermissionRepository {

    UserPermission save(UserPermission userPermission);

    List<UserPermission> findAllByUserId(Long userId);

    Optional<UserPermission> findActiveByUserIdAndPermissionId(Long userId, Long permissionId);
}

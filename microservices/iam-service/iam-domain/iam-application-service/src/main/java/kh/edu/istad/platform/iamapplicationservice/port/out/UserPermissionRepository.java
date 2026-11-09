package kh.edu.istad.platform.iamapplicationservice.port.out;

import kh.edu.istad.platform.iamdomaincore.model.UserPermission;

import java.util.List;

public interface UserPermissionRepository {

    List<UserPermission> findAllByUserId(Long userId);
}

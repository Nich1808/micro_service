package kh.edu.istad.platform.iamapplicationservice.port.out;

import kh.edu.istad.platform.iamdomaincore.model.UserRole;

import java.util.List;

public interface UserRoleRepository {

    List<UserRole> findAllByUserId(Long userId);
}

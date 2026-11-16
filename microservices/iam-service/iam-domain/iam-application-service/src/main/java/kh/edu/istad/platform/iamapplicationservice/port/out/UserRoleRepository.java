package kh.edu.istad.platform.iamapplicationservice.port.out;

import kh.edu.istad.platform.iamdomaincore.model.UserRole;

import java.util.List;
import java.util.Optional;

public interface UserRoleRepository {

    UserRole save(UserRole userRole);

    List<UserRole> findAllByUserId(Long userId);

    Optional<UserRole> findActiveByUserIdAndRoleId(Long userId, Long roleId);
}

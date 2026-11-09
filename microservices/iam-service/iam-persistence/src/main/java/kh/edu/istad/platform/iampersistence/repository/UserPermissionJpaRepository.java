package kh.edu.istad.platform.iampersistence.repository;

import kh.edu.istad.platform.iampersistence.entity.UserPermissionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserPermissionJpaRepository
        extends JpaRepository<UserPermissionEntity, Long> {

    List<UserPermissionEntity> findAllByUser_Id(Long userId);

    List<UserPermissionEntity> findAllByUser_IdAndRevokedAtIsNull(Long userId);

    Optional<UserPermissionEntity> findByUser_IdAndPermission_IdAndRevokedAtIsNull(
            Long userId,
            Long permissionId
    );
}

package kh.edu.istad.platform.iampersistence.repository;

import kh.edu.istad.platform.iampersistence.entity.RolePermissionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RolePermissionJpaRepository
        extends JpaRepository<RolePermissionEntity, Long> {

    List<RolePermissionEntity> findAllByRole_Id(Long roleId);

    List<RolePermissionEntity> findAllByRole_IdAndRevokedAtIsNull(Long roleId);

    Optional<RolePermissionEntity> findByRole_IdAndPermission_IdAndRevokedAtIsNull(
            Long roleId,
            Long permissionId
    );
}

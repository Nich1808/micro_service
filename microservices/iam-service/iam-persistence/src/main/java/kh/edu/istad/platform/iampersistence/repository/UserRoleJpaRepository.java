package kh.edu.istad.platform.iampersistence.repository;

import kh.edu.istad.platform.iampersistence.entity.UserRoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRoleJpaRepository extends JpaRepository<UserRoleEntity, Long> {

    List<UserRoleEntity> findAllByUser_Id(Long userId);

    List<UserRoleEntity> findAllByUser_IdAndRevokedAtIsNull(Long userId);

    Optional<UserRoleEntity> findByUser_IdAndRole_IdAndRevokedAtIsNull(
            Long userId,
            Long roleId
    );
}

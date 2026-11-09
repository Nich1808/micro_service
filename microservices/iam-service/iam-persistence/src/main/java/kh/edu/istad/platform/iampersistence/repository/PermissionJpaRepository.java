package kh.edu.istad.platform.iampersistence.repository;

import kh.edu.istad.platform.iampersistence.entity.PermissionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PermissionJpaRepository extends JpaRepository<PermissionEntity, Long> {

    Optional<PermissionEntity> findByUuid(UUID uuid);

    Optional<PermissionEntity> findByName(String name);

    boolean existsByName(String name);
}

package kh.edu.istad.platform.iampersistence.repository;

import kh.edu.istad.platform.iampersistence.entity.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface RoleJpaRepository extends JpaRepository<RoleEntity, Long> {

    Optional<RoleEntity> findByUuid(UUID uuid);

    Optional<RoleEntity> findByName(String name);

    boolean existsByName(String name);
}

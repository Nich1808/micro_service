package kh.edu.istad.platform.iampersistence.repository;

import kh.edu.istad.platform.iampersistence.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface UserJpaRepository extends JpaRepository<UserEntity, Long> {

    Optional<UserEntity> findByUuid(UUID uuid);

    Optional<UserEntity> findByUsername(String username);

    Optional<UserEntity> findByEmail(String email);

    @Query("""
            select u
            from UserEntity u
            where u.username = :value or u.email = :value
            """)
    Optional<UserEntity> findByUsernameOrEmail(@Param("value") String value);

    Page<UserEntity> findAllByDeletedFalse(Pageable pageable);

    long countByDeletedFalse();

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);
}

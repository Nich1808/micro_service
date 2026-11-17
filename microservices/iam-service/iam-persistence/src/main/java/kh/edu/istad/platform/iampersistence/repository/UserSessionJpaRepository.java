package kh.edu.istad.platform.iampersistence.repository;

import kh.edu.istad.platform.iampersistence.entity.UserSessionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserSessionJpaRepository
        extends JpaRepository<UserSessionEntity, Long> {

    Optional<UserSessionEntity> findByUuid(UUID uuid);

    Optional<UserSessionEntity> findBySessionKeyAndRevokedAtIsNull(String sessionKey);

    List<UserSessionEntity> findAllByUserIdAndRevokedAtIsNull(Long userId);

    List<UserSessionEntity> findAllByUserId(Long userId);
}

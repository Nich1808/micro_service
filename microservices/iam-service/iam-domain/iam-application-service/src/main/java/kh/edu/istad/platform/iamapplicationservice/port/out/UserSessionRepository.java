package kh.edu.istad.platform.iamapplicationservice.port.out;

import kh.edu.istad.platform.iamdomaincore.model.UserSession;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserSessionRepository {

    UserSession save(UserSession session);

    Optional<UserSession> findByUuid(UUID uuid);

    Optional<UserSession> findActiveBySessionKey(String sessionKey);

    List<UserSession> findActiveByUserId(Long userId, OffsetDateTime now);

    List<UserSession> findAllByUserId(Long userId);
}

package kh.edu.istad.platform.iampersistence.adapter;

import kh.edu.istad.platform.iamapplicationservice.port.out.UserSessionRepository;
import kh.edu.istad.platform.iamdomaincore.model.UserSession;
import kh.edu.istad.platform.iampersistence.entity.UserSessionEntity;
import kh.edu.istad.platform.iampersistence.repository.UserSessionJpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class UserSessionPersistenceAdapter implements UserSessionRepository {

    private final UserSessionJpaRepository repository;

    public UserSessionPersistenceAdapter(UserSessionJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional
    public UserSession save(UserSession session) {
        UserSessionEntity entity = session.id() != null
                ? repository.findById(session.id())
                        .orElseThrow(() -> new IllegalStateException(
                                "Session not found"
                        ))
                : repository.findByUuid(session.uuid())
                        .orElseGet(UserSessionEntity::new);

        entity.uuid = session.uuid();
        entity.userId = session.userId();
        entity.sessionKey = session.sessionKey();
        entity.device = session.device();
        entity.browser = session.browser();
        entity.operatingSystem = session.operatingSystem();
        entity.ipAddress = session.ipAddress();
        entity.location = session.location();
        entity.userAgent = session.userAgent();
        entity.createdAt = session.createdAt();
        entity.lastActivityAt = session.lastActivityAt();
        entity.expiresAt = session.expiresAt();
        entity.revokedAt = session.revokedAt();
        entity.revokedBy = session.revokedBy();
        entity.revokedReason = session.revokedReason();

        return toDomain(repository.save(entity));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UserSession> findByUuid(UUID uuid) {
        return repository.findByUuid(uuid).map(this::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UserSession> findActiveBySessionKey(String sessionKey) {
        return repository.findBySessionKeyAndRevokedAtIsNull(sessionKey)
                .map(this::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserSession> findActiveByUserId(Long userId, OffsetDateTime now) {
        return repository.findAllByUserIdAndRevokedAtIsNull(userId)
                .stream()
                .map(this::toDomain)
                .filter(session -> session.isActiveAt(now))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserSession> findAllByUserId(Long userId) {
        return repository.findAllByUserId(userId)
                .stream()
                .map(this::toDomain)
                .toList();
    }

    private UserSession toDomain(UserSessionEntity entity) {
        return new UserSession(
                entity.id,
                entity.uuid,
                entity.userId,
                entity.sessionKey,
                entity.device,
                entity.browser,
                entity.operatingSystem,
                entity.ipAddress,
                entity.location,
                entity.userAgent,
                entity.createdAt,
                entity.lastActivityAt,
                entity.expiresAt,
                entity.revokedAt,
                entity.revokedBy,
                entity.revokedReason
        );
    }
}

package kh.edu.istad.platform.iampersistence.adapter;

import kh.edu.istad.platform.iamapplicationservice.port.out.LoginAttemptRepository;
import kh.edu.istad.platform.iamdomaincore.model.LoginAttempt;
import kh.edu.istad.platform.iampersistence.entity.LoginAttemptEntity;
import kh.edu.istad.platform.iampersistence.repository.LoginAttemptJpaRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;

@Repository
public class LoginAttemptPersistenceAdapter implements LoginAttemptRepository {

    private final LoginAttemptJpaRepository repository;

    public LoginAttemptPersistenceAdapter(LoginAttemptJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional
    public LoginAttempt save(LoginAttempt attempt) {
        LoginAttemptEntity entity = new LoginAttemptEntity();

        entity.uuid = attempt.uuid();
        entity.userId = attempt.userId();
        entity.identifier = attempt.identifier();
        entity.successful = attempt.successful();
        entity.failureReason = attempt.failureReason();
        entity.ipAddress = attempt.ipAddress();
        entity.device = attempt.device();
        entity.browser = attempt.browser();
        entity.operatingSystem = attempt.operatingSystem();
        entity.location = attempt.location();
        entity.userAgent = attempt.userAgent();
        entity.attemptedAt = attempt.attemptedAt();

        return toDomain(repository.save(entity));
    }

    @Override
    @Transactional(readOnly = true)
    public List<LoginAttempt> findAllByUserId(Long userId, int page, int size) {
        return repository
                .findAllByUserIdOrderByAttemptedAtDesc(
                        userId,
                        PageRequest.of(Math.max(page, 0), Math.max(size, 1))
                )
                .getContent()
                .stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public long countByUserId(Long userId) {
        return repository.countByUserId(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<LoginAttempt> findFailuresSince(Long userId, OffsetDateTime since) {
        return repository
                .findAllByUserIdAndSuccessfulFalseAndAttemptedAtAfterOrderByAttemptedAtDesc(
                        userId,
                        since
                )
                .stream()
                .map(this::toDomain)
                .toList();
    }

    private LoginAttempt toDomain(LoginAttemptEntity entity) {
        return new LoginAttempt(
                entity.id,
                entity.uuid,
                entity.userId,
                entity.identifier,
                entity.successful,
                entity.failureReason,
                entity.ipAddress,
                entity.device,
                entity.browser,
                entity.operatingSystem,
                entity.location,
                entity.userAgent,
                entity.attemptedAt
        );
    }
}

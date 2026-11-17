package kh.edu.istad.platform.iampersistence.adapter;

import kh.edu.istad.platform.iamapplicationservice.port.out.AccountActivityRepository;
import kh.edu.istad.platform.iamdomaincore.enums.ActivityType;
import kh.edu.istad.platform.iamdomaincore.model.AccountActivity;
import kh.edu.istad.platform.iampersistence.entity.AccountActivityEntity;
import kh.edu.istad.platform.iampersistence.repository.AccountActivityJpaRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public class AccountActivityPersistenceAdapter implements AccountActivityRepository {

    private final AccountActivityJpaRepository repository;

    public AccountActivityPersistenceAdapter(
            AccountActivityJpaRepository repository
    ) {
        this.repository = repository;
    }

    @Override
    @Transactional
    public AccountActivity save(AccountActivity activity) {
        AccountActivityEntity entity = new AccountActivityEntity();

        entity.uuid = activity.uuid();
        entity.userId = activity.userId();
        entity.type = activity.type();
        entity.description = activity.description();
        entity.ipAddress = activity.ipAddress();
        entity.userAgent = activity.userAgent();
        entity.actor = activity.actor();
        entity.createdAt = activity.createdAt();

        return toDomain(repository.save(entity));
    }

    @Override
    @Transactional(readOnly = true)
    public List<AccountActivity> findAllByUserId(Long userId, int page, int size) {
        return repository
                .findAllByUserIdOrderByCreatedAtDesc(userId, pageRequest(page, size))
                .getContent()
                .stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<AccountActivity> findAll(int page, int size) {
        return repository
                .findAllByOrderByCreatedAtDesc(pageRequest(page, size))
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
    public long count() {
        return repository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public List<AccountActivity> findAuditLogs(
            ActivityType type,
            int page,
            int size
    ) {
        if (type == null) {
            return findAll(page, size);
        }

        return repository
                .findAllByTypeOrderByCreatedAtDesc(type, pageRequest(page, size))
                .getContent()
                .stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public long countAuditLogs(ActivityType type) {
        return type == null ? repository.count() : repository.countByType(type);
    }

    private static PageRequest pageRequest(int page, int size) {
        return PageRequest.of(Math.max(page, 0), Math.max(size, 1));
    }

    private AccountActivity toDomain(AccountActivityEntity entity) {
        return new AccountActivity(
                entity.id,
                entity.uuid,
                entity.userId,
                entity.type,
                entity.description,
                entity.ipAddress,
                entity.userAgent,
                entity.actor,
                entity.createdAt
        );
    }
}

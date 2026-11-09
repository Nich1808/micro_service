package kh.edu.istad.platform.iampersistence.adapter;

import kh.edu.istad.platform.iamapplicationservice.port.out.IdentityRepository;
import kh.edu.istad.platform.iamdomaincore.enums.IdentityProvider;
import kh.edu.istad.platform.iamdomaincore.model.Identity;
import kh.edu.istad.platform.iampersistence.entity.IdentityEntity;
import kh.edu.istad.platform.iampersistence.repository.IdentityJpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class IdentityPersistenceAdapter implements IdentityRepository {

    private final IdentityJpaRepository repository;

    public IdentityPersistenceAdapter(IdentityJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional
    public Identity save(Identity identity) {
        IdentityEntity entity = repository.findByUuid(identity.uuid())
                .orElseGet(IdentityEntity::new);

        entity.uuid = identity.uuid();
        entity.userId = identity.userId();
        entity.provider = identity.provider();
        entity.providerSubject = identity.providerSubject();
        entity.email = identity.email();
        entity.emailVerified = identity.emailVerified();
        entity.displayName = identity.displayName();
        entity.username = identity.username();
        entity.pictureUrl = identity.pictureUrl();
        entity.attributes = identity.attributes();
        entity.linkedAt = identity.linkedAt();
        entity.lastLoginAt = identity.lastLoginAt();
        entity.deleted = identity.deleted();
        entity.deletedAt = identity.deletedAt();
        entity.deletedBy = identity.deletedBy();
        entity.createdAt = identity.createdAt();
        entity.createdBy = identity.createdBy();
        entity.updatedAt = identity.updatedAt();
        entity.updatedBy = identity.updatedBy();

        return toDomain(repository.save(entity));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Identity> findByUuid(UUID uuid) {
        return repository.findByUuid(uuid).map(this::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Identity> findByProviderAndProviderSubject(
            IdentityProvider provider,
            String providerSubject
    ) {
        return repository
                .findByProviderAndProviderSubjectAndDeletedFalse(
                        provider,
                        providerSubject
                )
                .map(this::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Identity> findAllByUserId(Long userId) {
        return repository.findAllByUserIdAndDeletedFalse(userId)
                .stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByUserIdAndProvider(
            Long userId,
            IdentityProvider provider
    ) {
        return repository.existsByUserIdAndProviderAndDeletedFalse(
                userId,
                provider
        );
    }

    private Identity toDomain(IdentityEntity entity) {
        return new Identity(
                entity.id,
                entity.uuid,
                entity.userId,
                entity.provider,
                entity.providerSubject,
                entity.email,
                entity.emailVerified,
                entity.displayName,
                entity.username,
                entity.pictureUrl,
                entity.attributes,
                entity.linkedAt,
                entity.lastLoginAt,
                entity.deleted,
                entity.deletedAt,
                entity.deletedBy,
                entity.createdAt,
                entity.createdBy,
                entity.updatedAt,
                entity.updatedBy,
                entity.version
        );
    }
}

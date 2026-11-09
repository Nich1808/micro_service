package kh.edu.istad.platform.iampersistence.adapter;

import kh.edu.istad.platform.iamapplicationservice.port.out.PermissionRepository;
import kh.edu.istad.platform.iamdomaincore.model.Permission;
import kh.edu.istad.platform.iampersistence.entity.PermissionEntity;
import kh.edu.istad.platform.iampersistence.repository.PermissionJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class PermissionPersistenceAdapter implements PermissionRepository {

    private final PermissionJpaRepository repository;

    public PermissionPersistenceAdapter(PermissionJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public Permission save(Permission permission) {
        PermissionEntity entity = repository.findByUuid(permission.uuid())
                .orElseGet(PermissionEntity::new);

        entity.uuid = permission.uuid();
        entity.name = permission.name();
        entity.resource = permission.resource();
        entity.action = permission.action();
        entity.description = permission.description();
        entity.system = permission.system();
        entity.active = permission.active();
        entity.deleted = permission.deleted();
        entity.deletedAt = permission.deletedAt();
        entity.deletedBy = permission.deletedBy();
        entity.createdAt = permission.createdAt();
        entity.createdBy = permission.createdBy();
        entity.updatedAt = permission.updatedAt();
        entity.updatedBy = permission.updatedBy();

        return toDomain(repository.save(entity));
    }

    @Override
    public Optional<Permission> findById(Long id) {
        return repository.findById(id).map(this::toDomain);
    }

    @Override
    public Optional<Permission> findByUuid(UUID uuid) {
        return repository.findByUuid(uuid).map(this::toDomain);
    }

    @Override
    public Optional<Permission> findByName(String name) {
        return repository.findByName(name).map(this::toDomain);
    }

    @Override
    public boolean existsByName(String name) {
        return repository.existsByName(name);
    }

    private Permission toDomain(PermissionEntity entity) {
        return new Permission(
                entity.id,
                entity.uuid,
                entity.name,
                entity.resource,
                entity.action,
                entity.description,
                entity.system,
                entity.active,
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

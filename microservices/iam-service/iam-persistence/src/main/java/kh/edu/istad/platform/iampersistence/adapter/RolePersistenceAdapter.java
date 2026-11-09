package kh.edu.istad.platform.iampersistence.adapter;

import kh.edu.istad.platform.iamapplicationservice.port.out.RoleRepository;
import kh.edu.istad.platform.iamdomaincore.model.Role;
import kh.edu.istad.platform.iampersistence.entity.RoleEntity;
import kh.edu.istad.platform.iampersistence.repository.RoleJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class RolePersistenceAdapter implements RoleRepository {

    private final RoleJpaRepository repository;

    public RolePersistenceAdapter(RoleJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public Role save(Role role) {
        RoleEntity entity = repository.findByUuid(role.uuid())
                .orElseGet(RoleEntity::new);

        entity.uuid = role.uuid();
        entity.name = role.name();
        entity.description = role.description();
        entity.system = role.system();
        entity.active = role.active();
        entity.deleted = role.deleted();
        entity.deletedAt = role.deletedAt();
        entity.deletedBy = role.deletedBy();
        entity.createdAt = role.createdAt();
        entity.createdBy = role.createdBy();
        entity.updatedAt = role.updatedAt();
        entity.updatedBy = role.updatedBy();

        return toDomain(repository.save(entity));
    }

    @Override
    public Optional<Role> findById(Long id) {
        return repository.findById(id).map(this::toDomain);
    }

    @Override
    public Optional<Role> findByUuid(UUID uuid) {
        return repository.findByUuid(uuid).map(this::toDomain);
    }

    @Override
    public Optional<Role> findByName(String name) {
        return repository.findByName(name).map(this::toDomain);
    }

    @Override
    public boolean existsByName(String name) {
        return repository.existsByName(name);
    }

    private Role toDomain(RoleEntity entity) {
        return new Role(
                entity.id,
                entity.uuid,
                entity.name,
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

package kh.edu.istad.platform.iampersistence.adapter;

import kh.edu.istad.platform.iamapplicationservice.port.out.RolePermissionRepository;
import kh.edu.istad.platform.iamdomaincore.model.RolePermission;
import kh.edu.istad.platform.iampersistence.entity.RolePermissionEntity;
import kh.edu.istad.platform.iampersistence.repository.PermissionJpaRepository;
import kh.edu.istad.platform.iampersistence.repository.RoleJpaRepository;
import kh.edu.istad.platform.iampersistence.repository.RolePermissionJpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public class RolePermissionPersistenceAdapter implements RolePermissionRepository {

    private final RolePermissionJpaRepository repository;
    private final RoleJpaRepository roleRepository;
    private final PermissionJpaRepository permissionRepository;

    public RolePermissionPersistenceAdapter(
            RolePermissionJpaRepository repository,
            RoleJpaRepository roleRepository,
            PermissionJpaRepository permissionRepository
    ) {
        this.repository = repository;
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
    }

    @Override
    @Transactional
    public RolePermission save(RolePermission rolePermission) {
        RolePermissionEntity entity = rolePermission.id() == null
                ? new RolePermissionEntity()
                : repository.findById(rolePermission.id())
                        .orElseThrow(() -> new IllegalStateException(
                                "Permission assignment not found"
                        ));

        entity.role = roleRepository.findById(rolePermission.roleId())
                .orElseThrow(() -> new IllegalArgumentException("Role not found"));
        entity.permission = permissionRepository.findById(rolePermission.permissionId())
                .orElseThrow(() -> new IllegalArgumentException("Permission not found"));
        entity.assignedAt = rolePermission.assignedAt();
        entity.assignedBy = rolePermission.assignedBy();
        entity.expiresAt = rolePermission.expiresAt();
        entity.revokedAt = rolePermission.revokedAt();
        entity.revokedBy = rolePermission.revokedBy();
        entity.reason = rolePermission.reason();

        return toDomain(repository.save(entity));
    }

    @Override
    @Transactional(readOnly = true)
    public List<RolePermission> findAllByRoleId(Long roleId) {
        return repository.findAllByRole_Id(roleId)
                .stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<RolePermission> findActiveByRoleIdAndPermissionId(
            Long roleId,
            Long permissionId
    ) {
        return repository
                .findByRole_IdAndPermission_IdAndRevokedAtIsNull(roleId, permissionId)
                .map(this::toDomain);
    }

    private RolePermission toDomain(RolePermissionEntity entity) {
        return new RolePermission(
                entity.id,
                entity.role.getId(),
                entity.permission.getId(),
                entity.assignedAt,
                entity.assignedBy,
                entity.expiresAt,
                entity.revokedAt,
                entity.revokedBy,
                entity.reason
        );
    }
}

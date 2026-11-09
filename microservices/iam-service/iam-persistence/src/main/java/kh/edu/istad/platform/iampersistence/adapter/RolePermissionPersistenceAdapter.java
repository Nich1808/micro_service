package kh.edu.istad.platform.iampersistence.adapter;

import kh.edu.istad.platform.iamapplicationservice.port.out.RolePermissionRepository;
import kh.edu.istad.platform.iamdomaincore.model.RolePermission;
import kh.edu.istad.platform.iampersistence.repository.RolePermissionJpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public class RolePermissionPersistenceAdapter implements RolePermissionRepository {

    private final RolePermissionJpaRepository repository;

    public RolePermissionPersistenceAdapter(RolePermissionJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<RolePermission> findAllByRoleId(Long roleId) {
        return repository.findAllByRole_Id(roleId)
                .stream()
                .map(entity -> new RolePermission(
                        entity.id,
                        entity.role.id,
                        entity.permission.id,
                        entity.assignedAt,
                        entity.assignedBy,
                        entity.expiresAt,
                        entity.revokedAt,
                        entity.revokedBy,
                        entity.reason
                ))
                .toList();
    }
}

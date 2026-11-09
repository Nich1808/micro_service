package kh.edu.istad.platform.iampersistence.adapter;

import kh.edu.istad.platform.iamapplicationservice.port.out.UserPermissionRepository;
import kh.edu.istad.platform.iamdomaincore.model.UserPermission;
import kh.edu.istad.platform.iampersistence.repository.UserPermissionJpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public class UserPermissionPersistenceAdapter implements UserPermissionRepository {

    private final UserPermissionJpaRepository repository;

    public UserPermissionPersistenceAdapter(UserPermissionJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserPermission> findAllByUserId(Long userId) {
        return repository.findAllByUser_Id(userId)
                .stream()
                .map(entity -> new UserPermission(
                        entity.id,
                        entity.user.id,
                        entity.permission.id,
                        entity.effect,
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

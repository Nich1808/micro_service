package kh.edu.istad.platform.iampersistence.adapter;

import kh.edu.istad.platform.iamapplicationservice.port.out.UserPermissionRepository;
import kh.edu.istad.platform.iamdomaincore.model.UserPermission;
import kh.edu.istad.platform.iampersistence.entity.UserPermissionEntity;
import kh.edu.istad.platform.iampersistence.repository.PermissionJpaRepository;
import kh.edu.istad.platform.iampersistence.repository.UserJpaRepository;
import kh.edu.istad.platform.iampersistence.repository.UserPermissionJpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public class UserPermissionPersistenceAdapter implements UserPermissionRepository {

    private final UserPermissionJpaRepository repository;
    private final UserJpaRepository userRepository;
    private final PermissionJpaRepository permissionRepository;

    public UserPermissionPersistenceAdapter(
            UserPermissionJpaRepository repository,
            UserJpaRepository userRepository,
            PermissionJpaRepository permissionRepository
    ) {
        this.repository = repository;
        this.userRepository = userRepository;
        this.permissionRepository = permissionRepository;
    }

    @Override
    @Transactional
    public UserPermission save(UserPermission userPermission) {
        UserPermissionEntity entity = userPermission.id() == null
                ? new UserPermissionEntity()
                : repository.findById(userPermission.id())
                        .orElseThrow(() -> new IllegalStateException(
                                "Permission grant not found"
                        ));

        entity.user = userRepository.findById(userPermission.userId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        entity.permission = permissionRepository.findById(userPermission.permissionId())
                .orElseThrow(() -> new IllegalArgumentException("Permission not found"));
        entity.effect = userPermission.effect();
        entity.assignedAt = userPermission.assignedAt();
        entity.assignedBy = userPermission.assignedBy();
        entity.expiresAt = userPermission.expiresAt();
        entity.revokedAt = userPermission.revokedAt();
        entity.revokedBy = userPermission.revokedBy();
        entity.reason = userPermission.reason();

        return toDomain(repository.save(entity));
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserPermission> findAllByUserId(Long userId) {
        return repository.findAllByUser_Id(userId)
                .stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UserPermission> findActiveByUserIdAndPermissionId(
            Long userId,
            Long permissionId
    ) {
        return repository
                .findByUser_IdAndPermission_IdAndRevokedAtIsNull(userId, permissionId)
                .map(this::toDomain);
    }

    private UserPermission toDomain(UserPermissionEntity entity) {
        return new UserPermission(
                entity.id,
                entity.user.getId(),
                entity.permission.getId(),
                entity.effect,
                entity.assignedAt,
                entity.assignedBy,
                entity.expiresAt,
                entity.revokedAt,
                entity.revokedBy,
                entity.reason
        );
    }
}

package kh.edu.istad.platform.iampersistence.adapter;

import kh.edu.istad.platform.iamapplicationservice.port.out.UserRoleRepository;
import kh.edu.istad.platform.iamdomaincore.model.UserRole;
import kh.edu.istad.platform.iampersistence.entity.UserRoleEntity;
import kh.edu.istad.platform.iampersistence.repository.RoleJpaRepository;
import kh.edu.istad.platform.iampersistence.repository.UserJpaRepository;
import kh.edu.istad.platform.iampersistence.repository.UserRoleJpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public class UserRolePersistenceAdapter implements UserRoleRepository {

    private final UserRoleJpaRepository repository;
    private final UserJpaRepository userRepository;
    private final RoleJpaRepository roleRepository;

    public UserRolePersistenceAdapter(
            UserRoleJpaRepository repository,
            UserJpaRepository userRepository,
            RoleJpaRepository roleRepository
    ) {
        this.repository = repository;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    @Override
    @Transactional
    public UserRole save(UserRole userRole) {
        UserRoleEntity entity = userRole.id() == null
                ? new UserRoleEntity()
                : repository.findById(userRole.id())
                        .orElseThrow(() -> new IllegalStateException(
                                "Role assignment not found"
                        ));

        entity.user = userRepository.findById(userRole.userId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        entity.role = roleRepository.findById(userRole.roleId())
                .orElseThrow(() -> new IllegalArgumentException("Role not found"));
        entity.assignedAt = userRole.assignedAt();
        entity.assignedBy = userRole.assignedBy();
        entity.expiresAt = userRole.expiresAt();
        entity.revokedAt = userRole.revokedAt();
        entity.revokedBy = userRole.revokedBy();
        entity.reason = userRole.reason();

        return toDomain(repository.save(entity));
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserRole> findAllByUserId(Long userId) {
        return repository.findAllByUser_Id(userId)
                .stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UserRole> findActiveByUserIdAndRoleId(Long userId, Long roleId) {
        return repository.findByUser_IdAndRole_IdAndRevokedAtIsNull(userId, roleId)
                .map(this::toDomain);
    }

    private UserRole toDomain(UserRoleEntity entity) {
        return new UserRole(
                entity.id,
                entity.user.getId(),
                entity.role.getId(),
                entity.assignedAt,
                entity.assignedBy,
                entity.expiresAt,
                entity.revokedAt,
                entity.revokedBy,
                entity.reason
        );
    }
}

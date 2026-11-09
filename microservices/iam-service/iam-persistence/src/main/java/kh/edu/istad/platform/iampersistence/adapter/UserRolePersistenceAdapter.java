package kh.edu.istad.platform.iampersistence.adapter;

import kh.edu.istad.platform.iamapplicationservice.port.out.UserRoleRepository;
import kh.edu.istad.platform.iamdomaincore.model.UserRole;
import kh.edu.istad.platform.iampersistence.repository.UserRoleJpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public class UserRolePersistenceAdapter implements UserRoleRepository {

    private final UserRoleJpaRepository repository;

    public UserRolePersistenceAdapter(UserRoleJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserRole> findAllByUserId(Long userId) {
        return repository.findAllByUser_Id(userId)
                .stream()
                .map(entity -> new UserRole(
                        entity.id,
                        entity.user.id,
                        entity.role.id,
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

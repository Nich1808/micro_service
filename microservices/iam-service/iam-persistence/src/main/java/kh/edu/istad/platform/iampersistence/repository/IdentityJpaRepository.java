package kh.edu.istad.platform.iampersistence.repository;

import kh.edu.istad.platform.iamdomaincore.enums.IdentityProvider;
import kh.edu.istad.platform.iampersistence.entity.IdentityEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IdentityJpaRepository extends JpaRepository<IdentityEntity, Long> {

    Optional<IdentityEntity> findByUuid(UUID uuid);

    Optional<IdentityEntity> findByProviderAndProviderSubjectAndDeletedFalse(
            IdentityProvider provider,
            String providerSubject
    );

    List<IdentityEntity> findAllByUserIdAndDeletedFalse(Long userId);

    boolean existsByUserIdAndProviderAndDeletedFalse(
            Long userId,
            IdentityProvider provider
    );
}

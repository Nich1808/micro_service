package kh.edu.istad.platform.iampersistence.repository;

import kh.edu.istad.platform.iampersistence.entity.AccountActivityEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountActivityJpaRepository
        extends JpaRepository<AccountActivityEntity, Long> {

    Page<AccountActivityEntity> findAllByUserIdOrderByCreatedAtDesc(
            Long userId,
            Pageable pageable
    );

    Page<AccountActivityEntity> findAllByOrderByCreatedAtDesc(Pageable pageable);

    long countByUserId(Long userId);

    Page<AccountActivityEntity> findAllByTypeOrderByCreatedAtDesc(
            kh.edu.istad.platform.iamdomaincore.enums.ActivityType type,
            Pageable pageable
    );

    long countByType(kh.edu.istad.platform.iamdomaincore.enums.ActivityType type);
}

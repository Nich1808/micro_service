package kh.edu.istad.platform.iampersistence.repository;

import kh.edu.istad.platform.iampersistence.entity.LoginAttemptEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LoginAttemptJpaRepository
        extends JpaRepository<LoginAttemptEntity, Long> {

    Page<LoginAttemptEntity> findAllByUserIdOrderByAttemptedAtDesc(
            Long userId,
            Pageable pageable
    );

    long countByUserId(Long userId);

    List<LoginAttemptEntity> findAllByUserIdAndSuccessfulFalseAndAttemptedAtAfterOrderByAttemptedAtDesc(
            Long userId,
            java.time.OffsetDateTime since
    );
}

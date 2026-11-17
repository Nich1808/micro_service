package kh.edu.istad.platform.iampersistence.repository;

import kh.edu.istad.platform.iampersistence.entity.SystemSettingsEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SystemSettingsJpaRepository
        extends JpaRepository<SystemSettingsEntity, Long> {

    Optional<SystemSettingsEntity> findBySettingsKey(String settingsKey);
}

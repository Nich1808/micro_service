package kh.edu.istad.platform.iampersistence.adapter;

import kh.edu.istad.platform.iamapplicationservice.port.out.SystemSettingsRepository;
import kh.edu.istad.platform.iampersistence.entity.SystemSettingsEntity;
import kh.edu.istad.platform.iampersistence.repository.SystemSettingsJpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;

@Repository
public class SystemSettingsPersistenceAdapter implements SystemSettingsRepository {

    private static final String KEY = "default";

    private final SystemSettingsJpaRepository repository;

    public SystemSettingsPersistenceAdapter(SystemSettingsJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional(readOnly = true)
    public SystemSettings load() {
        return repository.findBySettingsKey(KEY)
                .map(this::toDomain)
                .orElseGet(SystemSettings::defaults);
    }

    @Override
    @Transactional
    public SystemSettings save(SystemSettings settings) {
        SystemSettingsEntity entity = repository.findBySettingsKey(KEY)
                .orElseGet(() -> {
                    SystemSettingsEntity created = new SystemSettingsEntity();
                    created.settingsKey = KEY;
                    return created;
                });

        entity.passwordMinLength = settings.passwordMinLength();
        entity.passwordRequireUppercase = settings.passwordRequireUppercase();
        entity.passwordRequireDigit = settings.passwordRequireDigit();
        entity.passwordRequireSymbol = settings.passwordRequireSymbol();
        entity.maxFailedLoginAttempts = settings.maxFailedLoginAttempts();
        entity.lockoutMinutes = settings.lockoutMinutes();
        entity.sessionIdleTimeoutMinutes = settings.sessionIdleTimeoutMinutes();
        entity.sessionAbsoluteTimeoutHours = settings.sessionAbsoluteTimeoutHours();
        entity.accessTokenTtlMinutes = settings.accessTokenTtlMinutes();
        entity.refreshTokenTtlDays = settings.refreshTokenTtlDays();
        entity.registrationEnabled = settings.registrationEnabled();
        entity.socialLoginEnabled = settings.socialLoginEnabled();
        entity.updatedAt = OffsetDateTime.now();
        entity.updatedBy = "system";

        return toDomain(repository.save(entity));
    }

    private SystemSettings toDomain(SystemSettingsEntity entity) {
        return new SystemSettings(
                entity.passwordMinLength,
                entity.passwordRequireUppercase,
                entity.passwordRequireDigit,
                entity.passwordRequireSymbol,
                entity.maxFailedLoginAttempts,
                entity.lockoutMinutes,
                entity.sessionIdleTimeoutMinutes,
                entity.sessionAbsoluteTimeoutHours,
                entity.accessTokenTtlMinutes,
                entity.refreshTokenTtlDays,
                entity.registrationEnabled,
                entity.socialLoginEnabled
        );
    }
}

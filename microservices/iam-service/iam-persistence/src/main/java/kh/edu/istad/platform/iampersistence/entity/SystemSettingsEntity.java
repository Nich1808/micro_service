package kh.edu.istad.platform.iampersistence.entity;

import jakarta.persistence.*;

import java.time.OffsetDateTime;

@Entity
@Table(name = "iam_system_settings")
public class SystemSettingsEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @Column(name = "settings_key", nullable = false, unique = true, length = 50)
    public String settingsKey;

    @Column(name = "password_min_length", nullable = false)
    public int passwordMinLength;

    @Column(name = "password_require_uppercase", nullable = false)
    public boolean passwordRequireUppercase;

    @Column(name = "password_require_digit", nullable = false)
    public boolean passwordRequireDigit;

    @Column(name = "password_require_symbol", nullable = false)
    public boolean passwordRequireSymbol;

    @Column(name = "max_failed_login_attempts", nullable = false)
    public int maxFailedLoginAttempts;

    @Column(name = "lockout_minutes", nullable = false)
    public int lockoutMinutes;

    @Column(name = "session_idle_timeout_minutes", nullable = false)
    public int sessionIdleTimeoutMinutes;

    @Column(name = "session_absolute_timeout_hours", nullable = false)
    public int sessionAbsoluteTimeoutHours;

    @Column(name = "access_token_ttl_minutes", nullable = false)
    public long accessTokenTtlMinutes;

    @Column(name = "refresh_token_ttl_days", nullable = false)
    public long refreshTokenTtlDays;

    @Column(name = "registration_enabled", nullable = false)
    public boolean registrationEnabled;

    @Column(name = "social_login_enabled", nullable = false)
    public boolean socialLoginEnabled;

    @Column(name = "updated_at", nullable = false)
    public OffsetDateTime updatedAt;

    @Column(name = "updated_by", nullable = false, length = 100)
    public String updatedBy;

    public SystemSettingsEntity() {
    }

    public Long getId() {
        return id;
    }
}

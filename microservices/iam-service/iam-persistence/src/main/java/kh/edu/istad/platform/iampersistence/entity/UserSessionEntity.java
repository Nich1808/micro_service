package kh.edu.istad.platform.iampersistence.entity;

import jakarta.persistence.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "iam_user_sessions")
public class UserSessionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @Column(nullable = false, unique = true, updatable = false)
    public UUID uuid;

    @Column(name = "user_id", nullable = false)
    public Long userId;

    @Column(name = "session_key", nullable = false, length = 200)
    public String sessionKey;

    @Column(length = 100)
    public String device;

    @Column(length = 100)
    public String browser;

    @Column(name = "operating_system", length = 100)
    public String operatingSystem;

    @Column(name = "ip_address", length = 45)
    public String ipAddress;

    @Column(length = 100)
    public String location;

    @Column(name = "user_agent", length = 500)
    public String userAgent;

    @Column(name = "created_at", nullable = false, updatable = false)
    public OffsetDateTime createdAt;

    @Column(name = "last_activity_at", nullable = false)
    public OffsetDateTime lastActivityAt;

    @Column(name = "expires_at")
    public OffsetDateTime expiresAt;

    @Column(name = "revoked_at")
    public OffsetDateTime revokedAt;

    @Column(name = "revoked_by", length = 100)
    public String revokedBy;

    @Column(name = "revoked_reason", length = 255)
    public String revokedReason;

    @Version
    @Column(nullable = false)
    public long version;

    public UserSessionEntity() {
    }

    public Long getId() {
        return id;
    }
}

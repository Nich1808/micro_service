package kh.edu.istad.platform.iampersistence.entity;

import jakarta.persistence.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "iam_login_attempts")
public class LoginAttemptEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @Column(nullable = false, unique = true, updatable = false)
    public UUID uuid;

    @Column(name = "user_id")
    public Long userId;

    @Column(nullable = false, length = 255)
    public String identifier;

    @Column(nullable = false)
    public boolean successful;

    @Column(name = "failure_reason", length = 100)
    public String failureReason;

    @Column(name = "ip_address", length = 45)
    public String ipAddress;

    @Column(length = 100)
    public String device;

    @Column(length = 100)
    public String browser;

    @Column(name = "operating_system", length = 100)
    public String operatingSystem;

    @Column(length = 100)
    public String location;

    @Column(name = "user_agent", length = 500)
    public String userAgent;

    @Column(name = "attempted_at", nullable = false, updatable = false)
    public OffsetDateTime attemptedAt;

    public LoginAttemptEntity() {
    }

    public Long getId() {
        return id;
    }
}

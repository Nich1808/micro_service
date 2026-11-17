package kh.edu.istad.platform.iampersistence.entity;

import jakarta.persistence.*;
import kh.edu.istad.platform.iamdomaincore.enums.ActivityType;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "iam_account_activities")
public class AccountActivityEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @Column(nullable = false, unique = true, updatable = false)
    public UUID uuid;

    @Column(name = "user_id", nullable = false)
    public Long userId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    public ActivityType type;

    @Column(length = 255)
    public String description;

    @Column(name = "ip_address", length = 45)
    public String ipAddress;

    @Column(name = "user_agent", length = 500)
    public String userAgent;

    @Column(nullable = false, length = 100)
    public String actor;

    @Column(name = "created_at", nullable = false, updatable = false)
    public OffsetDateTime createdAt;

    public AccountActivityEntity() {
    }

    public Long getId() {
        return id;
    }
}

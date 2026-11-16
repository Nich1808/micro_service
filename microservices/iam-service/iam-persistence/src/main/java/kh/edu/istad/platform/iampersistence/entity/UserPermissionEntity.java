package kh.edu.istad.platform.iampersistence.entity;

import jakarta.persistence.*;
import kh.edu.istad.platform.iamdomaincore.enums.PermissionEffect;

import java.time.OffsetDateTime;

@Entity
@Table(name = "iam_user_permissions")
public class UserPermissionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    public UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "permission_id", nullable = false)
    public PermissionEntity permission;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 5)
    public PermissionEffect effect;

    @Column(nullable = false, updatable = false)
    public OffsetDateTime assignedAt;

    @Column(nullable = false, updatable = false, length = 100)
    public String assignedBy;

    public OffsetDateTime expiresAt;

    public OffsetDateTime revokedAt;

    @Column(length = 100)
    public String revokedBy;

    @Column(length = 255)
    public String reason;

    public UserPermissionEntity() {
    }
}

package kh.edu.istad.platform.iampersistence.entity;

import jakarta.persistence.*;
import java.time.OffsetDateTime;

@Entity
@Table(name = "iam_user_roles")
public class UserRoleEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    public UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "role_id", nullable = false)
    public RoleEntity role;

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

    public UserRoleEntity() {
    }
}

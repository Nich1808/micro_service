package kh.edu.istad.platform.iampersistence.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(
    name = "iam_identities",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_iam_identity_provider_subject",
            columnNames = {"provider", "provider_subject"}
        )
    }
)
public class IdentityEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @Column(nullable = false, unique = true, updatable = false)
    public UUID uuid;

    @Column(name = "user_id", nullable = false)
    public Long userId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    public kh.edu.istad.platform.iamdomaincore.enums.IdentityProvider provider;

    @Column(name = "provider_subject", nullable = false, length = 255)
    public String providerSubject;

    @Column(length = 255)
    public String email;

    @Column(nullable = false)
    public boolean emailVerified;

    @Column(length = 150)
    public String displayName;

    @Column(length = 100)
    public String username;

    @Column(length = 500)
    public String pictureUrl;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    public Map<String, Object> attributes;

    @Column(nullable = false, updatable = false)
    public OffsetDateTime linkedAt;

    public OffsetDateTime lastLoginAt;

    @Column(name = "is_deleted", nullable = false)
    public boolean deleted;

    public OffsetDateTime deletedAt;

    @Column(length = 100)
    public String deletedBy;

    @Column(nullable = false, updatable = false)
    public OffsetDateTime createdAt;

    @Column(nullable = false, updatable = false, length = 100)
    public String createdBy;

    @Column(nullable = false)
    public OffsetDateTime updatedAt;

    @Column(nullable = false, length = 100)
    public String updatedBy;

    @Version
    @Column(nullable = false)
    public long version;

    public IdentityEntity() {
    }
}

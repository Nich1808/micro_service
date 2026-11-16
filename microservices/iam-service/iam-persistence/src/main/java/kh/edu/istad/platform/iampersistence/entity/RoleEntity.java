package kh.edu.istad.platform.iampersistence.entity;

import jakarta.persistence.*;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "iam_roles")
public class RoleEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @Column(nullable = false, unique = true, updatable = false)
    public UUID uuid;

    @Column(nullable = false, unique = true, length = 50)
    public String name;

    @Column(length = 255)
    public String description;

    @Column(name = "is_system", nullable = false)
    public boolean system;

    @Column(name = "is_active", nullable = false)
    public boolean active;

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

    public Long getId() {
        return id;
    }

    public RoleEntity() {
    }
}

package kh.edu.istad.platform.iampersistence.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "iam_users")
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @Column(nullable = false, unique = true, updatable = false)
    public UUID uuid;

    @Column(nullable = false, unique = true, length = 50)
    public String username;

    @Column(nullable = false, unique = true, length = 255)
    public String email;

    @Column(nullable = false)
    public boolean emailVerified;

    @Column(length = 255)
    public String passwordHash;

    @Column(nullable = false, length = 100)
    public String givenName;

    @Column(nullable = false, length = 100)
    public String familyName;

    @Column(length = 100)
    public String middleName;

    @Column(length = 100)
    public String nickname;

    @Column(length = 20)
    public String gender;

    public LocalDate birthdate;

    @Column(length = 500)
    public String profileUrl;

    @Column(length = 500)
    public String pictureUrl;

    @Column(length = 500)
    public String websiteUrl;

    @Column(length = 50)
    public String zoneinfo;

    @Column(nullable = false, length = 10)
    public String locale;

    @Column(length = 20)
    public String phoneNumber;

    @Column(nullable = false)
    public boolean phoneNumberVerified;

    @Column(name = "address_street_address", length = 255)
    public String streetAddress;

    @Column(name = "address_locality", length = 100)
    public String locality;

    @Column(name = "address_region", length = 100)
    public String region;

    @Column(name = "address_postal_code", length = 20)
    public String postalCode;

    @Column(name = "address_country", length = 2)
    public String country;

    @Column(nullable = false)
    public boolean enabled;

    @Column(nullable = false)
    public boolean accountNonExpired;

    @Column(nullable = false)
    public boolean accountNonLocked;

    @Column(nullable = false)
    public boolean credentialsNonExpired;

    @Column(nullable = false)
    public int failedLoginAttempts;

    public OffsetDateTime lastLoginAt;

    public OffsetDateTime passwordChangedAt;

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

    public UserEntity() {
    }
}

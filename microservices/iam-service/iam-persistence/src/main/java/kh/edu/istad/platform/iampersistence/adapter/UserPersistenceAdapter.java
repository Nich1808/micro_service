package kh.edu.istad.platform.iampersistence.adapter;

import kh.edu.istad.platform.iamapplicationservice.port.out.UserRepository;
import kh.edu.istad.platform.iamdomaincore.model.User;
import kh.edu.istad.platform.iampersistence.entity.UserEntity;
import kh.edu.istad.platform.iampersistence.repository.UserJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class UserPersistenceAdapter implements UserRepository {

    private final UserJpaRepository repository;

    public UserPersistenceAdapter(UserJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public User save(User user) {
        UserEntity entity = repository.findByUuid(user.uuid())
                .orElseGet(UserEntity::new);

        entity.uuid = user.uuid();
        entity.username = user.username();
        entity.email = user.email();
        entity.emailVerified = user.emailVerified();
        entity.passwordHash = user.passwordHash();

        entity.givenName = user.givenName();
        entity.familyName = user.familyName();
        entity.middleName = user.middleName();
        entity.nickname = user.nickname();
        entity.gender = user.gender();
        entity.birthdate = user.birthdate();
        entity.profileUrl = user.profileUrl();
        entity.pictureUrl = user.pictureUrl();
        entity.websiteUrl = user.websiteUrl();
        entity.zoneinfo = user.zoneinfo();
        entity.locale = user.locale();

        entity.phoneNumber = user.phoneNumber();
        entity.phoneNumberVerified = user.phoneNumberVerified();

        entity.streetAddress = user.streetAddress();
        entity.locality = user.locality();
        entity.region = user.region();
        entity.postalCode = user.postalCode();
        entity.country = user.country();

        entity.enabled = user.enabled();
        entity.accountNonExpired = user.accountNonExpired();
        entity.accountNonLocked = user.accountNonLocked();
        entity.credentialsNonExpired = user.credentialsNonExpired();

        entity.failedLoginAttempts = user.failedLoginAttempts();
        entity.lastLoginAt = user.lastLoginAt();
        entity.passwordChangedAt = user.passwordChangedAt();

        entity.deleted = user.deleted();
        entity.deletedAt = user.deletedAt();
        entity.deletedBy = user.deletedBy();

        entity.createdAt = user.createdAt();
        entity.createdBy = user.createdBy();
        entity.updatedAt = user.updatedAt();
        entity.updatedBy = user.updatedBy();

        return toDomain(repository.save(entity));
    }

    @Override
    public Optional<User> findByUuid(UUID uuid) {
        return repository.findByUuid(uuid).map(this::toDomain);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return repository.findByUsername(username).map(this::toDomain);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return repository.findByEmail(email).map(this::toDomain);
    }

    @Override
    public Optional<User> findByUsernameOrEmail(String usernameOrEmail) {
        return repository.findByUsernameOrEmail(usernameOrEmail)
                .map(this::toDomain);
    }

    @Override
    public boolean existsByUsername(String username) {
        return repository.existsByUsername(username);
    }

    @Override
    public boolean existsByEmail(String email) {
        return repository.existsByEmail(email);
    }

    private User toDomain(UserEntity entity) {
        return new User(
                entity.id,
                entity.uuid,
                entity.username,
                entity.email,
                entity.emailVerified,
                entity.passwordHash,
                entity.givenName,
                entity.familyName,
                entity.middleName,
                entity.nickname,
                entity.gender,
                entity.birthdate,
                entity.profileUrl,
                entity.pictureUrl,
                entity.websiteUrl,
                entity.zoneinfo,
                entity.locale,
                entity.phoneNumber,
                entity.phoneNumberVerified,
                entity.streetAddress,
                entity.locality,
                entity.region,
                entity.postalCode,
                entity.country,
                entity.enabled,
                entity.accountNonExpired,
                entity.accountNonLocked,
                entity.credentialsNonExpired,
                entity.failedLoginAttempts,
                entity.lastLoginAt,
                entity.passwordChangedAt,
                entity.deleted,
                entity.deletedAt,
                entity.deletedBy,
                entity.createdAt,
                entity.createdBy,
                entity.updatedAt,
                entity.updatedBy,
                entity.version
        );
    }
}

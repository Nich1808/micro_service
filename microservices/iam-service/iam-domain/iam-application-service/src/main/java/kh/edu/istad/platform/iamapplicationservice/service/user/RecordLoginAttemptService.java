package kh.edu.istad.platform.iamapplicationservice.service.user;

import kh.edu.istad.platform.iamapplicationservice.command.security.LoginAttemptCommand;
import kh.edu.istad.platform.iamapplicationservice.port.out.CurrentActorProvider;
import kh.edu.istad.platform.iamapplicationservice.port.out.AccountActivityRepository;
import kh.edu.istad.platform.iamapplicationservice.port.out.LoginAttemptRepository;
import kh.edu.istad.platform.iamapplicationservice.port.out.SystemSettingsRepository;
import kh.edu.istad.platform.iamapplicationservice.port.out.UserRepository;
import kh.edu.istad.platform.iamapplicationservice.service.security.ActivitySupport;
import kh.edu.istad.platform.iamapplicationservice.usecase.user.RecordLoginAttemptUseCase;
import kh.edu.istad.platform.iamdomaincore.enums.ActivityType;
import kh.edu.istad.platform.iamdomaincore.model.LoginAttempt;
import kh.edu.istad.platform.iamdomaincore.model.User;

import java.time.OffsetDateTime;

public class RecordLoginAttemptService implements RecordLoginAttemptUseCase {

    private final UserRepository userRepository;
    private final LoginAttemptRepository loginAttemptRepository;
    private final CurrentActorProvider currentActorProvider;
    private final SystemSettingsRepository systemSettingsRepository;
    private final AccountActivityRepository accountActivityRepository;

    public RecordLoginAttemptService(
            UserRepository userRepository,
            LoginAttemptRepository loginAttemptRepository,
            CurrentActorProvider currentActorProvider,
            SystemSettingsRepository systemSettingsRepository,
            AccountActivityRepository accountActivityRepository
    ) {
        this.userRepository = userRepository;
        this.loginAttemptRepository = loginAttemptRepository;
        this.currentActorProvider = currentActorProvider;
        this.systemSettingsRepository = systemSettingsRepository;
        this.accountActivityRepository = accountActivityRepository;
    }

    @Override
    public void execute(LoginAttemptCommand command) {
        if (command == null || command.identifier() == null
                || command.identifier().isBlank()) {
            throw new IllegalArgumentException("Username or email is required");
        }

        String identifier = command.identifier().trim();

        userRepository.findByUsernameOrEmail(identifier)
                .filter(user -> !user.deleted())
                .ifPresent(user -> {
                    OffsetDateTime now = OffsetDateTime.now();
                    String actor = currentActorProvider.getCurrentActor();

                    int failedAttempts = command.successful()
                            ? 0
                            : Math.addExact(user.failedLoginAttempts(), 1);

                    OffsetDateTime lastLoginAt = command.successful()
                            ? now
                            : user.lastLoginAt();

                    boolean lockAccount = shouldLock(user, failedAttempts);

                    userRepository.save(withLoginState(
                            user, failedAttempts, lastLoginAt, actor, now, lockAccount
                    ));

                    if (lockAccount) {
                        ActivitySupport.record(
                                accountActivityRepository,
                                user.id(),
                                ActivityType.ACCOUNT_LOCKED,
                                "Account locked after " + failedAttempts
                                        + " failed sign-in attempts",
                                "system"
                        );
                    }

                    loginAttemptRepository.save(new LoginAttempt(
                            null,
                            null,
                            user.id(),
                            identifier,
                            command.successful(),
                            command.failureReason(),
                            command.ipAddress(),
                            command.device(),
                            command.browser(),
                            command.operatingSystem(),
                            command.location(),
                            command.userAgent(),
                            now
                    ));
                });
    }

    /**
     * Locks the account once the configured number of consecutive failures is reached.
     * An already-locked or disabled account is left untouched.
     */
    private boolean shouldLock(User user, int failedAttempts) {
        if (!user.accountNonLocked() || !user.enabled()) {
            return false;
        }

        int threshold = systemSettingsRepository.load().maxFailedLoginAttempts();
        return failedAttempts >= threshold;
    }

    private User withLoginState(
            User user,
            int failedAttempts,
            OffsetDateTime lastLoginAt,
            String actor,
            OffsetDateTime now,
            boolean lockAccount
    ) {
        return new User(
                user.id(), user.uuid(),
                user.username(), user.email(),
                user.emailVerified(), user.passwordHash(),
                user.givenName(), user.familyName(),
                user.middleName(), user.nickname(),
                user.gender(), user.birthdate(),
                user.profileUrl(), user.pictureUrl(),
                user.websiteUrl(), user.zoneinfo(),
                user.locale(),
                user.phoneNumber(), user.phoneNumberVerified(),
                user.streetAddress(), user.locality(),
                user.region(), user.postalCode(),
                user.country(),
                user.enabled(),
                user.accountNonExpired(),
                lockAccount ? false : user.accountNonLocked(),
                user.credentialsNonExpired(),
                failedAttempts,
                lastLoginAt,
                user.passwordChangedAt(),
                user.deleted(),
                user.deletedAt(),
                user.deletedBy(),
                user.createdAt(),
                user.createdBy(),
                now,
                actor,
                user.version() + 1
        );
    }
}

package kh.edu.istad.platform.iamapplicationservice.service.user;

import kh.edu.istad.platform.iamapplicationservice.port.out.AccountActivityRepository;
import kh.edu.istad.platform.iamapplicationservice.port.out.CurrentActorProvider;
import kh.edu.istad.platform.iamapplicationservice.port.out.UserRepository;
import kh.edu.istad.platform.iamapplicationservice.port.out.UserSessionRepository;
import kh.edu.istad.platform.iamapplicationservice.service.security.ActivitySupport;
import kh.edu.istad.platform.iamapplicationservice.usecase.user.DeactivateAccountUseCase;
import kh.edu.istad.platform.iamdomaincore.enums.ActivityType;
import kh.edu.istad.platform.iamdomaincore.model.User;
import kh.edu.istad.platform.iamdomaincore.model.UserSession;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Self-service deactivation: the account is disabled (it can be re-enabled by an
 * administrator), all sessions are revoked and the event is recorded.
 */
public class DeactivateAccountService implements DeactivateAccountUseCase {

    private final UserRepository userRepository;
    private final UserSessionRepository userSessionRepository;
    private final AccountActivityRepository accountActivityRepository;
    private final CurrentActorProvider actorProvider;

    public DeactivateAccountService(
            UserRepository userRepository,
            UserSessionRepository userSessionRepository,
            AccountActivityRepository accountActivityRepository,
            CurrentActorProvider actorProvider
    ) {
        this.userRepository = userRepository;
        this.userSessionRepository = userSessionRepository;
        this.accountActivityRepository = accountActivityRepository;
        this.actorProvider = actorProvider;
    }

    @Override
    public void execute(UUID userUuid, String reason) {
        User user = UserStateSupport.requireActiveUser(userRepository, userUuid);

        String actor = actorProvider.getCurrentActor();

        if (actor == null || actor.isBlank()) {
            throw new IllegalStateException("Current actor is required");
        }

        OffsetDateTime now = OffsetDateTime.now();

        userRepository.save(UserStateSupport.changeStatus(
                user,
                false,
                user.accountNonLocked(),
                actor,
                now
        ));

        List<UserSession> active =
                userSessionRepository.findActiveByUserId(user.id(), now);

        for (UserSession session : active) {
            userSessionRepository.save(session.revoke(
                    actor,
                    now,
                    "Account deactivated"
            ));
        }

        ActivitySupport.record(
                accountActivityRepository,
                user.id(),
                ActivityType.ACCOUNT_DEACTIVATED,
                reason == null || reason.isBlank()
                        ? "Account deactivated by the owner"
                        : reason,
                actor
        );
    }
}

package kh.edu.istad.platform.iamapplicationservice.service.session;

import kh.edu.istad.platform.iamapplicationservice.port.out.AccountActivityRepository;
import kh.edu.istad.platform.iamapplicationservice.port.out.CurrentActorProvider;
import kh.edu.istad.platform.iamapplicationservice.port.out.UserRepository;
import kh.edu.istad.platform.iamapplicationservice.port.out.UserSessionRepository;
import kh.edu.istad.platform.iamapplicationservice.service.security.ActivitySupport;
import kh.edu.istad.platform.iamapplicationservice.usecase.session.TerminateAllSessionsUseCase;
import kh.edu.istad.platform.iamdomaincore.enums.ActivityType;
import kh.edu.istad.platform.iamdomaincore.model.User;
import kh.edu.istad.platform.iamdomaincore.model.UserSession;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public class TerminateAllSessionsService implements TerminateAllSessionsUseCase {

    private final UserRepository userRepository;
    private final UserSessionRepository userSessionRepository;
    private final AccountActivityRepository accountActivityRepository;
    private final CurrentActorProvider actorProvider;

    public TerminateAllSessionsService(
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
    public int execute(UUID userUuid, String keepKey) {
        if (userUuid == null) {
            throw new IllegalArgumentException("User UUID is required");
        }

        User user = userRepository.findByUuid(userUuid)
                .filter(found -> !found.deleted())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        String actor = actorProvider.getCurrentActor();

        if (actor == null || actor.isBlank()) {
            throw new IllegalStateException("Current actor is required");
        }

        OffsetDateTime now = OffsetDateTime.now();

        List<UserSession> active = userSessionRepository
                .findActiveByUserId(user.id(), now);

        int revoked = 0;

        for (UserSession session : active) {
            if (keepKey != null && keepKey.equals(session.sessionKey())) {
                continue;
            }

            userSessionRepository.save(session.revoke(
                    actor,
                    now,
                    "Signed out from all devices"
            ));
            revoked++;
        }

        if (revoked > 0) {
            ActivitySupport.record(
                    accountActivityRepository,
                    user.id(),
                    ActivityType.SESSION_REVOKED_ALL,
                    revoked + " other session(s) terminated",
                    actor
            );
        }

        return revoked;
    }
}

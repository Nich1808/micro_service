package kh.edu.istad.platform.iamapplicationservice.service.session;

import kh.edu.istad.platform.iamapplicationservice.port.out.AccountActivityRepository;
import kh.edu.istad.platform.iamapplicationservice.port.out.CurrentActorProvider;
import kh.edu.istad.platform.iamapplicationservice.port.out.UserRepository;
import kh.edu.istad.platform.iamapplicationservice.port.out.UserSessionRepository;
import kh.edu.istad.platform.iamapplicationservice.service.security.ActivitySupport;
import kh.edu.istad.platform.iamapplicationservice.usecase.session.TerminateSessionUseCase;
import kh.edu.istad.platform.iamdomaincore.enums.ActivityType;
import kh.edu.istad.platform.iamdomaincore.model.User;
import kh.edu.istad.platform.iamdomaincore.model.UserSession;

import java.time.OffsetDateTime;
import java.util.UUID;

public class TerminateSessionService implements TerminateSessionUseCase {

    private final UserRepository userRepository;
    private final UserSessionRepository userSessionRepository;
    private final AccountActivityRepository accountActivityRepository;
    private final CurrentActorProvider actorProvider;

    public TerminateSessionService(
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
    public void execute(UUID userUuid, UUID sessionUuid, String reason) {
        if (userUuid == null || sessionUuid == null) {
            throw new IllegalArgumentException(
                    "User UUID and session UUID are required"
            );
        }

        User user = userRepository.findByUuid(userUuid)
                .filter(found -> !found.deleted())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        UserSession session = userSessionRepository.findByUuid(sessionUuid)
                .orElseThrow(() -> new IllegalArgumentException("Session not found"));

        if (!session.userId().equals(user.id())) {
            throw new IllegalStateException("Session does not belong to this user");
        }

        String actor = actorProvider.getCurrentActor();

        if (actor == null || actor.isBlank()) {
            throw new IllegalStateException("Current actor is required");
        }

        userSessionRepository.save(session.revoke(
                actor,
                OffsetDateTime.now(),
                reason == null || reason.isBlank() ? "Terminated by user" : reason
        ));

        ActivitySupport.record(
                accountActivityRepository,
                user.id(),
                ActivityType.SESSION_REVOKED,
                "Session " + session.device() + " terminated",
                actor
        );
    }
}

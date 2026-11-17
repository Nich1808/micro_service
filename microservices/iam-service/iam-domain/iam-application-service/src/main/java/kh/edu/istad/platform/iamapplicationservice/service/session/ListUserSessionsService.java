package kh.edu.istad.platform.iamapplicationservice.service.session;

import kh.edu.istad.platform.iamapplicationservice.port.out.UserRepository;
import kh.edu.istad.platform.iamapplicationservice.port.out.UserSessionRepository;
import kh.edu.istad.platform.iamapplicationservice.result.session.UserSessionResult;
import kh.edu.istad.platform.iamapplicationservice.usecase.session.ListUserSessionsUseCase;
import kh.edu.istad.platform.iamdomaincore.model.User;
import kh.edu.istad.platform.iamdomaincore.model.UserSession;

import java.time.OffsetDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

public class ListUserSessionsService implements ListUserSessionsUseCase {

    private final UserRepository userRepository;
    private final UserSessionRepository userSessionRepository;

    public ListUserSessionsService(
            UserRepository userRepository,
            UserSessionRepository userSessionRepository
    ) {
        this.userRepository = userRepository;
        this.userSessionRepository = userSessionRepository;
    }

    @Override
    public List<UserSessionResult> execute(UUID userUuid, String currentKey) {
        if (userUuid == null) {
            throw new IllegalArgumentException("User UUID is required");
        }

        User user = userRepository.findByUuid(userUuid)
                .filter(found -> !found.deleted())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        OffsetDateTime now = OffsetDateTime.now();

        return userSessionRepository.findActiveByUserId(user.id(), now)
                .stream()
                .sorted(Comparator.comparing(
                        UserSession::lastActivityAt,
                        Comparator.nullsLast(Comparator.reverseOrder())
                ))
                .map(session -> toResult(session, currentKey))
                .toList();
    }

    private UserSessionResult toResult(UserSession session, String currentKey) {
        return new UserSessionResult(
                session.uuid(),
                session.device(),
                session.browser(),
                session.operatingSystem(),
                session.ipAddress(),
                session.location(),
                session.createdAt(),
                session.lastActivityAt(),
                session.expiresAt(),
                currentKey != null && currentKey.equals(session.sessionKey())
        );
    }
}

package kh.edu.istad.platform.iamapplicationservice.service.security;

import kh.edu.istad.platform.iamapplicationservice.port.out.LoginAttemptRepository;
import kh.edu.istad.platform.iamapplicationservice.port.out.UserRepository;
import kh.edu.istad.platform.iamapplicationservice.result.common.PageResult;
import kh.edu.istad.platform.iamapplicationservice.result.security.LoginAttemptResult;
import kh.edu.istad.platform.iamapplicationservice.usecase.security.ListLoginHistoryUseCase;
import kh.edu.istad.platform.iamdomaincore.model.LoginAttempt;
import kh.edu.istad.platform.iamdomaincore.model.User;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class ListLoginHistoryService implements ListLoginHistoryUseCase {

    private static final int MAX_PAGE_SIZE = 100;

    /** Three or more failures inside this window count as a suspicious burst. */
    private static final int SUSPICIOUS_THRESHOLD = 3;
    private static final Duration SUSPICIOUS_WINDOW = Duration.ofMinutes(15);

    private final UserRepository userRepository;
    private final LoginAttemptRepository loginAttemptRepository;

    public ListLoginHistoryService(
            UserRepository userRepository,
            LoginAttemptRepository loginAttemptRepository
    ) {
        this.userRepository = userRepository;
        this.loginAttemptRepository = loginAttemptRepository;
    }

    @Override
    public PageResult<LoginAttemptResult> execute(UUID userUuid, int page, int size) {
        if (userUuid == null) {
            throw new IllegalArgumentException("User UUID is required");
        }

        int safePage = Math.max(page, 0);
        int safeSize = size < 1 ? 20 : Math.min(size, MAX_PAGE_SIZE);

        User user = userRepository.findByUuid(userUuid)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        List<LoginAttempt> attempts = loginAttemptRepository
                .findAllByUserId(user.id(), safePage, safeSize);

        Set<UUID> suspicious = suspiciousAttempts(user.id(), attempts);

        List<LoginAttemptResult> content = attempts.stream()
                .map(attempt -> toResult(attempt, suspicious.contains(attempt.uuid())))
                .toList();

        long total = loginAttemptRepository.countByUserId(user.id());

        return new PageResult<>(
                content,
                new PageResult.Page(
                        safePage,
                        safeSize,
                        total,
                        (int) ((total + safeSize - 1) / safeSize)
                )
        );
    }

    /**
     * A failed attempt is suspicious when at least {@link #SUSPICIOUS_THRESHOLD} failures
     * happened within {@link #SUSPICIOUS_WINDOW} before it. The burst is always computed
     * from the full window in the repository, so a burst that started on a previous page
     * is still detected.
     */
    private Set<UUID> suspiciousAttempts(Long userId, List<LoginAttempt> page) {
        if (page.stream().noneMatch(attempt -> !attempt.successful())) {
            return Set.of();
        }

        OffsetDateTime oldest = page.stream()
                .map(LoginAttempt::attemptedAt)
                .min(OffsetDateTime::compareTo)
                .orElse(OffsetDateTime.now());

        // The full window is loaded from the repository so a burst that started on an
        // earlier page is still detected.
        List<LoginAttempt> failures = loginAttemptRepository.findFailuresSince(
                userId,
                oldest.minus(SUSPICIOUS_WINDOW)
        );

        if (failures.isEmpty()) {
            return Set.of();
        }

        Set<UUID> suspicious = new java.util.HashSet<>();

        for (LoginAttempt candidate : failures) {
            OffsetDateTime windowStart = candidate.attemptedAt()
                    .minus(SUSPICIOUS_WINDOW);

            long burst = 0;

            for (LoginAttempt other : failures) {
                if (!other.attemptedAt().isBefore(windowStart)
                        && !other.attemptedAt().isAfter(candidate.attemptedAt())) {
                    burst++;
                }
            }

            if (burst >= SUSPICIOUS_THRESHOLD) {
                suspicious.add(candidate.uuid());
            }
        }

        return suspicious;
    }

    private LoginAttemptResult toResult(LoginAttempt attempt, boolean suspicious) {
        return new LoginAttemptResult(
                attempt.uuid(),
                attempt.successful(),
                attempt.failureReason(),
                attempt.identifier(),
                attempt.ipAddress(),
                attempt.device(),
                attempt.browser(),
                attempt.operatingSystem(),
                attempt.location(),
                attempt.attemptedAt(),
                suspicious
        );
    }
}

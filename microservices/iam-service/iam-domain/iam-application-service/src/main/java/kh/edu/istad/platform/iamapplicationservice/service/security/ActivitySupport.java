package kh.edu.istad.platform.iamapplicationservice.service.security;

import kh.edu.istad.platform.iamapplicationservice.port.out.AccountActivityRepository;
import kh.edu.istad.platform.iamdomaincore.enums.ActivityType;
import kh.edu.istad.platform.iamdomaincore.model.AccountActivity;

import java.time.OffsetDateTime;

/**
 * Writes an entry to the account activity feed. Kept separate from the use cases so every
 * service records events the same way.
 *
 * <p>{@code userId} may be null for system-scoped events such as a change to a role,
 * permission, client or system setting: those belong to the audit trail rather than to one
 * person's activity feed.
 */
public final class ActivitySupport {

    private ActivitySupport() {
    }

    public static void record(
            AccountActivityRepository repository,
            Long userId,
            ActivityType type,
            String description,
            String actor
    ) {
        record(repository, userId, type, description, actor, null, null);
    }

    public static void record(
            AccountActivityRepository repository,
            Long userId,
            ActivityType type,
            String description,
            String actor,
            String ipAddress,
            String userAgent
    ) {
        if (repository == null || type == null) {
            return;
        }

        repository.save(new AccountActivity(
                null,
                null,
                userId,
                type,
                description,
                ipAddress,
                userAgent,
                actor,
                OffsetDateTime.now()
        ));
    }
}

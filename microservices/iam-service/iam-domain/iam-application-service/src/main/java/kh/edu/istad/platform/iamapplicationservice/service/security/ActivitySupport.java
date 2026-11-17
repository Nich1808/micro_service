package kh.edu.istad.platform.iamapplicationservice.service.security;

import kh.edu.istad.platform.iamapplicationservice.port.out.AccountActivityRepository;
import kh.edu.istad.platform.iamdomaincore.enums.ActivityType;
import kh.edu.istad.platform.iamdomaincore.model.AccountActivity;

import java.time.OffsetDateTime;

/**
 * Writes an entry to the account activity feed. Kept separate from the use cases so every
 * service records events the same way.
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
        if (repository == null || userId == null || type == null) {
            return;
        }

        repository.save(new AccountActivity(
                null,
                null,
                userId,
                type,
                description,
                null,
                null,
                actor,
                OffsetDateTime.now()
        ));
    }
}

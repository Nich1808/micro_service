package kh.edu.istad.platform.iamapplicationservice.result.admin;

import java.time.OffsetDateTime;
import java.util.List;

public record AdminOverviewResult(
        long totalUsers,
        long activeUsers,
        long lockedUsers,
        long disabledUsers,
        long totalRoles,
        long totalPermissions,
        long totalClients,
        long activeSessions,
        long loginAttemptsToday,
        long failedLoginsToday,
        List<RecentEvent> recentEvents
) {
    public AdminOverviewResult {
        recentEvents = recentEvents == null ? List.of() : List.copyOf(recentEvents);
    }

    public record RecentEvent(
            String type,
            String description,
            String actor,
            OffsetDateTime createdAt
    ) {
    }
}

package kh.edu.istad.platform.iamapplicationservice.port.out;

import java.time.OffsetDateTime;
import java.util.List;

/**
 * Aggregated counters for the admin overview dashboard.
 */
public interface AdminStatisticsQuery {

    Snapshot snapshot();

    record Snapshot(
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
        public Snapshot {
            recentEvents = recentEvents == null ? List.of() : List.copyOf(recentEvents);
        }
    }

    record RecentEvent(
            String type,
            String description,
            String actor,
            OffsetDateTime createdAt
    ) {
    }
}

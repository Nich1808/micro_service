package kh.edu.istad.platform.iamapplicationservice.service.admin;

import kh.edu.istad.platform.iamapplicationservice.port.out.AdminStatisticsQuery;
import kh.edu.istad.platform.iamapplicationservice.result.admin.AdminOverviewResult;
import kh.edu.istad.platform.iamapplicationservice.usecase.admin.AdminUseCases;

public class GetAdminOverviewService implements AdminUseCases.GetAdminOverview {

    private final AdminStatisticsQuery adminStatisticsQuery;

    public GetAdminOverviewService(AdminStatisticsQuery adminStatisticsQuery) {
        this.adminStatisticsQuery = adminStatisticsQuery;
    }

    @Override
    public AdminOverviewResult execute() {
        AdminStatisticsQuery.Snapshot snapshot = adminStatisticsQuery.snapshot();

        return new AdminOverviewResult(
                snapshot.totalUsers(),
                snapshot.activeUsers(),
                snapshot.lockedUsers(),
                snapshot.disabledUsers(),
                snapshot.totalRoles(),
                snapshot.totalPermissions(),
                snapshot.totalClients(),
                snapshot.activeSessions(),
                snapshot.loginAttemptsToday(),
                snapshot.failedLoginsToday(),
                snapshot.recentEvents().stream()
                        .map(event -> new AdminOverviewResult.RecentEvent(
                                event.type(),
                                event.description(),
                                event.actor(),
                                event.createdAt()
                        ))
                        .toList()
        );
    }
}

package kh.edu.istad.platform.iamrestapi.controller;

import kh.edu.istad.platform.iamapplicationservice.command.admin.UpdateSystemSettingsCommand;
import kh.edu.istad.platform.iamapplicationservice.port.out.SystemSettingsRepository;
import kh.edu.istad.platform.iamapplicationservice.result.admin.AdminOverviewResult;
import kh.edu.istad.platform.iamapplicationservice.usecase.admin.AdminUseCases;
import kh.edu.istad.platform.iamrestapi.dto.admin.AdminDto;
import kh.edu.istad.platform.iamrestapi.dto.response.AccountActivityResponse;
import kh.edu.istad.platform.iamrestapi.dto.response.PageResponse;
import kh.edu.istad.platform.iamrestapi.mapper.ApiMapper;
import kh.edu.istad.platform.iamdomaincore.enums.ActivityType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Admin overview, audit trail and security configuration.
 */
@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminUseCases.GetAdminOverview getAdminOverview;
    private final AdminUseCases.ListAuditLogs listAuditLogs;
    private final AdminUseCases.GetSystemSettings getSystemSettings;
    private final AdminUseCases.UpdateSystemSettings updateSystemSettings;

    public AdminController(
            AdminUseCases.GetAdminOverview getAdminOverview,
            AdminUseCases.ListAuditLogs listAuditLogs,
            AdminUseCases.GetSystemSettings getSystemSettings,
            AdminUseCases.UpdateSystemSettings updateSystemSettings
    ) {
        this.getAdminOverview = getAdminOverview;
        this.listAuditLogs = listAuditLogs;
        this.getSystemSettings = getSystemSettings;
        this.updateSystemSettings = updateSystemSettings;
    }

    @GetMapping("/overview")
    @PreAuthorize("hasAuthority('user:read')")
    public AdminDto.AdminOverviewResponse overview() {
        AdminOverviewResult result = getAdminOverview.execute();

        return new AdminDto.AdminOverviewResponse(
                result.totalUsers(),
                result.activeUsers(),
                result.lockedUsers(),
                result.disabledUsers(),
                result.totalRoles(),
                result.totalPermissions(),
                result.totalClients(),
                result.activeSessions(),
                result.loginAttemptsToday(),
                result.failedLoginsToday(),
                result.recentEvents().stream()
                        .map(event -> new AdminDto.AdminOverviewResponse.RecentEvent(
                                event.type(), event.description(),
                                event.actor(), event.createdAt()
                        ))
                        .toList()
        );
    }

    @GetMapping("/audit-logs")
    @PreAuthorize("hasAuthority('user:read')")
    public PageResponse<AccountActivityResponse> auditLogs(
            @RequestParam(required = false) ActivityType type,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return ApiMapper.toPageResponse(
                listAuditLogs.execute(type, page, size),
                ApiMapper::toResponse
        );
    }

    @GetMapping("/settings")
    @PreAuthorize("hasAuthority('permission:read')")
    public AdminDto.SystemSettingsResponse settings() {
        return toResponse(getSystemSettings.execute());
    }

    @PatchMapping("/settings")
    @PreAuthorize("hasAuthority('permission:update')")
    public AdminDto.SystemSettingsResponse updateSettings(
            @RequestBody AdminDto.UpdateSystemSettingsRequest request
    ) {
        return toResponse(updateSystemSettings.execute(
                new UpdateSystemSettingsCommand(
                        request.passwordMinLength(),
                        request.passwordRequireUppercase(),
                        request.passwordRequireDigit(),
                        request.passwordRequireSymbol(),
                        request.maxFailedLoginAttempts(),
                        request.lockoutMinutes(),
                        request.sessionIdleTimeoutMinutes(),
                        request.sessionAbsoluteTimeoutHours(),
                        request.accessTokenTtlMinutes(),
                        request.refreshTokenTtlDays(),
                        request.registrationEnabled(),
                        request.socialLoginEnabled()
                )
        ));
    }

    private AdminDto.SystemSettingsResponse toResponse(
            SystemSettingsRepository.SystemSettings settings
    ) {
        return new AdminDto.SystemSettingsResponse(
                settings.passwordMinLength(),
                settings.passwordRequireUppercase(),
                settings.passwordRequireDigit(),
                settings.passwordRequireSymbol(),
                settings.maxFailedLoginAttempts(),
                settings.lockoutMinutes(),
                settings.sessionIdleTimeoutMinutes(),
                settings.sessionAbsoluteTimeoutHours(),
                settings.accessTokenTtlMinutes(),
                settings.refreshTokenTtlDays(),
                settings.registrationEnabled(),
                settings.socialLoginEnabled()
        );
    }
}

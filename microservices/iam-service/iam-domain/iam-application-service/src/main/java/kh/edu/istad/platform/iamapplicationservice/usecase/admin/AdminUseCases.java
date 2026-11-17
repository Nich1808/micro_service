package kh.edu.istad.platform.iamapplicationservice.usecase.admin;

import kh.edu.istad.platform.iamapplicationservice.command.admin.ClientCommand;
import kh.edu.istad.platform.iamapplicationservice.command.admin.UpdateSystemSettingsCommand;
import kh.edu.istad.platform.iamapplicationservice.result.admin.AdminOverviewResult;
import kh.edu.istad.platform.iamapplicationservice.result.admin.ClientResult;
import kh.edu.istad.platform.iamapplicationservice.result.security.AccountActivityResult;
import kh.edu.istad.platform.iamapplicationservice.result.common.PageResult;

import java.util.List;

/**
 * Administrative operations that span several aggregates. Each still maps to one
 * responsibility, so they live as nested interfaces rather than one fat service.
 */
public interface AdminUseCases {

    interface GetAdminOverview {
        AdminOverviewResult execute();
    }

    interface ListAuditLogs {
        PageResult<AccountActivityResult> execute(
                kh.edu.istad.platform.iamdomaincore.enums.ActivityType type,
                int page,
                int size
        );
    }

    interface ListClients {
        List<ClientResult> execute();
    }

    interface GetClient {
        ClientResult execute(String clientId);
    }

    interface CreateClient {
        ClientResult execute(ClientCommand command);
    }

    interface UpdateClient {
        ClientResult execute(String clientId, ClientCommand command);
    }

    interface DeleteClient {
        void execute(String clientId);
    }

    interface GetSystemSettings {
        kh.edu.istad.platform.iamapplicationservice.port.out.SystemSettingsRepository.SystemSettings execute();
    }

    interface UpdateSystemSettings {
        kh.edu.istad.platform.iamapplicationservice.port.out.SystemSettingsRepository.SystemSettings execute(
                UpdateSystemSettingsCommand command
        );
    }
}

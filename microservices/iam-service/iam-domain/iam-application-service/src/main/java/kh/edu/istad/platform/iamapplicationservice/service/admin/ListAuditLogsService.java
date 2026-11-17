package kh.edu.istad.platform.iamapplicationservice.service.admin;

import kh.edu.istad.platform.iamapplicationservice.port.out.AccountActivityRepository;
import kh.edu.istad.platform.iamapplicationservice.result.common.PageResult;
import kh.edu.istad.platform.iamapplicationservice.result.security.AccountActivityResult;
import kh.edu.istad.platform.iamapplicationservice.usecase.admin.AdminUseCases;
import kh.edu.istad.platform.iamdomaincore.enums.ActivityType;
import kh.edu.istad.platform.iamdomaincore.model.AccountActivity;

import java.util.List;

/**
 * System-wide audit trail, newest first.
 */
public class ListAuditLogsService implements AdminUseCases.ListAuditLogs {

    private static final int MAX_PAGE_SIZE = 100;

    private final AccountActivityRepository accountActivityRepository;

    public ListAuditLogsService(AccountActivityRepository accountActivityRepository) {
        this.accountActivityRepository = accountActivityRepository;
    }

    @Override
    public PageResult<AccountActivityResult> execute(
            ActivityType type,
            int page,
            int size
    ) {
        int safePage = Math.max(page, 0);
        int safeSize = size < 1 ? 20 : Math.min(size, MAX_PAGE_SIZE);

        List<AccountActivityResult> content = accountActivityRepository
                .findAuditLogs(type, safePage, safeSize)
                .stream()
                .map(this::toResult)
                .toList();

        long total = accountActivityRepository.countAuditLogs(type);

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

    private AccountActivityResult toResult(AccountActivity activity) {
        return new AccountActivityResult(
                activity.uuid(),
                activity.type(),
                activity.description(),
                activity.ipAddress(),
                activity.userAgent(),
                activity.actor(),
                activity.createdAt()
        );
    }
}

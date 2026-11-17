package kh.edu.istad.platform.iamapplicationservice.port.out;

import kh.edu.istad.platform.iamdomaincore.model.AccountActivity;

import java.util.List;

public interface AccountActivityRepository {

    AccountActivity save(AccountActivity activity);

    List<AccountActivity> findAllByUserId(Long userId, int page, int size);

    List<AccountActivity> findAll(int page, int size);

    long countByUserId(Long userId);

    long count();

    /** Audit-log view filtered by event type; {@code type} may be null for "all". */
    List<AccountActivity> findAuditLogs(
            kh.edu.istad.platform.iamdomaincore.enums.ActivityType type,
            int page,
            int size
    );

    long countAuditLogs(kh.edu.istad.platform.iamdomaincore.enums.ActivityType type);
}

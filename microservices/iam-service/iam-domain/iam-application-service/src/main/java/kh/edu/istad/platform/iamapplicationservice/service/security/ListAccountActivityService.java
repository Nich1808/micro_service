package kh.edu.istad.platform.iamapplicationservice.service.security;

import kh.edu.istad.platform.iamapplicationservice.port.out.AccountActivityRepository;
import kh.edu.istad.platform.iamapplicationservice.port.out.UserRepository;
import kh.edu.istad.platform.iamapplicationservice.result.common.PageResult;
import kh.edu.istad.platform.iamapplicationservice.result.security.AccountActivityResult;
import kh.edu.istad.platform.iamapplicationservice.usecase.security.ListAccountActivityUseCase;
import kh.edu.istad.platform.iamdomaincore.model.AccountActivity;
import kh.edu.istad.platform.iamdomaincore.model.User;

import java.util.List;
import java.util.UUID;

public class ListAccountActivityService implements ListAccountActivityUseCase {

    private static final int MAX_PAGE_SIZE = 100;

    private final UserRepository userRepository;
    private final AccountActivityRepository accountActivityRepository;

    public ListAccountActivityService(
            UserRepository userRepository,
            AccountActivityRepository accountActivityRepository
    ) {
        this.userRepository = userRepository;
        this.accountActivityRepository = accountActivityRepository;
    }

    @Override
    public PageResult<AccountActivityResult> execute(UUID userUuid, int page, int size) {
        if (userUuid == null) {
            throw new IllegalArgumentException("User UUID is required");
        }

        int safePage = Math.max(page, 0);
        int safeSize = size < 1 ? 20 : Math.min(size, MAX_PAGE_SIZE);

        User user = userRepository.findByUuid(userUuid)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        List<AccountActivityResult> content = accountActivityRepository
                .findAllByUserId(user.id(), safePage, safeSize)
                .stream()
                .map(this::toResult)
                .toList();

        long total = accountActivityRepository.countByUserId(user.id());

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

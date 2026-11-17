package kh.edu.istad.platform.iamapplicationservice.service.user;

import kh.edu.istad.platform.iamapplicationservice.port.out.UserRepository;
import kh.edu.istad.platform.iamapplicationservice.query.user.UserFilterQuery;
import kh.edu.istad.platform.iamapplicationservice.result.common.PageResult;
import kh.edu.istad.platform.iamapplicationservice.result.user.UserSummary;
import kh.edu.istad.platform.iamapplicationservice.usecase.user.ListUsersUseCase;

import java.util.List;

public class ListUsersService implements ListUsersUseCase {

    private final UserRepository userRepository;

    public ListUsersService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public PageResult<UserSummary> execute(UserFilterQuery query) {
        if (query == null) {
            throw new IllegalArgumentException("User filter query is required");
        }

        List<UserSummary> content = userRepository
                .findAll(query.page(), query.size(), query.sort())
                .stream()
                .map(user -> new UserSummary(
                        user.uuid(),
                        user.username(),
                        user.email(),
                        user.name(),
                        user.enabled(),
                        user.accountNonLocked(),
                        user.createdAt()
                ))
                .toList();

        long totalElements = userRepository.count();

        return new PageResult<>(
                content,
                new PageResult.Page(
                        query.page(),
                        query.size(),
                        totalElements,
                        totalPages(totalElements, query.size())
                )
        );
    }

    private int totalPages(long totalElements, int size) {
        return (int) ((totalElements + size - 1) / size);
    }
}

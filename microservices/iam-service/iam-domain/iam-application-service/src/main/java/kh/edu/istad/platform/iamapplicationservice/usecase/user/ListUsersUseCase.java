package kh.edu.istad.platform.iamapplicationservice.usecase.user;

import kh.edu.istad.platform.iamapplicationservice.query.user.UserFilterQuery;
import kh.edu.istad.platform.iamapplicationservice.result.common.PageResult;
import kh.edu.istad.platform.iamapplicationservice.result.user.UserSummary;

public interface ListUsersUseCase {
    PageResult<UserSummary> execute(UserFilterQuery query);
}

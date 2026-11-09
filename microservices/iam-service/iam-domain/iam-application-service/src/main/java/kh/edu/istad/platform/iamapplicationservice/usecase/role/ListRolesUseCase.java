package kh.edu.istad.platform.iamapplicationservice.usecase.role;

import kh.edu.istad.platform.iamapplicationservice.query.role.RoleFilterQuery;
import kh.edu.istad.platform.iamapplicationservice.result.common.PageResult;
import kh.edu.istad.platform.iamapplicationservice.result.role.RoleSummary;

public interface ListRolesUseCase {
    PageResult<RoleSummary> execute(RoleFilterQuery query);
}

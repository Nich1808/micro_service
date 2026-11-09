package kh.edu.istad.platform.iamapplicationservice.usecase.permission;

import kh.edu.istad.platform.iamapplicationservice.query.permission.PermissionFilterQuery;
import kh.edu.istad.platform.iamapplicationservice.result.common.PageResult;
import kh.edu.istad.platform.iamapplicationservice.result.permission.PermissionResult;

public interface ListPermissionsUseCase {
    PageResult<PermissionResult> execute(PermissionFilterQuery query);
}

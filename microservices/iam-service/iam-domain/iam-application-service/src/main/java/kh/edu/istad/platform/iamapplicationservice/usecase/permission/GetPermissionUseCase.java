package kh.edu.istad.platform.iamapplicationservice.usecase.permission;

import kh.edu.istad.platform.iamapplicationservice.result.permission.PermissionResult;
import java.util.UUID;

public interface GetPermissionUseCase {
    PermissionResult execute(UUID permissionUuid);
}

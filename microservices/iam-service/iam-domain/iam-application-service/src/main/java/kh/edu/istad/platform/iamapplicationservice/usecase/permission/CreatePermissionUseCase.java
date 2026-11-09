package kh.edu.istad.platform.iamapplicationservice.usecase.permission;

import kh.edu.istad.platform.iamapplicationservice.command.permission.CreatePermissionCommand;
import kh.edu.istad.platform.iamapplicationservice.result.permission.PermissionResult;

public interface CreatePermissionUseCase {
    PermissionResult execute(CreatePermissionCommand command);
}

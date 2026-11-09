package kh.edu.istad.platform.iamapplicationservice.usecase.permission;

import kh.edu.istad.platform.iamapplicationservice.command.permission.UpdatePermissionCommand;
import kh.edu.istad.platform.iamapplicationservice.result.permission.PermissionResult;

public interface UpdatePermissionUseCase {
    PermissionResult execute(UpdatePermissionCommand command);
}

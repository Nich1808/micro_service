package kh.edu.istad.platform.iamapplicationservice.usecase.role;

import kh.edu.istad.platform.iamapplicationservice.command.role.UpdateRoleCommand;
import kh.edu.istad.platform.iamapplicationservice.result.role.RoleResult;

public interface UpdateRoleUseCase {
    RoleResult execute(UpdateRoleCommand command);
}

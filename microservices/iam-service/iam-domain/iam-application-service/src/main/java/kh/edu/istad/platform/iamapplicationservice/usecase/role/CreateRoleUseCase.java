package kh.edu.istad.platform.iamapplicationservice.usecase.role;

import kh.edu.istad.platform.iamapplicationservice.command.role.CreateRoleCommand;
import kh.edu.istad.platform.iamapplicationservice.result.role.RoleResult;

public interface CreateRoleUseCase {
    RoleResult execute(CreateRoleCommand command);
}

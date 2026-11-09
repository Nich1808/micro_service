package kh.edu.istad.platform.iamapplicationservice.usecase.user;

import kh.edu.istad.platform.iamapplicationservice.command.user.AssignRolesToUserCommand;
import kh.edu.istad.platform.iamapplicationservice.result.user.RoleAssignmentResult;
import java.util.List;

public interface AssignRolesToUserUseCase {
    List<RoleAssignmentResult> execute(AssignRolesToUserCommand command);
}

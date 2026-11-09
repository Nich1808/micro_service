package kh.edu.istad.platform.iamapplicationservice.usecase.role;

import kh.edu.istad.platform.iamapplicationservice.command.role.AssignPermissionsToRoleCommand;
import kh.edu.istad.platform.iamapplicationservice.result.role.PermissionAssignmentResult;
import java.util.List;

public interface AssignPermissionsToRoleUseCase {
    List<PermissionAssignmentResult> execute(AssignPermissionsToRoleCommand command);
}

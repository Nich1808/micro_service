package kh.edu.istad.platform.iamapplicationservice.usecase.user;

import kh.edu.istad.platform.iamapplicationservice.command.user.GrantPermissionsToUserCommand;
import kh.edu.istad.platform.iamapplicationservice.result.user.PermissionGrantResult;
import java.util.List;

public interface GrantPermissionsToUserUseCase {
    List<PermissionGrantResult> execute(GrantPermissionsToUserCommand command);
}

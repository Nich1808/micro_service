package kh.edu.istad.platform.iamapplicationservice.usecase.permission;

import java.util.UUID;

public interface ActivatePermissionUseCase {
    void execute(UUID permissionUuid);
}

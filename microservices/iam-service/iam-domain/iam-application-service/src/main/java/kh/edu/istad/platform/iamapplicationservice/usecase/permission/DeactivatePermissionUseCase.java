package kh.edu.istad.platform.iamapplicationservice.usecase.permission;

import java.util.UUID;

public interface DeactivatePermissionUseCase {
    void execute(UUID permissionUuid);
}

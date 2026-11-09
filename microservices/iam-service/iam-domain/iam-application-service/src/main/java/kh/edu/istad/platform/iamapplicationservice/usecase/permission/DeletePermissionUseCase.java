package kh.edu.istad.platform.iamapplicationservice.usecase.permission;

import java.util.UUID;

public interface DeletePermissionUseCase {
    void execute(UUID permissionUuid);
}

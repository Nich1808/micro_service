package kh.edu.istad.platform.iamapplicationservice.usecase.role;

import java.util.UUID;

public interface RevokePermissionFromRoleUseCase {
    void execute(UUID roleUuid, UUID permissionUuid, String reason);
}

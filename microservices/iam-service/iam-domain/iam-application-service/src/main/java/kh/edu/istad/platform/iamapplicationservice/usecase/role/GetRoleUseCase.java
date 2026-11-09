package kh.edu.istad.platform.iamapplicationservice.usecase.role;

import kh.edu.istad.platform.iamapplicationservice.result.role.RoleResult;
import java.util.UUID;

public interface GetRoleUseCase {
    RoleResult execute(UUID roleUuid);
}

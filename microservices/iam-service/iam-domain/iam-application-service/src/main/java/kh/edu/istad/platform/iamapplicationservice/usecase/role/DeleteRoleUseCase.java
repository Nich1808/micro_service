package kh.edu.istad.platform.iamapplicationservice.usecase.role;

import java.util.UUID;

public interface DeleteRoleUseCase {
    void execute(UUID roleUuid);
}

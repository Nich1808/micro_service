package kh.edu.istad.platform.iamapplicationservice.usecase.role;

import java.util.UUID;

public interface ActivateRoleUseCase {
    void execute(UUID roleUuid);
}

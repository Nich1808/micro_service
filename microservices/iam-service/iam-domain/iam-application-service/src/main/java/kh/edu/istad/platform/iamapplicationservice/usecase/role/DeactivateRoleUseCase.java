package kh.edu.istad.platform.iamapplicationservice.usecase.role;

import java.util.UUID;

public interface DeactivateRoleUseCase {
    void execute(UUID roleUuid);
}

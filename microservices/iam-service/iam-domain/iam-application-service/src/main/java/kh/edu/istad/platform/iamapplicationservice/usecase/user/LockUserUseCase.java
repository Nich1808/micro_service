package kh.edu.istad.platform.iamapplicationservice.usecase.user;

import java.util.UUID;

public interface LockUserUseCase {
    void execute(UUID userUuid);
}

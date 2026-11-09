package kh.edu.istad.platform.iamapplicationservice.usecase.user;

import java.util.UUID;

public interface UnlockUserUseCase {
    void execute(UUID userUuid);
}

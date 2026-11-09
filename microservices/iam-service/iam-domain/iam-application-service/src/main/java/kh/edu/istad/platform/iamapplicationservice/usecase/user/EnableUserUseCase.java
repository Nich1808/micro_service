package kh.edu.istad.platform.iamapplicationservice.usecase.user;

import java.util.UUID;

public interface EnableUserUseCase {
    void execute(UUID userUuid);
}

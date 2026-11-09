package kh.edu.istad.platform.iamapplicationservice.usecase.user;

import java.util.UUID;

public interface DeleteUserUseCase {
    void execute(UUID userUuid);
}

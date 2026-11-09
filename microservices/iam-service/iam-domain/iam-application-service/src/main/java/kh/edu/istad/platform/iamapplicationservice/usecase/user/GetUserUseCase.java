package kh.edu.istad.platform.iamapplicationservice.usecase.user;

import kh.edu.istad.platform.iamapplicationservice.result.user.UserResult;
import java.util.UUID;

public interface GetUserUseCase {
    UserResult execute(UUID userUuid);
}

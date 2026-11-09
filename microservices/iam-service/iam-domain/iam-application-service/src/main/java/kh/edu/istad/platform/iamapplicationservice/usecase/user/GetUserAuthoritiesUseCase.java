package kh.edu.istad.platform.iamapplicationservice.usecase.user;

import kh.edu.istad.platform.iamapplicationservice.result.user.UserAuthoritiesResult;
import java.util.UUID;

public interface GetUserAuthoritiesUseCase {
    UserAuthoritiesResult execute(UUID userUuid);
}

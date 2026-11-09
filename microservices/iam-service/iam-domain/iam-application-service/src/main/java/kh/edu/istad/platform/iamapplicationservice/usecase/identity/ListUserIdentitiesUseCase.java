package kh.edu.istad.platform.iamapplicationservice.usecase.identity;

import kh.edu.istad.platform.iamapplicationservice.result.identity.IdentityResult;
import java.util.List;
import java.util.UUID;

public interface ListUserIdentitiesUseCase {
    List<IdentityResult> execute(UUID userUuid);
}

package kh.edu.istad.platform.iamapplicationservice.usecase.identity;

import java.util.UUID;

public interface UnlinkIdentityUseCase {
    void execute(UUID userUuid, UUID identityUuid);
}

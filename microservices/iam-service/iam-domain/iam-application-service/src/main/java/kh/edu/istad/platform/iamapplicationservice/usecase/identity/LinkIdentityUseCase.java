package kh.edu.istad.platform.iamapplicationservice.usecase.identity;

import kh.edu.istad.platform.iamapplicationservice.command.identity.FederatedIdentityCommand;
import kh.edu.istad.platform.iamapplicationservice.result.identity.IdentityResult;
import java.util.UUID;

public interface LinkIdentityUseCase {
    IdentityResult execute(UUID userUuid, FederatedIdentityCommand command);
}

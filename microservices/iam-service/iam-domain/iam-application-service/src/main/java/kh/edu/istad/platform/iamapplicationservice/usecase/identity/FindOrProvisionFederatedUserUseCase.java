package kh.edu.istad.platform.iamapplicationservice.usecase.identity;

import kh.edu.istad.platform.iamapplicationservice.command.identity.FederatedIdentityCommand;
import kh.edu.istad.platform.iamapplicationservice.result.user.AuthUserResult;

public interface FindOrProvisionFederatedUserUseCase {
    AuthUserResult execute(FederatedIdentityCommand command);
}

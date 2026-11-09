package kh.edu.istad.platform.iamapplicationservice.service.user;

import kh.edu.istad.platform.iamapplicationservice.port.out.*;
import kh.edu.istad.platform.iamapplicationservice.usecase.user.DisableUserUseCase;

import java.time.OffsetDateTime;
import java.util.UUID;

public class DisableUserService implements DisableUserUseCase {

    private final UserRepository repository;
    private final CurrentActorProvider actorProvider;

    public DisableUserService(
            UserRepository repository,
            CurrentActorProvider actorProvider
    ) {
        this.repository = repository;
        this.actorProvider = actorProvider;
    }

    @Override
    public void execute(UUID userUuid) {
        var user = UserStateSupport.requireActiveUser(repository, userUuid);

        if (user.enabled()) {
            repository.save(UserStateSupport.changeStatus(
                    user, false, user.accountNonLocked(),
                    actorProvider.getCurrentActor(), OffsetDateTime.now()
            ));
        }
    }
}

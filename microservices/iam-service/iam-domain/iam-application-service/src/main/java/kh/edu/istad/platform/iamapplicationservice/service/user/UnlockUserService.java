package kh.edu.istad.platform.iamapplicationservice.service.user;

import kh.edu.istad.platform.iamapplicationservice.port.out.*;
import kh.edu.istad.platform.iamapplicationservice.usecase.user.UnlockUserUseCase;

import java.time.OffsetDateTime;
import java.util.UUID;

public class UnlockUserService implements UnlockUserUseCase {

    private final UserRepository repository;
    private final CurrentActorProvider actorProvider;

    public UnlockUserService(
            UserRepository repository,
            CurrentActorProvider actorProvider
    ) {
        this.repository = repository;
        this.actorProvider = actorProvider;
    }

    @Override
    public void execute(UUID userUuid) {
        var user = UserStateSupport.requireActiveUser(repository, userUuid);

        if (!user.accountNonLocked()) {
            repository.save(UserStateSupport.changeStatus(
                    user, user.enabled(), true,
                    actorProvider.getCurrentActor(), OffsetDateTime.now()
            ));
        }
    }
}

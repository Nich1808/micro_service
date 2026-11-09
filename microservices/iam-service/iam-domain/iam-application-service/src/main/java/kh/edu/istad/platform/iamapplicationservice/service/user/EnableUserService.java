package kh.edu.istad.platform.iamapplicationservice.service.user;

import kh.edu.istad.platform.iamapplicationservice.port.out.*;
import kh.edu.istad.platform.iamapplicationservice.usecase.user.EnableUserUseCase;

import java.time.OffsetDateTime;
import java.util.UUID;

public class EnableUserService implements EnableUserUseCase {

    private final UserRepository repository;
    private final CurrentActorProvider actorProvider;

    public EnableUserService(
            UserRepository repository,
            CurrentActorProvider actorProvider
    ) {
        this.repository = repository;
        this.actorProvider = actorProvider;
    }

    @Override
    public void execute(UUID userUuid) {
        var user = UserStateSupport.requireActiveUser(repository, userUuid);

        if (!user.enabled()) {
            repository.save(UserStateSupport.changeStatus(
                    user, true, user.accountNonLocked(),
                    actorProvider.getCurrentActor(), OffsetDateTime.now()
            ));
        }
    }
}

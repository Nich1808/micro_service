package kh.edu.istad.platform.iamapplicationservice.service.user;

import kh.edu.istad.platform.iamapplicationservice.port.out.*;
import kh.edu.istad.platform.iamapplicationservice.usecase.user.DeleteUserUseCase;

import java.time.OffsetDateTime;
import java.util.UUID;

public class DeleteUserService implements DeleteUserUseCase {

    private final UserRepository repository;
    private final CurrentActorProvider actorProvider;

    public DeleteUserService(
            UserRepository repository,
            CurrentActorProvider actorProvider
    ) {
        this.repository = repository;
        this.actorProvider = actorProvider;
    }

    @Override
    public void execute(UUID userUuid) {
        var user = UserStateSupport.requireActiveUser(repository, userUuid);

        repository.save(user.softDelete(
                actorProvider.getCurrentActor(),
                OffsetDateTime.now()
        ));
    }
}

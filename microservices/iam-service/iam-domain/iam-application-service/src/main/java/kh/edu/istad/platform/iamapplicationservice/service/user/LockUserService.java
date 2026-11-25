package kh.edu.istad.platform.iamapplicationservice.service.user;

import kh.edu.istad.platform.iamapplicationservice.port.out.*;
import kh.edu.istad.platform.iamapplicationservice.service.security.ActivitySupport;
import kh.edu.istad.platform.iamapplicationservice.usecase.user.LockUserUseCase;
import kh.edu.istad.platform.iamdomaincore.enums.ActivityType;

import java.time.OffsetDateTime;
import java.util.UUID;

public class LockUserService implements LockUserUseCase {

    private final UserRepository repository;
    private final CurrentActorProvider actorProvider;
    private final AccountActivityRepository accountActivityRepository;

    public LockUserService(
            UserRepository repository,
            CurrentActorProvider actorProvider,
            AccountActivityRepository accountActivityRepository
    ) {
        this.repository = repository;
        this.actorProvider = actorProvider;
        this.accountActivityRepository = accountActivityRepository;
    }

    @Override
    public void execute(UUID userUuid) {
        var user = UserStateSupport.requireActiveUser(repository, userUuid);

        if (user.accountNonLocked()) {
            String actor = actorProvider.getCurrentActor();

            repository.save(UserStateSupport.changeStatus(
                    user, user.enabled(), false,
                    actor, OffsetDateTime.now()
            ));

            ActivitySupport.record(
                    accountActivityRepository, user.id(),
                    ActivityType.ACCOUNT_LOCKED,
                    "Account locked by an administrator", actor
            );
        }
    }
}

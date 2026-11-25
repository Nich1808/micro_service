package kh.edu.istad.platform.iamapplicationservice.service.user;

import kh.edu.istad.platform.iamapplicationservice.port.out.*;
import kh.edu.istad.platform.iamapplicationservice.service.security.ActivitySupport;
import kh.edu.istad.platform.iamapplicationservice.usecase.user.UnlockUserUseCase;
import kh.edu.istad.platform.iamdomaincore.enums.ActivityType;

import java.time.OffsetDateTime;
import java.util.UUID;

public class UnlockUserService implements UnlockUserUseCase {

    private final UserRepository repository;
    private final CurrentActorProvider actorProvider;
    private final AccountActivityRepository accountActivityRepository;

    public UnlockUserService(
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

        if (!user.accountNonLocked()) {
            String actor = actorProvider.getCurrentActor();

            repository.save(UserStateSupport.changeStatus(
                    user, user.enabled(), true,
                    actor, OffsetDateTime.now()
            ));

            ActivitySupport.record(
                    accountActivityRepository, user.id(),
                    ActivityType.ACCOUNT_UNLOCKED,
                    "Account unlocked", actor
            );
        }
    }
}

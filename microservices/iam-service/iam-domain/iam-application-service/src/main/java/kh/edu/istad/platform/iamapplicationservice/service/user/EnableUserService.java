package kh.edu.istad.platform.iamapplicationservice.service.user;

import kh.edu.istad.platform.iamapplicationservice.port.out.*;
import kh.edu.istad.platform.iamapplicationservice.service.security.ActivitySupport;
import kh.edu.istad.platform.iamapplicationservice.usecase.user.EnableUserUseCase;
import kh.edu.istad.platform.iamdomaincore.enums.ActivityType;

import java.time.OffsetDateTime;
import java.util.UUID;

public class EnableUserService implements EnableUserUseCase {

    private final UserRepository repository;
    private final CurrentActorProvider actorProvider;
    private final AccountActivityRepository accountActivityRepository;

    public EnableUserService(
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

        if (!user.enabled()) {
            String actor = actorProvider.getCurrentActor();

            repository.save(UserStateSupport.changeStatus(
                    user, true, user.accountNonLocked(),
                    actor, OffsetDateTime.now()
            ));

            ActivitySupport.record(
                    accountActivityRepository, user.id(),
                    ActivityType.ACCOUNT_ENABLED,
                    "Account enabled", actor
            );
        }
    }
}

package kh.edu.istad.platform.iamapplicationservice.service.identity;

import kh.edu.istad.platform.iamapplicationservice.command.identity.FederatedIdentityCommand;
import kh.edu.istad.platform.iamapplicationservice.port.out.CurrentActorProvider;
import kh.edu.istad.platform.iamapplicationservice.port.out.IdentityRepository;
import kh.edu.istad.platform.iamapplicationservice.port.out.UserRepository;
import kh.edu.istad.platform.iamapplicationservice.result.identity.IdentityResult;
import kh.edu.istad.platform.iamapplicationservice.usecase.identity.LinkIdentityUseCase;
import kh.edu.istad.platform.iamdomaincore.model.Identity;
import kh.edu.istad.platform.iamdomaincore.model.User;

import java.time.OffsetDateTime;
import java.util.UUID;

public class LinkIdentityService implements LinkIdentityUseCase {

    private final UserRepository userRepository;
    private final IdentityRepository identityRepository;
    private final CurrentActorProvider currentActorProvider;

    public LinkIdentityService(
            UserRepository userRepository,
            IdentityRepository identityRepository,
            CurrentActorProvider currentActorProvider) {
        this.userRepository = userRepository;
        this.identityRepository = identityRepository;
        this.currentActorProvider = currentActorProvider;
    }

    @Override
    public IdentityResult execute(UUID userUuid, FederatedIdentityCommand command) {
        if (userUuid == null || command == null) {
            throw new IllegalArgumentException("User UUID and identity command are required");
        }

        User user = userRepository.findByUuid(userUuid)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (identityRepository.findByProviderAndProviderSubject(
                command.provider(), command.providerSubject()).isPresent()) {
            throw new IllegalStateException("Federated identity is already linked");
        }

        if (identityRepository.existsByUserIdAndProvider(user.id(), command.provider())) {
            throw new IllegalStateException("User already has an identity from this provider");
        }

        String actor = currentActorProvider.getCurrentActor();
        if (actor == null || actor.isBlank()) {
            throw new IllegalStateException("Current actor is required");
        }

        OffsetDateTime now = OffsetDateTime.now();

        Identity identity = new Identity(
                null, null, user.id(),
                command.provider(),
                command.providerSubject(),
                command.email(),
                command.emailVerified(),
                command.displayName(),
                command.username(),
                command.pictureUrl(),
                command.attributes(),
                now, null,
                false, null, null,
                now, actor, now, actor, 0
        );

        Identity saved = identityRepository.save(identity);

        return new IdentityResult(
                saved.uuid(),
                saved.provider(),
                saved.email(),
                saved.displayName(),
                saved.linkedAt()
        );
    }
}

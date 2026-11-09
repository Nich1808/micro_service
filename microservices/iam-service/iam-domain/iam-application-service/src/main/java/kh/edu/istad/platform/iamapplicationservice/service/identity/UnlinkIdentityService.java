package kh.edu.istad.platform.iamapplicationservice.service.identity;

import kh.edu.istad.platform.iamapplicationservice.port.out.CurrentActorProvider;
import kh.edu.istad.platform.iamapplicationservice.port.out.IdentityRepository;
import kh.edu.istad.platform.iamapplicationservice.port.out.UserRepository;
import kh.edu.istad.platform.iamapplicationservice.usecase.identity.UnlinkIdentityUseCase;
import kh.edu.istad.platform.iamdomaincore.model.Identity;
import kh.edu.istad.platform.iamdomaincore.model.User;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public class UnlinkIdentityService implements UnlinkIdentityUseCase {

    private final UserRepository userRepository;
    private final IdentityRepository identityRepository;
    private final CurrentActorProvider currentActorProvider;

    public UnlinkIdentityService(
            UserRepository userRepository,
            IdentityRepository identityRepository,
            CurrentActorProvider currentActorProvider) {
        this.userRepository = userRepository;
        this.identityRepository = identityRepository;
        this.currentActorProvider = currentActorProvider;
    }

    @Override
    public void execute(UUID userUuid, UUID identityUuid) {
        if (userUuid == null || identityUuid == null) {
            throw new IllegalArgumentException("User UUID and identity UUID are required");
        }

        User user = userRepository.findByUuid(userUuid)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Identity identity = identityRepository.findByUuid(identityUuid)
                .orElseThrow(() -> new IllegalArgumentException("Identity not found"));

        identity.validateUsable();

        if (!identity.userId().equals(user.id())) {
            throw new IllegalStateException("Identity does not belong to this user");
        }

        List<Identity> identities = identityRepository.findAllByUserId(user.id());

        boolean hasPassword = user.passwordHash() != null
                && !user.passwordHash().isBlank();

        if (identities.size() <= 1 && !hasPassword) {
            throw new IllegalStateException("Cannot unlink the user's last sign-in method");
        }

        String actor = currentActorProvider.getCurrentActor();
        if (actor == null || actor.isBlank()) {
            throw new IllegalStateException("Current actor is required");
        }

        identityRepository.save(identity.softDelete(actor, OffsetDateTime.now()));
    }
}

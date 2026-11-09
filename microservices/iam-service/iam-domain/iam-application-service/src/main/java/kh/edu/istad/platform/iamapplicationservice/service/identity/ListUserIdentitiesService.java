package kh.edu.istad.platform.iamapplicationservice.service.identity;

import kh.edu.istad.platform.iamapplicationservice.port.out.IdentityRepository;
import kh.edu.istad.platform.iamapplicationservice.port.out.UserRepository;
import kh.edu.istad.platform.iamapplicationservice.result.identity.IdentityResult;
import kh.edu.istad.platform.iamapplicationservice.usecase.identity.ListUserIdentitiesUseCase;
import kh.edu.istad.platform.iamdomaincore.model.Identity;

import java.util.List;
import java.util.UUID;

public class ListUserIdentitiesService implements ListUserIdentitiesUseCase {

    private final UserRepository userRepository;
    private final IdentityRepository identityRepository;

    public ListUserIdentitiesService(
            UserRepository userRepository,
            IdentityRepository identityRepository) {
        this.userRepository = userRepository;
        this.identityRepository = identityRepository;
    }

    @Override
    public List<IdentityResult> execute(UUID userUuid) {
        if (userUuid == null) {
            throw new IllegalArgumentException("User UUID is required");
        }

        var user = userRepository.findByUuid(userUuid)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        return identityRepository.findAllByUserId(user.id())
                .stream()
                .map(this::toResult)
                .toList();
    }

    private IdentityResult toResult(Identity identity) {
        return new IdentityResult(
                identity.uuid(),
                identity.provider(),
                identity.email(),
                identity.displayName(),
                identity.linkedAt()
        );
    }
}

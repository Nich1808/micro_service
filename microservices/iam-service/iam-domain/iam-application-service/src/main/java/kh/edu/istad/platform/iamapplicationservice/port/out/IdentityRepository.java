package kh.edu.istad.platform.iamapplicationservice.port.out;

import kh.edu.istad.platform.iamdomaincore.enums.IdentityProvider;
import kh.edu.istad.platform.iamdomaincore.model.Identity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IdentityRepository {

    Identity save(Identity identity);

    Optional<Identity> findByUuid(UUID uuid);

    Optional<Identity> findByProviderAndProviderSubject(
            IdentityProvider provider,
            String providerSubject
    );

    List<Identity> findAllByUserId(Long userId);

    boolean existsByUserIdAndProvider(
            Long userId,
            IdentityProvider provider
    );
}

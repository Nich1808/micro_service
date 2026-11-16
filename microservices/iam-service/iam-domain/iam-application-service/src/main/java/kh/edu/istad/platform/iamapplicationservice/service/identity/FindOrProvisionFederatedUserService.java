package kh.edu.istad.platform.iamapplicationservice.service.identity;

import kh.edu.istad.platform.iamapplicationservice.command.identity.FederatedIdentityCommand;
import kh.edu.istad.platform.iamapplicationservice.port.out.CurrentActorProvider;
import kh.edu.istad.platform.iamapplicationservice.port.out.IdentityRepository;
import kh.edu.istad.platform.iamapplicationservice.port.out.UserRepository;
import kh.edu.istad.platform.iamapplicationservice.result.user.AuthUserResult;
import kh.edu.istad.platform.iamapplicationservice.result.user.UserAuthoritiesResult;
import kh.edu.istad.platform.iamapplicationservice.usecase.identity.FindOrProvisionFederatedUserUseCase;
import kh.edu.istad.platform.iamapplicationservice.usecase.user.GetUserAuthoritiesUseCase;
import kh.edu.istad.platform.iamdomaincore.model.Identity;
import kh.edu.istad.platform.iamdomaincore.model.User;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

public class FindOrProvisionFederatedUserService
        implements FindOrProvisionFederatedUserUseCase {

    private final UserRepository userRepository;
    private final IdentityRepository identityRepository;
    private final GetUserAuthoritiesUseCase getUserAuthoritiesUseCase;
    private final CurrentActorProvider actorProvider;

    public FindOrProvisionFederatedUserService(
            UserRepository userRepository,
            IdentityRepository identityRepository,
            GetUserAuthoritiesUseCase getUserAuthoritiesUseCase,
            CurrentActorProvider actorProvider
    ) {
        this.userRepository = userRepository;
        this.identityRepository = identityRepository;
        this.getUserAuthoritiesUseCase = getUserAuthoritiesUseCase;
        this.actorProvider = actorProvider;
    }

    @Override
    public AuthUserResult execute(FederatedIdentityCommand command) {
        if (command == null) {
            throw new IllegalArgumentException("Federated identity command is required");
        }

        if (command.provider() == null) {
            throw new IllegalArgumentException("Identity provider is required");
        }

        if (command.providerSubject() == null || command.providerSubject().isBlank()) {
            throw new IllegalArgumentException("Provider subject is required");
        }

        String actor = actorProvider.getCurrentActor();

        if (actor == null || actor.isBlank()) {
            throw new IllegalStateException("Current actor is required");
        }

        OffsetDateTime now = OffsetDateTime.now();
        User user = resolveUser(command, actor, now);

        return toAuthUser(user);
    }

    private User resolveUser(
            FederatedIdentityCommand command,
            String actor,
            OffsetDateTime now
    ) {
        Optional<Identity> linked = identityRepository
                .findByProviderAndProviderSubject(
                        command.provider(),
                        command.providerSubject()
                );

        if (linked.isPresent()) {
            Identity identity = linked.get();
            identity.validateUsable();

            identityRepository.save(identity.recordLogin(now));

            return userRepository.findById(identity.userId())
                    .filter(found -> !found.deleted())
                    .orElseThrow(() -> new IllegalStateException(
                            "User for the federated identity was not found"
                    ));
        }

        User user = findExistingUser(command);

        if (user != null) {
            if (identityRepository.existsByUserIdAndProvider(
                    user.id(), command.provider()
            )) {
                throw new IllegalStateException(
                        "User already has an identity from this provider"
                );
            }
        } else {
            user = userRepository.save(provisionUser(command, actor, now));
        }

        identityRepository.save(new Identity(
                null, null,
                user.id(),
                command.provider(),
                command.providerSubject(),
                command.email(),
                command.emailVerified(),
                command.displayName(),
                command.username(),
                command.pictureUrl(),
                command.attributes(),
                now, now,
                false, null, null,
                now, actor,
                now, actor,
                0
        ));

        return user;
    }

    private User findExistingUser(FederatedIdentityCommand command) {
        if (command.email() == null || command.email().isBlank()) {
            return null;
        }

        Optional<User> existing = userRepository.findByEmail(
                command.email().trim().toLowerCase(Locale.ROOT)
        );

        if (existing.isEmpty()) {
            return null;
        }

        User user = existing.get();

        if (user.deleted()) {
            throw new IllegalStateException("User account is not usable");
        }

        return user;
    }

    private User provisionUser(
            FederatedIdentityCommand command,
            String actor,
            OffsetDateTime now
    ) {
        String email = resolveEmail(command);
        String displayName = resolveDisplayName(command, email);

        String[] parts = displayName.split("\\s+", 2);
        String givenName = parts[0];
        String familyName = parts.length > 1 ? parts[1] : parts[0];

        return new User(
                null, null,
                uniqueUsername(command, email), email, command.emailVerified(), null,
                givenName, familyName, null, command.username(),
                null, null, null, command.pictureUrl(), null, null,
                "en",
                null, false,
                null, null, null, null, null,
                true, true, true, true,
                0, null, null,
                false, null, null,
                now, actor,
                now, actor,
                0
        );
    }

    private String resolveEmail(FederatedIdentityCommand command) {
        if (command.email() != null && !command.email().isBlank()) {
            return command.email().trim().toLowerCase(Locale.ROOT);
        }

        String subject = command.providerSubject()
                .replaceAll("[^a-zA-Z0-9]", "");

        return command.provider().name().toLowerCase(Locale.ROOT)
                + "-" + subject + "@users.local";
    }

    private String resolveDisplayName(
            FederatedIdentityCommand command,
            String email
    ) {
        if (command.displayName() != null && !command.displayName().isBlank()) {
            return command.displayName().trim();
        }

        if (command.username() != null && !command.username().isBlank()) {
            return command.username().trim();
        }

        return email.substring(0, email.indexOf('@'));
    }

    private String uniqueUsername(
            FederatedIdentityCommand command,
            String email
    ) {
        String base = command.username() != null && !command.username().isBlank()
                ? command.username().trim()
                : email.substring(0, email.indexOf('@'));

        base = base.replaceAll("[^A-Za-z0-9._-]", "");

        if (base.isBlank()) {
            base = "user";
        }

        if (base.length() > 40) {
            base = base.substring(0, 40);
        }

        String candidate = base;
        int attempts = 0;

        while (userRepository.existsByUsername(candidate) && attempts < 10) {
            candidate = base + UUID.randomUUID().toString().substring(0, 4);
            attempts++;
        }

        if (userRepository.existsByUsername(candidate)) {
            throw new IllegalStateException("Unable to allocate a unique username");
        }

        return candidate;
    }

    private AuthUserResult toAuthUser(User user) {
        UserAuthoritiesResult authorities =
                getUserAuthoritiesUseCase.execute(user.uuid());

        LinkedHashSet<String> values = new LinkedHashSet<>();

        authorities.roles().forEach(role ->
                values.add("ROLE_" + role.name()));

        values.addAll(authorities.effectivePermissions());

        return new AuthUserResult(
                user.uuid(),
                user.username(),
                user.passwordHash(),
                user.enabled() && !user.deleted(),
                user.accountNonExpired(),
                user.accountNonLocked(),
                user.credentialsNonExpired(),
                new ArrayList<>(values)
        );
    }
}

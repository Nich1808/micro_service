package kh.edu.istad.platform.iamapplicationservice.service.user;

import kh.edu.istad.platform.iamapplicationservice.port.out.UserRepository;
import kh.edu.istad.platform.iamapplicationservice.result.user.AuthUserResult;
import kh.edu.istad.platform.iamapplicationservice.result.user.UserAuthoritiesResult;
import kh.edu.istad.platform.iamapplicationservice.usecase.user.GetUserAuthoritiesUseCase;
import kh.edu.istad.platform.iamapplicationservice.usecase.user.LoadUserForAuthenticationUseCase;
import kh.edu.istad.platform.iamdomaincore.model.User;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.NoSuchElementException;

public class LoadUserForAuthenticationService
        implements LoadUserForAuthenticationUseCase {

    private final UserRepository userRepository;
    private final GetUserAuthoritiesUseCase getUserAuthoritiesUseCase;

    public LoadUserForAuthenticationService(
            UserRepository userRepository,
            GetUserAuthoritiesUseCase getUserAuthoritiesUseCase
    ) {
        this.userRepository = userRepository;
        this.getUserAuthoritiesUseCase = getUserAuthoritiesUseCase;
    }

    @Override
    public AuthUserResult execute(String usernameOrEmail) {
        if (usernameOrEmail == null || usernameOrEmail.isBlank()) {
            throw new IllegalArgumentException(
                    "Username or email is required"
            );
        }

        User user = userRepository.findByUsernameOrEmail(
                usernameOrEmail.trim()
        ).orElseThrow(() ->
                new NoSuchElementException("User not found"));

        UserAuthoritiesResult userAuthorities =
                getUserAuthoritiesUseCase.execute(user.uuid());

        LinkedHashSet<String> authorities = new LinkedHashSet<>();

        userAuthorities.roles().forEach(role ->
                authorities.add("ROLE_" + role.name()));

        authorities.addAll(userAuthorities.effectivePermissions());

        return new AuthUserResult(
                user.uuid(),
                user.username(),
                user.passwordHash(),
                user.enabled() && !user.deleted(),
                user.accountNonExpired(),
                user.accountNonLocked(),
                user.credentialsNonExpired(),
                new ArrayList<>(authorities)
        );
    }
}

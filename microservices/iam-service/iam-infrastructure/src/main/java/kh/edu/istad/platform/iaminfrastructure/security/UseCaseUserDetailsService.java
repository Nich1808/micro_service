package kh.edu.istad.platform.iaminfrastructure.security;

import kh.edu.istad.platform.iamapplicationservice.result.user.AuthUserResult;
import kh.edu.istad.platform.iamapplicationservice.usecase.user.LoadUserForAuthenticationUseCase;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.NoSuchElementException;

/**
 * Adapter from Spring Security to {@link LoadUserForAuthenticationUseCase}.
 * All identity resolution and authority computation stays in the application layer.
 *
 * <p>The standard {@link User} principal is used on purpose: the authorization server
 * persists the authenticated principal, and only well-known Spring Security types are
 * permitted by its JSON type validator.</p>
 */
public class UseCaseUserDetailsService implements UserDetailsService {

    private final LoadUserForAuthenticationUseCase loadUserForAuthenticationUseCase;

    public UseCaseUserDetailsService(
            LoadUserForAuthenticationUseCase loadUserForAuthenticationUseCase
    ) {
        this.loadUserForAuthenticationUseCase = loadUserForAuthenticationUseCase;
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        try {
            AuthUserResult result = loadUserForAuthenticationUseCase.execute(username);

            return new User(
                    result.username(),
                    result.passwordHash() == null ? "" : result.passwordHash(),
                    result.enabled(),
                    result.accountNonExpired(),
                    result.accountNonLocked(),
                    result.credentialsNonExpired(),
                    result.authorities().stream()
                            .map(SimpleGrantedAuthority::new)
                            .toList()
            );
        } catch (NoSuchElementException | IllegalArgumentException ex) {
            throw new UsernameNotFoundException("User not found", ex);
        }
    }
}

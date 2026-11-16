package kh.edu.istad.platform.iamweb.web;

import kh.edu.istad.platform.iamapplicationservice.port.out.UserRepository;
import kh.edu.istad.platform.iamdomaincore.model.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.NoSuchElementException;

/**
 * Resolves the signed-in user through the application port. Works for both password
 * logins and social logins, because both expose the IAM username as the principal name.
 */
@Component
public class CurrentUser {

    private final UserRepository userRepository;

    public CurrentUser(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Authentication authentication() {
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new NoSuchElementException("Authentication is required");
        }

        return authentication;
    }

    public String name() {
        return authentication().getName();
    }

    public User require() {
        return userRepository.findByUsernameOrEmail(name())
                .filter(user -> !user.deleted())
                .orElseThrow(() -> new NoSuchElementException("User not found"));
    }
}

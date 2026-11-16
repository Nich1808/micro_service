package kh.edu.istad.platform.iamapplicationservice.port.out;

import kh.edu.istad.platform.iamdomaincore.model.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository {

    User save(User user);

    Optional<User> findById(Long id);

    Optional<User> findByUuid(UUID uuid);

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    Optional<User> findByUsernameOrEmail(String usernameOrEmail);

    List<User> findAll(int page, int size, String sort);

    long count();

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);
}

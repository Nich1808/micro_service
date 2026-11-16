package kh.edu.istad.platform.iamapplicationservice.port.out;

import kh.edu.istad.platform.iamdomaincore.model.Role;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RoleRepository {

    Role save(Role role);

    Optional<Role> findById(Long id);

    Optional<Role> findByUuid(UUID uuid);

    Optional<Role> findByName(String name);

    List<Role> findAll(int page, int size, String sort);

    long count();

    boolean existsByName(String name);
}

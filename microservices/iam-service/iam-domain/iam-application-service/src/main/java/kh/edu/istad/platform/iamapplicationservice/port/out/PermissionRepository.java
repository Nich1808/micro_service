package kh.edu.istad.platform.iamapplicationservice.port.out;

import kh.edu.istad.platform.iamdomaincore.model.Permission;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PermissionRepository {

    Permission save(Permission permission);

    Optional<Permission> findById(Long id);

    Optional<Permission> findByUuid(UUID uuid);

    Optional<Permission> findByName(String name);

    List<Permission> findAll(int page, int size, String sort);

    long count();

    boolean existsByName(String name);
}

package kh.edu.istad.platform.iamapplicationservice.service.user;

import kh.edu.istad.platform.iamapplicationservice.port.out.*;
import kh.edu.istad.platform.iamapplicationservice.result.user.UserAuthoritiesResult;
import kh.edu.istad.platform.iamapplicationservice.usecase.user.GetUserAuthoritiesUseCase;
import kh.edu.istad.platform.iamdomaincore.enums.PermissionEffect;
import kh.edu.istad.platform.iamdomaincore.model.Permission;
import kh.edu.istad.platform.iamdomaincore.model.Role;
import kh.edu.istad.platform.iamdomaincore.model.User;

import java.time.OffsetDateTime;
import java.util.*;

public class GetUserAuthoritiesService implements GetUserAuthoritiesUseCase {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final UserRoleRepository userRoleRepository;
    private final UserPermissionRepository userPermissionRepository;
    private final RolePermissionRepository rolePermissionRepository;

    public GetUserAuthoritiesService(
            UserRepository userRepository,
            RoleRepository roleRepository,
            PermissionRepository permissionRepository,
            UserRoleRepository userRoleRepository,
            UserPermissionRepository userPermissionRepository,
            RolePermissionRepository rolePermissionRepository
    ) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
        this.userRoleRepository = userRoleRepository;
        this.userPermissionRepository = userPermissionRepository;
        this.rolePermissionRepository = rolePermissionRepository;
    }

    @Override
    public UserAuthoritiesResult execute(UUID userUuid) {
        User user = userRepository.findByUuid(userUuid)
                .orElseThrow(() -> new NoSuchElementException("User not found"));

        OffsetDateTime now = OffsetDateTime.now();

        Map<Long, Role> activeRoles = new LinkedHashMap<>();
        List<UserAuthoritiesResult.RoleAuthority> roleResults = new ArrayList<>();
        Set<String> effective = new TreeSet<>();

        userRoleRepository.findAllByUserId(user.id()).stream()
                .filter(assignment -> assignment.isActiveAt(now))
                .forEach(assignment -> roleRepository.findById(assignment.roleId())
                        .filter(Role::isAssignable)
                        .ifPresent(role -> {
                            activeRoles.put(role.id(), role);
                            roleResults.add(new UserAuthoritiesResult.RoleAuthority(
                                    role.uuid(), role.name(), assignment.expiresAt()
                            ));
                        }));

        for (Role role : activeRoles.values()) {
            rolePermissionRepository.findAllByRoleId(role.id()).stream()
                    .filter(assignment -> assignment.isActiveAt(now))
                    .forEach(assignment -> permissionRepository
                            .findById(assignment.permissionId())
                            .filter(Permission::isAssignable)
                            .ifPresent(permission -> effective.add(permission.name())));
        }

        Map<String, PermissionEffect> directEffects = new LinkedHashMap<>();
        List<UserAuthoritiesResult.DirectPermission> directResults = new ArrayList<>();

        userPermissionRepository.findAllByUserId(user.id()).stream()
                .filter(assignment -> assignment.isActiveAt(now))
                .forEach(assignment -> permissionRepository
                        .findById(assignment.permissionId())
                        .filter(Permission::isAssignable)
                        .ifPresent(permission -> {
                            directEffects.merge(
                                    permission.name(),
                                    assignment.effect(),
                                    (oldEffect, newEffect) ->
                                            oldEffect == PermissionEffect.DENY
                                                    || newEffect == PermissionEffect.DENY
                                                    ? PermissionEffect.DENY
                                                    : PermissionEffect.GRANT
                            );
                        }));

        directEffects.forEach((name, effect) -> {
            directResults.add(
                    new UserAuthoritiesResult.DirectPermission(name, effect)
            );

            if (effect == PermissionEffect.GRANT) {
                effective.add(name);
            } else {
                effective.remove(name);
            }
        });

        return new UserAuthoritiesResult(
                roleResults,
                directResults,
                new ArrayList<>(effective)
        );
    }
}

package kh.edu.istad.platform.iampersistence;

import kh.edu.istad.platform.iamapplicationservice.command.permission.CreatePermissionCommand;
import kh.edu.istad.platform.iamapplicationservice.command.role.CreateRoleCommand;
import kh.edu.istad.platform.iamapplicationservice.command.user.GrantPermissionsToUserCommand;
import kh.edu.istad.platform.iamapplicationservice.port.out.*;
import kh.edu.istad.platform.iamapplicationservice.result.role.RoleResult;
import kh.edu.istad.platform.iamapplicationservice.service.permission.CreatePermissionService;
import kh.edu.istad.platform.iamapplicationservice.service.role.CreateRoleService;
import kh.edu.istad.platform.iamapplicationservice.service.user.GrantPermissionsToUserService;
import kh.edu.istad.platform.iamdomaincore.enums.PermissionEffect;
import kh.edu.istad.platform.iamdomaincore.model.*;

import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class IamUseCaseServicesTest {

    private static final String ACTOR = "tester";
    private static final OffsetDateTime NOW = OffsetDateTime.now();

    private final CurrentActorProvider actorProvider = () -> ACTOR;

    // ------------------------------------------------------------------
    // Permission creation
    // ------------------------------------------------------------------

    @Test
    void createPermissionDerivesResourceActionName() {
        FakePermissionRepository permissions = new FakePermissionRepository();

        new CreatePermissionService(permissions, actorProvider)
                .execute(new CreatePermissionCommand("Course", "PUBLISH", "Publish a course"));

        Permission saved = permissions.all().getFirst();

        assertEquals("course:publish", saved.name());
        assertEquals("course", saved.resource());
        assertEquals("publish", saved.action());
        assertFalse(saved.system());
        assertTrue(saved.active());
        assertFalse(saved.deleted());
    }

    @Test
    void createPermissionRejectsDuplicates() {
        FakePermissionRepository permissions = new FakePermissionRepository();
        CreatePermissionService service =
                new CreatePermissionService(permissions, actorProvider);

        service.execute(new CreatePermissionCommand("course", "publish", "Publish"));

        assertThrows(
                IllegalArgumentException.class,
                () -> service.execute(
                        new CreatePermissionCommand("course", "publish", "Again")
                )
        );
    }

    // ------------------------------------------------------------------
    // Role creation with permission assignment
    // ------------------------------------------------------------------

    @Test
    void createRoleAssignsRequestedPermissions() {
        FakePermissionRepository permissions = new FakePermissionRepository();
        Permission read = permission(10L, "user", "read");
        permissions.seed(read);

        FakeRoleRepository roles = new FakeRoleRepository();
        FakeRolePermissionRepository rolePermissions = new FakeRolePermissionRepository();

        RoleResult result = new CreateRoleService(
                roles, permissions, rolePermissions, actorProvider
        ).execute(new CreateRoleCommand(
                "content_manager", "Manages content", List.of(read.uuid())
        ));

        assertEquals("CONTENT_MANAGER", result.name());
        assertTrue(result.active());
        assertFalse(result.system());

        assertEquals(1, rolePermissions.all().size());
        assertEquals(read.id(), rolePermissions.all().getFirst().permissionId());
    }

    @Test
    void createRoleRejectsInactivePermission() {
        FakePermissionRepository permissions = new FakePermissionRepository();
        Permission inactive = permission(
                null, 10L, "user", "read", false, false
        );
        permissions.seed(inactive);

        FakeRoleRepository roles = new FakeRoleRepository();
        FakeRolePermissionRepository rolePermissions = new FakeRolePermissionRepository();

        CreateRoleService service = new CreateRoleService(
                roles, permissions, rolePermissions, actorProvider
        );

        assertThrows(
                IllegalStateException.class,
                () -> service.execute(new CreateRoleCommand(
                        "auditor", "Auditor", List.of(inactive.uuid())
                ))
        );
    }

    // ------------------------------------------------------------------
    // Direct permission grants
    // ------------------------------------------------------------------

    @Test
    void grantPermissionReplacesOppositeEffect() {
        FakeUserRepository users = new FakeUserRepository();
        User user = user(1L, "sokdara");
        users.seed(user);

        FakePermissionRepository permissions = new FakePermissionRepository();
        Permission report = permission(20L, "report", "read");
        permissions.seed(report);

        FakeUserPermissionRepository grants = new FakeUserPermissionRepository();

        GrantPermissionsToUserService service = new GrantPermissionsToUserService(
                users, permissions, grants, actorProvider, null
        );

        service.execute(new GrantPermissionsToUserCommand(
                user.uuid(), List.of(report.uuid()),
                PermissionEffect.GRANT, null, "Needs to read reports"
        ));

        service.execute(new GrantPermissionsToUserCommand(
                user.uuid(), List.of(report.uuid()),
                PermissionEffect.DENY, null, "Reports revoked"
        ));

        assertEquals(2, grants.all().size());
        assertNotNull(grants.all().getFirst().revokedAt(),
                "The previous GRANT must be revoked");

        UserPermission active = grants
                .findActiveByUserIdAndPermissionId(user.id(), report.id())
                .orElseThrow();

        assertEquals(PermissionEffect.DENY, active.effect());
    }

    @Test
    void grantPermissionIsIdempotentForSameEffect() {
        FakeUserRepository users = new FakeUserRepository();
        User user = user(2L, "dara");
        users.seed(user);

        FakePermissionRepository permissions = new FakePermissionRepository();
        Permission report = permission(21L, "report", "read");
        permissions.seed(report);

        FakeUserPermissionRepository grants = new FakeUserPermissionRepository();

        GrantPermissionsToUserService service = new GrantPermissionsToUserService(
                users, permissions, grants, actorProvider, null
        );

        GrantPermissionsToUserCommand command = new GrantPermissionsToUserCommand(
                user.uuid(), List.of(report.uuid()),
                PermissionEffect.GRANT, null, "First"
        );

        assertFalse(service.execute(command).isEmpty());
        assertTrue(service.execute(command).isEmpty(),
                "An identical active grant must not be assigned twice");

        assertEquals(1, grants.all().size());
    }

    // ------------------------------------------------------------------
    // Fixtures
    // ------------------------------------------------------------------

    private static User user(Long id, String username) {
        return new User(
                id, UUID.randomUUID(),
                username, username + "@istad.co", true, "hash",
                "Sok", "Dara", null, null, "male", null,
                null, null, null, null, "en",
                null, false,
                null, null, null, null, null,
                true, true, true, true,
                0, null, null,
                false, null, null,
                NOW, ACTOR, NOW, ACTOR,
                0
        );
    }

    private static Permission permission(Long id, String resource, String action) {
        return permission(null, id, resource, action, true, true);
    }

    private static Permission permission(
            UUID uuid,
            Long id,
            String resource,
            String action,
            boolean system,
            boolean active
    ) {
        return new Permission(
                id, uuid, null, resource, action, "Fixture permission",
                system, active, false,
                null, null,
                NOW, ACTOR, NOW, ACTOR,
                0
        );
    }

    // ------------------------------------------------------------------
    // In-memory ports
    // ------------------------------------------------------------------

    private static final class FakeUserRepository implements UserRepository {

        private final Map<UUID, User> users = new LinkedHashMap<>();

        void seed(User user) {
            users.put(user.uuid(), user);
        }

        @Override
        public User save(User user) {
            users.put(user.uuid(), user);
            return user;
        }

        @Override
        public Optional<User> findById(Long id) {
            return users.values().stream()
                    .filter(user -> Objects.equals(user.id(), id))
                    .findFirst();
        }

        @Override
        public Optional<User> findByUuid(UUID uuid) {
            return Optional.ofNullable(users.get(uuid));
        }

        @Override
        public Optional<User> findByUsername(String username) {
            return users.values().stream()
                    .filter(user -> user.username().equals(username))
                    .findFirst();
        }

        @Override
        public Optional<User> findByEmail(String email) {
            return users.values().stream()
                    .filter(user -> user.email().equals(email))
                    .findFirst();
        }

        @Override
        public Optional<User> findByUsernameOrEmail(String usernameOrEmail) {
            return users.values().stream()
                    .filter(user -> user.username().equals(usernameOrEmail)
                            || user.email().equals(usernameOrEmail))
                    .findFirst();
        }

        @Override
        public List<User> findAll(int page, int size, String sort) {
            return new ArrayList<>(users.values());
        }

        @Override
        public long count() {
            return users.size();
        }

        @Override
        public boolean existsByUsername(String username) {
            return findByUsername(username).isPresent();
        }

        @Override
        public boolean existsByEmail(String email) {
            return findByEmail(email).isPresent();
        }
    }

    private static final class FakePermissionRepository implements PermissionRepository {

        private final List<Permission> permissions = new ArrayList<>();

        void seed(Permission permission) {
            permissions.add(permission);
        }

        List<Permission> all() {
            return permissions;
        }

        @Override
        public Permission save(Permission permission) {
            if (permission.id() == null) {
                Permission persisted = new Permission(
                        100L + permissions.size(),
                        permission.uuid(),
                        permission.name(),
                        permission.resource(),
                        permission.action(),
                        permission.description(),
                        permission.system(),
                        permission.active(),
                        permission.deleted(),
                        permission.deletedAt(),
                        permission.deletedBy(),
                        permission.createdAt(),
                        permission.createdBy(),
                        permission.updatedAt(),
                        permission.updatedBy(),
                        permission.version()
                );
                permissions.add(persisted);
                return persisted;
            }

            permissions.removeIf(existing -> existing.uuid().equals(permission.uuid()));
            permissions.add(permission);
            return permission;
        }

        Optional<Permission> byId(Long id) {
            return permissions.stream()
                    .filter(permission -> Objects.equals(permission.id(), id))
                    .findFirst();
        }

        @Override
        public Optional<Permission> findById(Long id) {
            return byId(id);
        }

        @Override
        public Optional<Permission> findByUuid(UUID uuid) {
            return permissions.stream()
                    .filter(permission -> permission.uuid().equals(uuid))
                    .findFirst();
        }

        @Override
        public Optional<Permission> findByName(String name) {
            return permissions.stream()
                    .filter(permission -> permission.name().equals(name))
                    .findFirst();
        }

        @Override
        public List<Permission> findAll(int page, int size, String sort) {
            return permissions;
        }

        @Override
        public long count() {
            return permissions.size();
        }

        @Override
        public boolean existsByName(String name) {
            return findByName(name).isPresent();
        }
    }

    private static final class FakeRoleRepository implements RoleRepository {

        private final Map<UUID, Role> roles = new LinkedHashMap<>();

        @Override
        public Role save(Role role) {
            Role persisted = role.id() != null
                    ? role
                    : new Role(
                            1L, role.uuid(), role.name(), role.description(),
                            role.system(), role.active(), role.deleted(),
                            role.deletedAt(), role.deletedBy(),
                            role.createdAt(), role.createdBy(),
                            role.updatedAt(), role.updatedBy(),
                            role.version()
                    );
            roles.put(persisted.uuid(), persisted);
            return persisted;
        }

        @Override
        public Optional<Role> findById(Long id) {
            return roles.values().stream()
                    .filter(role -> Objects.equals(role.id(), id))
                    .findFirst();
        }

        @Override
        public Optional<Role> findByUuid(UUID uuid) {
            return Optional.ofNullable(roles.get(uuid));
        }

        @Override
        public Optional<Role> findByName(String name) {
            return roles.values().stream()
                    .filter(role -> role.name().equals(name))
                    .findFirst();
        }

        @Override
        public List<Role> findAll(int page, int size, String sort) {
            return new ArrayList<>(roles.values());
        }

        @Override
        public long count() {
            return roles.size();
        }

        @Override
        public boolean existsByName(String name) {
            return findByName(name).isPresent();
        }
    }

    private static final class FakeRolePermissionRepository
            implements RolePermissionRepository {

        private final List<RolePermission> assignments = new ArrayList<>();

        List<RolePermission> all() {
            return assignments;
        }

        @Override
        public RolePermission save(RolePermission rolePermission) {
            if (rolePermission.id() == null) {
                RolePermission persisted = new RolePermission(
                        1L,
                        rolePermission.roleId(),
                        rolePermission.permissionId(),
                        rolePermission.assignedAt(),
                        rolePermission.assignedBy(),
                        rolePermission.expiresAt(),
                        rolePermission.revokedAt(),
                        rolePermission.revokedBy(),
                        rolePermission.reason()
                );
                assignments.add(persisted);
                return persisted;
            }

            assignments.removeIf(existing -> existing.id().equals(rolePermission.id()));
            assignments.add(rolePermission);
            return rolePermission;
        }

        @Override
        public List<RolePermission> findAllByRoleId(Long roleId) {
            return assignments.stream()
                    .filter(assignment -> assignment.roleId().equals(roleId))
                    .toList();
        }

        @Override
        public Optional<RolePermission> findActiveByRoleIdAndPermissionId(
                Long roleId,
                Long permissionId
        ) {
            return assignments.stream()
                    .filter(assignment -> assignment.roleId().equals(roleId))
                    .filter(assignment -> assignment.permissionId().equals(permissionId))
                    .filter(assignment -> assignment.revokedAt() == null)
                    .findFirst();
        }
    }

    private static final class FakeUserPermissionRepository
            implements UserPermissionRepository {

        private final List<UserPermission> grants = new ArrayList<>();

        List<UserPermission> all() {
            return grants;
        }

        @Override
        public UserPermission save(UserPermission userPermission) {
            if (userPermission.id() == null) {
                UserPermission persisted = new UserPermission(
                        1L,
                        userPermission.userId(),
                        userPermission.permissionId(),
                        userPermission.effect(),
                        userPermission.assignedAt(),
                        userPermission.assignedBy(),
                        userPermission.expiresAt(),
                        userPermission.revokedAt(),
                        userPermission.revokedBy(),
                        userPermission.reason()
                );
                grants.add(persisted);
                return persisted;
            }

            grants.removeIf(existing -> existing.id().equals(userPermission.id()));
            grants.add(userPermission);
            return userPermission;
        }

        @Override
        public List<UserPermission> findAllByUserId(Long userId) {
            return grants.stream()
                    .filter(grant -> grant.userId().equals(userId))
                    .toList();
        }

        @Override
        public Optional<UserPermission> findActiveByUserIdAndPermissionId(
                Long userId,
                Long permissionId
        ) {
            return grants.stream()
                    .filter(grant -> grant.userId().equals(userId))
                    .filter(grant -> grant.permissionId().equals(permissionId))
                    .filter(grant -> grant.revokedAt() == null)
                    .findFirst();
        }
    }
}

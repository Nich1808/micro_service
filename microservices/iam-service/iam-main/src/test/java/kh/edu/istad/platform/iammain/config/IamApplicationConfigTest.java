package kh.edu.istad.platform.iammain.config;

import kh.edu.istad.platform.iamapplicationservice.port.out.AccountActivityRepository;
import kh.edu.istad.platform.iamapplicationservice.port.out.AdminStatisticsQuery;
import kh.edu.istad.platform.iamapplicationservice.port.out.ClientAdminRepository;
import kh.edu.istad.platform.iamapplicationservice.port.out.AuthorizedApplicationQuery;
import kh.edu.istad.platform.iamapplicationservice.port.out.ClientRegistryQuery;
import kh.edu.istad.platform.iamapplicationservice.port.out.CurrentActorProvider;
import kh.edu.istad.platform.iamapplicationservice.port.out.IdentityRepository;
import kh.edu.istad.platform.iamapplicationservice.port.out.LoginAttemptRepository;
import kh.edu.istad.platform.iamapplicationservice.port.out.PasswordHasher;
import kh.edu.istad.platform.iamapplicationservice.port.out.PermissionRepository;
import kh.edu.istad.platform.iamapplicationservice.port.out.RolePermissionRepository;
import kh.edu.istad.platform.iamapplicationservice.port.out.RoleRepository;
import kh.edu.istad.platform.iamapplicationservice.port.out.UserPermissionRepository;
import kh.edu.istad.platform.iamapplicationservice.port.out.UserRepository;
import kh.edu.istad.platform.iamapplicationservice.port.out.UserRoleRepository;
import kh.edu.istad.platform.iamapplicationservice.port.out.SystemSettingsRepository;
import kh.edu.istad.platform.iamapplicationservice.port.out.UserSessionRepository;
import kh.edu.istad.platform.iamapplicationservice.usecase.admin.AdminUseCases;
import kh.edu.istad.platform.iamapplicationservice.usecase.identity.FindOrProvisionFederatedUserUseCase;
import kh.edu.istad.platform.iamapplicationservice.usecase.identity.LinkIdentityUseCase;
import kh.edu.istad.platform.iamapplicationservice.usecase.identity.ListUserIdentitiesUseCase;
import kh.edu.istad.platform.iamapplicationservice.usecase.identity.UnlinkIdentityUseCase;
import kh.edu.istad.platform.iamapplicationservice.usecase.permission.ActivatePermissionUseCase;
import kh.edu.istad.platform.iamapplicationservice.usecase.permission.CreatePermissionUseCase;
import kh.edu.istad.platform.iamapplicationservice.usecase.permission.DeactivatePermissionUseCase;
import kh.edu.istad.platform.iamapplicationservice.usecase.permission.DeletePermissionUseCase;
import kh.edu.istad.platform.iamapplicationservice.usecase.permission.GetPermissionUseCase;
import kh.edu.istad.platform.iamapplicationservice.usecase.permission.ListPermissionsUseCase;
import kh.edu.istad.platform.iamapplicationservice.usecase.permission.UpdatePermissionUseCase;
import kh.edu.istad.platform.iamapplicationservice.usecase.role.ActivateRoleUseCase;
import kh.edu.istad.platform.iamapplicationservice.usecase.role.AssignPermissionsToRoleUseCase;
import kh.edu.istad.platform.iamapplicationservice.usecase.role.CreateRoleUseCase;
import kh.edu.istad.platform.iamapplicationservice.usecase.role.DeactivateRoleUseCase;
import kh.edu.istad.platform.iamapplicationservice.usecase.role.DeleteRoleUseCase;
import kh.edu.istad.platform.iamapplicationservice.usecase.role.GetRoleUseCase;
import kh.edu.istad.platform.iamapplicationservice.usecase.role.ListRolesUseCase;
import kh.edu.istad.platform.iamapplicationservice.usecase.role.RevokePermissionFromRoleUseCase;
import kh.edu.istad.platform.iamapplicationservice.usecase.role.UpdateRoleUseCase;
import kh.edu.istad.platform.iamapplicationservice.usecase.security.ListAccountActivityUseCase;
import kh.edu.istad.platform.iamapplicationservice.usecase.security.ListLoginHistoryUseCase;
import kh.edu.istad.platform.iamapplicationservice.usecase.session.ListUserSessionsUseCase;
import kh.edu.istad.platform.iamapplicationservice.usecase.session.TerminateAllSessionsUseCase;
import kh.edu.istad.platform.iamapplicationservice.usecase.session.TerminateSessionUseCase;
import kh.edu.istad.platform.iamapplicationservice.usecase.user.AssignRolesToUserUseCase;
import kh.edu.istad.platform.iamapplicationservice.usecase.user.ChangePasswordUseCase;
import kh.edu.istad.platform.iamapplicationservice.usecase.user.CreateUserUseCase;
import kh.edu.istad.platform.iamapplicationservice.usecase.user.DeactivateAccountUseCase;
import kh.edu.istad.platform.iamapplicationservice.usecase.user.DeleteUserUseCase;
import kh.edu.istad.platform.iamapplicationservice.usecase.user.DisableUserUseCase;
import kh.edu.istad.platform.iamapplicationservice.usecase.user.EnableUserUseCase;
import kh.edu.istad.platform.iamapplicationservice.usecase.user.GetUserAuthoritiesUseCase;
import kh.edu.istad.platform.iamapplicationservice.usecase.user.GetUserUseCase;
import kh.edu.istad.platform.iamapplicationservice.usecase.user.GrantPermissionsToUserUseCase;
import kh.edu.istad.platform.iamapplicationservice.usecase.user.ListUsersUseCase;
import kh.edu.istad.platform.iamapplicationservice.usecase.user.LoadUserForAuthenticationUseCase;
import kh.edu.istad.platform.iamapplicationservice.usecase.user.LockUserUseCase;
import kh.edu.istad.platform.iamapplicationservice.usecase.user.RecordLoginAttemptUseCase;
import kh.edu.istad.platform.iamapplicationservice.usecase.user.RegisterUserUseCase;
import kh.edu.istad.platform.iamapplicationservice.usecase.user.ResetUserPasswordUseCase;
import kh.edu.istad.platform.iamapplicationservice.usecase.user.RevokePermissionFromUserUseCase;
import kh.edu.istad.platform.iamapplicationservice.usecase.user.RevokeRoleFromUserUseCase;
import kh.edu.istad.platform.iamapplicationservice.usecase.user.UnlockUserUseCase;
import kh.edu.istad.platform.iamapplicationservice.usecase.user.UpdateUserProfileUseCase;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Proves the application graph is complete: every use case declared in the assignment
 * can be resolved from the wiring configuration with only the output ports present.
 * This catches a missing or mistyped dependency without needing a database.
 */
class IamApplicationConfigTest {

    private static final List<Class<?>> USE_CASES = List.of(
            // user
            RegisterUserUseCase.class,
            CreateUserUseCase.class,
            GetUserUseCase.class,
            ListUsersUseCase.class,
            UpdateUserProfileUseCase.class,
            ChangePasswordUseCase.class,
            ResetUserPasswordUseCase.class,
            EnableUserUseCase.class,
            DisableUserUseCase.class,
            LockUserUseCase.class,
            UnlockUserUseCase.class,
            DeleteUserUseCase.class,
            AssignRolesToUserUseCase.class,
            RevokeRoleFromUserUseCase.class,
            GrantPermissionsToUserUseCase.class,
            RevokePermissionFromUserUseCase.class,
            GetUserAuthoritiesUseCase.class,
            LoadUserForAuthenticationUseCase.class,
            RecordLoginAttemptUseCase.class,
            // role
            CreateRoleUseCase.class,
            GetRoleUseCase.class,
            ListRolesUseCase.class,
            UpdateRoleUseCase.class,
            ActivateRoleUseCase.class,
            DeactivateRoleUseCase.class,
            DeleteRoleUseCase.class,
            AssignPermissionsToRoleUseCase.class,
            RevokePermissionFromRoleUseCase.class,
            // permission
            CreatePermissionUseCase.class,
            GetPermissionUseCase.class,
            ListPermissionsUseCase.class,
            UpdatePermissionUseCase.class,
            ActivatePermissionUseCase.class,
            DeactivatePermissionUseCase.class,
            DeletePermissionUseCase.class,
            // identity
            FindOrProvisionFederatedUserUseCase.class,
            LinkIdentityUseCase.class,
            ListUserIdentitiesUseCase.class,
            UnlinkIdentityUseCase.class,
            // account security
            ListUserSessionsUseCase.class,
            TerminateSessionUseCase.class,
            TerminateAllSessionsUseCase.class,
            ListLoginHistoryUseCase.class,
            ListAccountActivityUseCase.class,
            DeactivateAccountUseCase.class,
            // administration
            AdminUseCases.GetAdminOverview.class,
            AdminUseCases.ListAuditLogs.class,
            AdminUseCases.ListClients.class,
            AdminUseCases.GetClient.class,
            AdminUseCases.CreateClient.class,
            AdminUseCases.UpdateClient.class,
            AdminUseCases.DeleteClient.class,
            AdminUseCases.GetSystemSettings.class,
            AdminUseCases.UpdateSystemSettings.class
    );

    private static AnnotationConfigApplicationContext context;

    @BeforeAll
    static void start() {
        context = new AnnotationConfigApplicationContext();
        context.register(IamApplicationConfig.class, FakePorts.class);
        context.refresh();
    }

    @AfterAll
    static void stop() {
        context.close();
    }

    @Test
    void everyUseCaseFromTheSpecificationIsWired() {
        for (Class<?> useCase : USE_CASES) {
            assertNotNull(context.getBean(useCase),
                    "Use case is not wired: " + useCase.getSimpleName());
        }
    }

    @Test
    void everyUseCaseHasExactlyOneBeanDefinition() {
        for (Class<?> useCase : USE_CASES) {
            assertEquals(1, context.getBeanNamesForType(useCase).length,
                    "Expected exactly one bean for " + useCase.getSimpleName());
        }
    }

    /**
     * The application layer depends only on output ports; mocks stand in for the
     * adapters so the graph can be validated in isolation.
     */
    @Configuration
    static class FakePorts {

        @Bean
        UserRepository userRepository() {
            return stub(UserRepository.class);
        }

        @Bean
        RoleRepository roleRepository() {
            return stub(RoleRepository.class);
        }

        @Bean
        PermissionRepository permissionRepository() {
            return stub(PermissionRepository.class);
        }

        @Bean
        UserRoleRepository userRoleRepository() {
            return stub(UserRoleRepository.class);
        }

        @Bean
        RolePermissionRepository rolePermissionRepository() {
            return stub(RolePermissionRepository.class);
        }

        @Bean
        UserPermissionRepository userPermissionRepository() {
            return stub(UserPermissionRepository.class);
        }

        @Bean
        IdentityRepository identityRepository() {
            return stub(IdentityRepository.class);
        }

        @Bean
        PasswordHasher passwordHasher() {
            return stub(PasswordHasher.class);
        }

        @Bean
        CurrentActorProvider currentActorProvider() {
            return stub(CurrentActorProvider.class);
        }

        @Bean
        UserSessionRepository userSessionRepository() {
            return stub(UserSessionRepository.class);
        }

        @Bean
        LoginAttemptRepository loginAttemptRepository() {
            return stub(LoginAttemptRepository.class);
        }

        @Bean
        AccountActivityRepository accountActivityRepository() {
            return stub(AccountActivityRepository.class);
        }

        @Bean
        AdminStatisticsQuery adminStatisticsQuery() {
            return stub(AdminStatisticsQuery.class);
        }

        @Bean
        ClientAdminRepository clientAdminRepository() {
            return stub(ClientAdminRepository.class);
        }

        @Bean
        SystemSettingsRepository systemSettingsRepository() {
            return stub(SystemSettingsRepository.class);
        }

        @Bean
        ClientRegistryQuery clientRegistryQuery() {
            return stub(ClientRegistryQuery.class);
        }

        @Bean
        AuthorizedApplicationQuery authorizedApplicationQuery() {
            return stub(AuthorizedApplicationQuery.class);
        }
    }

    /**
     * Dependency-free stand-in for an output port. Spring only needs the bean to exist
     * while the wiring is validated, so defaults are enough. Avoids Mockito, whose mock
     * maker is not reliable on every JDK.
     */
    @SuppressWarnings("unchecked")
    private static <T> T stub(Class<T> type) {
        return (T) java.lang.reflect.Proxy.newProxyInstance(
                type.getClassLoader(),
                new Class<?>[]{type},
                (proxy, method, args) -> {
                    switch (method.getName()) {
                        case "toString":
                            return type.getSimpleName() + "Stub";
                        case "hashCode":
                            return System.identityHashCode(proxy);
                        case "equals":
                            return proxy == (args == null ? null : args[0]);
                        default:
                            break;
                    }

                    Class<?> returnType = method.getReturnType();

                    if (returnType == java.util.Optional.class) {
                        return java.util.Optional.empty();
                    }

                    if (returnType == java.util.List.class
                            || returnType == java.util.Set.class) {
                        return java.util.List.of();
                    }

                    if (returnType == boolean.class) {
                        return false;
                    }

                    if (returnType == long.class) {
                        return 0L;
                    }

                    if (returnType == int.class) {
                        return 0;
                    }

                    return null;
                });
    }
}

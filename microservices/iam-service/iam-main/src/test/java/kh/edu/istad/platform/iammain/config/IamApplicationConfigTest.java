package kh.edu.istad.platform.iammain.config;

import kh.edu.istad.platform.iamapplicationservice.port.out.AuthorizedApplicationQuery;
import kh.edu.istad.platform.iamapplicationservice.port.out.ClientRegistryQuery;
import kh.edu.istad.platform.iamapplicationservice.port.out.CurrentActorProvider;
import kh.edu.istad.platform.iamapplicationservice.port.out.IdentityRepository;
import kh.edu.istad.platform.iamapplicationservice.port.out.PasswordHasher;
import kh.edu.istad.platform.iamapplicationservice.port.out.PermissionRepository;
import kh.edu.istad.platform.iamapplicationservice.port.out.RolePermissionRepository;
import kh.edu.istad.platform.iamapplicationservice.port.out.RoleRepository;
import kh.edu.istad.platform.iamapplicationservice.port.out.UserPermissionRepository;
import kh.edu.istad.platform.iamapplicationservice.port.out.UserRepository;
import kh.edu.istad.platform.iamapplicationservice.port.out.UserRoleRepository;
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
import kh.edu.istad.platform.iamapplicationservice.usecase.user.AssignRolesToUserUseCase;
import kh.edu.istad.platform.iamapplicationservice.usecase.user.ChangePasswordUseCase;
import kh.edu.istad.platform.iamapplicationservice.usecase.user.CreateUserUseCase;
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
import static org.mockito.Mockito.mock;

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
            UnlinkIdentityUseCase.class
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
            return mock(UserRepository.class);
        }

        @Bean
        RoleRepository roleRepository() {
            return mock(RoleRepository.class);
        }

        @Bean
        PermissionRepository permissionRepository() {
            return mock(PermissionRepository.class);
        }

        @Bean
        UserRoleRepository userRoleRepository() {
            return mock(UserRoleRepository.class);
        }

        @Bean
        RolePermissionRepository rolePermissionRepository() {
            return mock(RolePermissionRepository.class);
        }

        @Bean
        UserPermissionRepository userPermissionRepository() {
            return mock(UserPermissionRepository.class);
        }

        @Bean
        IdentityRepository identityRepository() {
            return mock(IdentityRepository.class);
        }

        @Bean
        PasswordHasher passwordHasher() {
            return mock(PasswordHasher.class);
        }

        @Bean
        CurrentActorProvider currentActorProvider() {
            return mock(CurrentActorProvider.class);
        }

        @Bean
        ClientRegistryQuery clientRegistryQuery() {
            return mock(ClientRegistryQuery.class);
        }

        @Bean
        AuthorizedApplicationQuery authorizedApplicationQuery() {
            return mock(AuthorizedApplicationQuery.class);
        }
    }
}

package kh.edu.istad.platform.iammain.config;

import kh.edu.istad.platform.iamapplicationservice.port.out.*;
import kh.edu.istad.platform.iamapplicationservice.service.identity.*;
import kh.edu.istad.platform.iamapplicationservice.service.permission.*;
import kh.edu.istad.platform.iamapplicationservice.service.role.*;
import kh.edu.istad.platform.iamapplicationservice.service.user.*;
import kh.edu.istad.platform.iamapplicationservice.usecase.identity.*;
import kh.edu.istad.platform.iamapplicationservice.usecase.permission.*;
import kh.edu.istad.platform.iamapplicationservice.usecase.role.*;
import kh.edu.istad.platform.iamapplicationservice.usecase.user.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class IamApplicationConfig {

    // ------------------------------------------------------------------
    // User use cases
    // ------------------------------------------------------------------

    @Bean
    public RegisterUserUseCase registerUserUseCase(
            UserRepository userRepository,
            PasswordHasher passwordHasher,
            CurrentActorProvider currentActorProvider
    ) {
        return new RegisterUserService(
                userRepository,
                passwordHasher,
                currentActorProvider
        );
    }

    @Bean
    public CreateUserUseCase createUserUseCase(
            UserRepository userRepository,
            PasswordHasher passwordHasher,
            CurrentActorProvider currentActorProvider,
            RoleRepository roleRepository,
            UserRoleRepository userRoleRepository
    ) {
        return new CreateUserService(
                userRepository,
                passwordHasher,
                currentActorProvider,
                roleRepository,
                userRoleRepository
        );
    }

    @Bean
    public GetUserUseCase getUserUseCase(UserRepository userRepository) {
        return new GetUserService(userRepository);
    }

    @Bean
    public ListUsersUseCase listUsersUseCase(UserRepository userRepository) {
        return new ListUsersService(userRepository);
    }

    @Bean
    public UpdateUserProfileUseCase updateUserProfileUseCase(
            UserRepository userRepository,
            CurrentActorProvider currentActorProvider
    ) {
        return new UpdateUserProfileService(
                userRepository,
                currentActorProvider
        );
    }

    @Bean
    public ChangePasswordUseCase changePasswordUseCase(
            UserRepository userRepository,
            PasswordHasher passwordHasher,
            CurrentActorProvider currentActorProvider
    ) {
        return new ChangePasswordService(
                userRepository,
                passwordHasher,
                currentActorProvider
        );
    }

    @Bean
    public ResetUserPasswordUseCase resetUserPasswordUseCase(
            UserRepository userRepository,
            PasswordHasher passwordHasher,
            CurrentActorProvider currentActorProvider
    ) {
        return new ResetUserPasswordService(
                userRepository,
                passwordHasher,
                currentActorProvider
        );
    }

    @Bean
    public EnableUserUseCase enableUserUseCase(
            UserRepository userRepository,
            CurrentActorProvider currentActorProvider
    ) {
        return new EnableUserService(userRepository, currentActorProvider);
    }

    @Bean
    public DisableUserUseCase disableUserUseCase(
            UserRepository userRepository,
            CurrentActorProvider currentActorProvider
    ) {
        return new DisableUserService(userRepository, currentActorProvider);
    }

    @Bean
    public LockUserUseCase lockUserUseCase(
            UserRepository userRepository,
            CurrentActorProvider currentActorProvider
    ) {
        return new LockUserService(userRepository, currentActorProvider);
    }

    @Bean
    public UnlockUserUseCase unlockUserUseCase(
            UserRepository userRepository,
            CurrentActorProvider currentActorProvider
    ) {
        return new UnlockUserService(userRepository, currentActorProvider);
    }

    @Bean
    public DeleteUserUseCase deleteUserUseCase(
            UserRepository userRepository,
            CurrentActorProvider currentActorProvider
    ) {
        return new DeleteUserService(userRepository, currentActorProvider);
    }

    @Bean
    public GetUserAuthoritiesUseCase getUserAuthoritiesUseCase(
            UserRepository userRepository,
            RoleRepository roleRepository,
            PermissionRepository permissionRepository,
            UserRoleRepository userRoleRepository,
            UserPermissionRepository userPermissionRepository,
            RolePermissionRepository rolePermissionRepository
    ) {
        return new GetUserAuthoritiesService(
                userRepository,
                roleRepository,
                permissionRepository,
                userRoleRepository,
                userPermissionRepository,
                rolePermissionRepository
        );
    }

    @Bean
    public LoadUserForAuthenticationUseCase loadUserForAuthenticationUseCase(
            UserRepository userRepository,
            GetUserAuthoritiesUseCase getUserAuthoritiesUseCase
    ) {
        return new LoadUserForAuthenticationService(
                userRepository,
                getUserAuthoritiesUseCase
        );
    }

    @Bean
    public RecordLoginAttemptUseCase recordLoginAttemptUseCase(
            UserRepository userRepository,
            CurrentActorProvider currentActorProvider
    ) {
        return new RecordLoginAttemptService(
                userRepository,
                currentActorProvider
        );
    }

    @Bean
    public AssignRolesToUserUseCase assignRolesToUserUseCase(
            UserRepository userRepository,
            RoleRepository roleRepository,
            UserRoleRepository userRoleRepository,
            CurrentActorProvider currentActorProvider
    ) {
        return new AssignRolesToUserService(
                userRepository,
                roleRepository,
                userRoleRepository,
                currentActorProvider
        );
    }

    @Bean
    public RevokeRoleFromUserUseCase revokeRoleFromUserUseCase(
            UserRepository userRepository,
            RoleRepository roleRepository,
            UserRoleRepository userRoleRepository,
            CurrentActorProvider currentActorProvider
    ) {
        return new RevokeRoleFromUserService(
                userRepository,
                roleRepository,
                userRoleRepository,
                currentActorProvider
        );
    }

    @Bean
    public GrantPermissionsToUserUseCase grantPermissionsToUserUseCase(
            UserRepository userRepository,
            PermissionRepository permissionRepository,
            UserPermissionRepository userPermissionRepository,
            CurrentActorProvider currentActorProvider
    ) {
        return new GrantPermissionsToUserService(
                userRepository,
                permissionRepository,
                userPermissionRepository,
                currentActorProvider
        );
    }

    @Bean
    public RevokePermissionFromUserUseCase revokePermissionFromUserUseCase(
            UserRepository userRepository,
            PermissionRepository permissionRepository,
            UserPermissionRepository userPermissionRepository,
            CurrentActorProvider currentActorProvider
    ) {
        return new RevokePermissionFromUserService(
                userRepository,
                permissionRepository,
                userPermissionRepository,
                currentActorProvider
        );
    }

    // ------------------------------------------------------------------
    // Role use cases
    // ------------------------------------------------------------------

    @Bean
    public CreateRoleUseCase createRoleUseCase(
            RoleRepository roleRepository,
            PermissionRepository permissionRepository,
            RolePermissionRepository rolePermissionRepository,
            CurrentActorProvider currentActorProvider
    ) {
        return new CreateRoleService(
                roleRepository,
                permissionRepository,
                rolePermissionRepository,
                currentActorProvider
        );
    }

    @Bean
    public GetRoleUseCase getRoleUseCase(RoleRepository roleRepository) {
        return new GetRoleService(roleRepository);
    }

    @Bean
    public ListRolesUseCase listRolesUseCase(RoleRepository roleRepository) {
        return new ListRolesService(roleRepository);
    }

    @Bean
    public UpdateRoleUseCase updateRoleUseCase(
            RoleRepository roleRepository,
            CurrentActorProvider currentActorProvider
    ) {
        return new UpdateRoleService(roleRepository, currentActorProvider);
    }

    @Bean
    public ActivateRoleUseCase activateRoleUseCase(
            RoleRepository roleRepository,
            CurrentActorProvider currentActorProvider
    ) {
        return new ActivateRoleService(roleRepository, currentActorProvider);
    }

    @Bean
    public DeactivateRoleUseCase deactivateRoleUseCase(
            RoleRepository roleRepository,
            CurrentActorProvider currentActorProvider
    ) {
        return new DeactivateRoleService(roleRepository, currentActorProvider);
    }

    @Bean
    public DeleteRoleUseCase deleteRoleUseCase(
            RoleRepository roleRepository,
            CurrentActorProvider currentActorProvider
    ) {
        return new DeleteRoleService(roleRepository, currentActorProvider);
    }

    @Bean
    public AssignPermissionsToRoleUseCase assignPermissionsToRoleUseCase(
            RoleRepository roleRepository,
            PermissionRepository permissionRepository,
            RolePermissionRepository rolePermissionRepository,
            CurrentActorProvider currentActorProvider
    ) {
        return new AssignPermissionsToRoleService(
                roleRepository,
                permissionRepository,
                rolePermissionRepository,
                currentActorProvider
        );
    }

    @Bean
    public RevokePermissionFromRoleUseCase revokePermissionFromRoleUseCase(
            RoleRepository roleRepository,
            PermissionRepository permissionRepository,
            RolePermissionRepository rolePermissionRepository,
            CurrentActorProvider currentActorProvider
    ) {
        return new RevokePermissionFromRoleService(
                roleRepository,
                permissionRepository,
                rolePermissionRepository,
                currentActorProvider
        );
    }

    // ------------------------------------------------------------------
    // Permission use cases
    // ------------------------------------------------------------------

    @Bean
    public CreatePermissionUseCase createPermissionUseCase(
            PermissionRepository permissionRepository,
            CurrentActorProvider currentActorProvider
    ) {
        return new CreatePermissionService(
                permissionRepository,
                currentActorProvider
        );
    }

    @Bean
    public GetPermissionUseCase getPermissionUseCase(
            PermissionRepository permissionRepository
    ) {
        return new GetPermissionService(permissionRepository);
    }

    @Bean
    public ListPermissionsUseCase listPermissionsUseCase(
            PermissionRepository permissionRepository
    ) {
        return new ListPermissionsService(permissionRepository);
    }

    @Bean
    public UpdatePermissionUseCase updatePermissionUseCase(
            PermissionRepository permissionRepository,
            CurrentActorProvider currentActorProvider
    ) {
        return new UpdatePermissionService(
                permissionRepository,
                currentActorProvider
        );
    }

    @Bean
    public ActivatePermissionUseCase activatePermissionUseCase(
            PermissionRepository permissionRepository,
            CurrentActorProvider currentActorProvider
    ) {
        return new ActivatePermissionService(
                permissionRepository,
                currentActorProvider
        );
    }

    @Bean
    public DeactivatePermissionUseCase deactivatePermissionUseCase(
            PermissionRepository permissionRepository,
            CurrentActorProvider currentActorProvider
    ) {
        return new DeactivatePermissionService(
                permissionRepository,
                currentActorProvider
        );
    }

    @Bean
    public DeletePermissionUseCase deletePermissionUseCase(
            PermissionRepository permissionRepository,
            CurrentActorProvider currentActorProvider
    ) {
        return new DeletePermissionService(
                permissionRepository,
                currentActorProvider
        );
    }

    // ------------------------------------------------------------------
    // Identity use cases
    // ------------------------------------------------------------------

    @Bean
    public LinkIdentityUseCase linkIdentityUseCase(
            UserRepository userRepository,
            IdentityRepository identityRepository,
            CurrentActorProvider currentActorProvider
    ) {
        return new LinkIdentityService(
                userRepository,
                identityRepository,
                currentActorProvider
        );
    }

    @Bean
    public ListUserIdentitiesUseCase listUserIdentitiesUseCase(
            UserRepository userRepository,
            IdentityRepository identityRepository
    ) {
        return new ListUserIdentitiesService(
                userRepository,
                identityRepository
        );
    }

    @Bean
    public UnlinkIdentityUseCase unlinkIdentityUseCase(
            UserRepository userRepository,
            IdentityRepository identityRepository,
            CurrentActorProvider currentActorProvider
    ) {
        return new UnlinkIdentityService(
                userRepository,
                identityRepository,
                currentActorProvider
        );
    }

    @Bean
    public FindOrProvisionFederatedUserUseCase findOrProvisionFederatedUserUseCase(
            UserRepository userRepository,
            IdentityRepository identityRepository,
            GetUserAuthoritiesUseCase getUserAuthoritiesUseCase,
            CurrentActorProvider currentActorProvider
    ) {
        return new FindOrProvisionFederatedUserService(
                userRepository,
                identityRepository,
                getUserAuthoritiesUseCase,
                currentActorProvider
        );
    }
}

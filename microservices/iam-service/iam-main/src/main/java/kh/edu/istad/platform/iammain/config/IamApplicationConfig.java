package kh.edu.istad.platform.iammain.config;

import kh.edu.istad.platform.iamapplicationservice.port.out.*;
import kh.edu.istad.platform.iamapplicationservice.service.user.*;
import kh.edu.istad.platform.iamapplicationservice.usecase.user.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class IamApplicationConfig {

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
}

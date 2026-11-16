package kh.edu.istad.platform.iamrestapi.mapper;

import kh.edu.istad.platform.iamapplicationservice.command.permission.CreatePermissionCommand;
import kh.edu.istad.platform.iamapplicationservice.command.permission.UpdatePermissionCommand;
import kh.edu.istad.platform.iamapplicationservice.command.role.AssignPermissionsToRoleCommand;
import kh.edu.istad.platform.iamapplicationservice.command.role.CreateRoleCommand;
import kh.edu.istad.platform.iamapplicationservice.command.role.UpdateRoleCommand;
import kh.edu.istad.platform.iamapplicationservice.command.user.AssignRolesToUserCommand;
import kh.edu.istad.platform.iamapplicationservice.command.user.CreateUserCommand;
import kh.edu.istad.platform.iamapplicationservice.command.user.GrantPermissionsToUserCommand;
import kh.edu.istad.platform.iamapplicationservice.command.user.RegisterUserCommand;
import kh.edu.istad.platform.iamapplicationservice.command.user.UpdateUserProfileCommand;
import kh.edu.istad.platform.iamapplicationservice.result.common.PageResult;
import kh.edu.istad.platform.iamapplicationservice.result.identity.IdentityResult;
import kh.edu.istad.platform.iamapplicationservice.result.permission.PermissionResult;
import kh.edu.istad.platform.iamapplicationservice.result.role.PermissionAssignmentResult;
import kh.edu.istad.platform.iamapplicationservice.result.role.RoleResult;
import kh.edu.istad.platform.iamapplicationservice.result.role.RoleSummary;
import kh.edu.istad.platform.iamapplicationservice.result.user.PermissionGrantResult;
import kh.edu.istad.platform.iamapplicationservice.result.user.RoleAssignmentResult;
import kh.edu.istad.platform.iamapplicationservice.result.user.UserAuthoritiesResult;
import kh.edu.istad.platform.iamapplicationservice.result.user.UserResult;
import kh.edu.istad.platform.iamapplicationservice.result.user.UserSummary;
import kh.edu.istad.platform.iamrestapi.dto.request.AddressRequest;
import kh.edu.istad.platform.iamrestapi.dto.request.AssignPermissionsToRoleRequest;
import kh.edu.istad.platform.iamrestapi.dto.request.AssignRolesToUserRequest;
import kh.edu.istad.platform.iamrestapi.dto.request.CreatePermissionRequest;
import kh.edu.istad.platform.iamrestapi.dto.request.CreateRoleRequest;
import kh.edu.istad.platform.iamrestapi.dto.request.CreateUserRequest;
import kh.edu.istad.platform.iamrestapi.dto.request.GrantPermissionsToUserRequest;
import kh.edu.istad.platform.iamrestapi.dto.request.RegisterUserRequest;
import kh.edu.istad.platform.iamrestapi.dto.request.UpdatePermissionRequest;
import kh.edu.istad.platform.iamrestapi.dto.request.UpdateRoleRequest;
import kh.edu.istad.platform.iamrestapi.dto.request.UpdateUserProfileRequest;
import kh.edu.istad.platform.iamrestapi.dto.response.IdentityResponse;
import kh.edu.istad.platform.iamrestapi.dto.response.PageResponse;
import kh.edu.istad.platform.iamrestapi.dto.response.PermissionAssignmentResponse;
import kh.edu.istad.platform.iamrestapi.dto.response.PermissionGrantResponse;
import kh.edu.istad.platform.iamrestapi.dto.response.PermissionResponse;
import kh.edu.istad.platform.iamrestapi.dto.response.RoleAssignmentResponse;
import kh.edu.istad.platform.iamrestapi.dto.response.RoleResponse;
import kh.edu.istad.platform.iamrestapi.dto.response.RoleSummaryResponse;
import kh.edu.istad.platform.iamrestapi.dto.response.UserAuthoritiesResponse;
import kh.edu.istad.platform.iamrestapi.dto.response.UserResponse;
import kh.edu.istad.platform.iamrestapi.dto.response.UserSummaryResponse;

import java.util.List;
import java.util.UUID;
import java.util.function.Function;

/**
 * Stateless translation between the HTTP contract and application contracts.
 * Controllers never build application commands inline.
 */
public final class ApiMapper {

    private ApiMapper() {
    }

    // ------------------------------------------------------------------
    // Requests -> commands
    // ------------------------------------------------------------------

    public static RegisterUserCommand toCommand(RegisterUserRequest request) {
        return new RegisterUserCommand(
                request.username(),
                request.email(),
                request.password(),
                request.givenName(),
                request.familyName(),
                request.locale()
        );
    }

    public static CreateUserCommand toCommand(CreateUserRequest request) {
        AddressRequest address = request.address();

        return new CreateUserCommand(
                request.username(),
                request.email(),
                request.password(),
                request.givenName(),
                request.familyName(),
                request.gender(),
                request.birthdate(),
                request.phoneNumber(),
                request.zoneinfo(),
                request.locale(),
                address == null ? null : address.streetAddress(),
                address == null ? null : address.locality(),
                address == null ? null : address.region(),
                address == null ? null : address.postalCode(),
                address == null ? null : address.country(),
                request.roleUuids()
        );
    }

    public static UpdateUserProfileCommand toCommand(
            UUID userUuid,
            UpdateUserProfileRequest request
    ) {
        return new UpdateUserProfileCommand(
                userUuid,
                request.givenName(),
                request.familyName(),
                request.middleName(),
                request.nickname(),
                request.gender(),
                request.birthdate(),
                request.profileUrl(),
                request.pictureUrl(),
                request.websiteUrl(),
                request.zoneinfo(),
                request.locale(),
                request.phoneNumber(),
                request.streetAddress(),
                request.locality(),
                request.region(),
                request.postalCode(),
                request.country()
        );
    }

    public static AssignRolesToUserCommand toCommand(
            UUID userUuid,
            AssignRolesToUserRequest request
    ) {
        return new AssignRolesToUserCommand(
                userUuid,
                request.roleUuids(),
                request.expiresAt(),
                request.reason()
        );
    }

    public static GrantPermissionsToUserCommand toCommand(
            UUID userUuid,
            GrantPermissionsToUserRequest request
    ) {
        return new GrantPermissionsToUserCommand(
                userUuid,
                request.permissionUuids(),
                request.effect(),
                request.expiresAt(),
                request.reason()
        );
    }

    public static CreateRoleCommand toCommand(CreateRoleRequest request) {
        return new CreateRoleCommand(
                request.name(),
                request.description(),
                request.permissionUuids()
        );
    }

    public static UpdateRoleCommand toCommand(UUID roleUuid, UpdateRoleRequest request) {
        return new UpdateRoleCommand(
                roleUuid,
                request.name(),
                request.description()
        );
    }

    public static AssignPermissionsToRoleCommand toCommand(
            UUID roleUuid,
            AssignPermissionsToRoleRequest request
    ) {
        return new AssignPermissionsToRoleCommand(
                roleUuid,
                request.permissionUuids(),
                request.expiresAt(),
                request.reason()
        );
    }

    public static CreatePermissionCommand toCommand(CreatePermissionRequest request) {
        return new CreatePermissionCommand(
                request.resource(),
                request.action(),
                request.description()
        );
    }

    public static UpdatePermissionCommand toCommand(
            UUID permissionUuid,
            UpdatePermissionRequest request
    ) {
        return new UpdatePermissionCommand(
                permissionUuid,
                request.resource(),
                request.action(),
                request.description()
        );
    }

    // ------------------------------------------------------------------
    // Results -> responses
    // ------------------------------------------------------------------

    public static UserResponse toResponse(UserResult result) {
        return new UserResponse(
                result.uuid(),
                result.username(),
                result.email(),
                result.emailVerified(),
                result.name(),
                result.locale(),
                result.enabled(),
                result.roles().stream().map(ApiMapper::toResponse).toList(),
                result.createdAt(),
                result.updatedAt()
        );
    }

    public static RoleSummaryResponse toResponse(RoleSummary summary) {
        return new RoleSummaryResponse(
                summary.uuid(),
                summary.name(),
                summary.active()
        );
    }

    public static UserSummaryResponse toResponse(UserSummary summary) {
        return new UserSummaryResponse(
                summary.uuid(),
                summary.username(),
                summary.email(),
                summary.name(),
                summary.enabled(),
                summary.createdAt()
        );
    }

    public static RoleResponse toResponse(RoleResult result) {
        return new RoleResponse(
                result.uuid(),
                result.name(),
                result.description(),
                result.system(),
                result.active(),
                result.createdAt(),
                result.updatedAt()
        );
    }

    public static PermissionResponse toResponse(PermissionResult result) {
        return new PermissionResponse(
                result.uuid(),
                result.name(),
                result.resource(),
                result.action(),
                result.description(),
                result.system(),
                result.active(),
                result.createdAt(),
                result.updatedAt()
        );
    }

    public static IdentityResponse toResponse(IdentityResult result) {
        return new IdentityResponse(
                result.uuid(),
                result.provider(),
                result.email(),
                result.displayName(),
                result.linkedAt()
        );
    }

    public static UserAuthoritiesResponse toResponse(UserAuthoritiesResult result) {
        return new UserAuthoritiesResponse(
                result.roles().stream()
                        .map(role -> new UserAuthoritiesResponse.RoleAuthority(
                                role.uuid(), role.name(), role.expiresAt()
                        ))
                        .toList(),
                result.directPermissions().stream()
                        .map(permission -> new UserAuthoritiesResponse.DirectPermission(
                                permission.name(), permission.effect()
                        ))
                        .toList(),
                result.effectivePermissions()
        );
    }

    public static RoleAssignmentResponse toResponse(RoleAssignmentResult result) {
        return new RoleAssignmentResponse(
                result.roleUuid(),
                result.roleName(),
                result.assignedAt(),
                result.assignedBy(),
                result.expiresAt()
        );
    }

    public static PermissionGrantResponse toResponse(PermissionGrantResult result) {
        return new PermissionGrantResponse(
                result.permissionUuid(),
                result.permissionName(),
                result.effect(),
                result.assignedAt(),
                result.assignedBy(),
                result.expiresAt()
        );
    }

    public static PermissionAssignmentResponse toResponse(
            PermissionAssignmentResult result
    ) {
        return new PermissionAssignmentResponse(
                result.permissionUuid(),
                result.permissionName(),
                result.assignedAt(),
                result.assignedBy(),
                result.expiresAt()
        );
    }

    public static <T, R> PageResponse<R> toPageResponse(
            PageResult<T> page,
            Function<T, R> mapper
    ) {
        List<R> content = page.content().stream().map(mapper).toList();

        return new PageResponse<>(
                content,
                new PageResponse.Page(
                        page.page().number(),
                        page.page().size(),
                        page.page().totalElements(),
                        page.page().totalPages()
                )
        );
    }
}

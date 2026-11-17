package kh.edu.istad.platform.iamrestapi.controller;

import jakarta.validation.Valid;
import kh.edu.istad.platform.iamapplicationservice.query.user.UserFilterQuery;
import kh.edu.istad.platform.iamapplicationservice.usecase.identity.ListUserIdentitiesUseCase;
import kh.edu.istad.platform.iamapplicationservice.usecase.identity.UnlinkIdentityUseCase;
import kh.edu.istad.platform.iamapplicationservice.usecase.user.AssignRolesToUserUseCase;
import kh.edu.istad.platform.iamapplicationservice.usecase.user.CreateUserUseCase;
import kh.edu.istad.platform.iamapplicationservice.usecase.user.DeleteUserUseCase;
import kh.edu.istad.platform.iamapplicationservice.usecase.user.DisableUserUseCase;
import kh.edu.istad.platform.iamapplicationservice.usecase.user.EnableUserUseCase;
import kh.edu.istad.platform.iamapplicationservice.usecase.user.GetUserAuthoritiesUseCase;
import kh.edu.istad.platform.iamapplicationservice.usecase.user.GetUserUseCase;
import kh.edu.istad.platform.iamapplicationservice.usecase.user.GrantPermissionsToUserUseCase;
import kh.edu.istad.platform.iamapplicationservice.usecase.user.ListUsersUseCase;
import kh.edu.istad.platform.iamapplicationservice.usecase.user.LockUserUseCase;
import kh.edu.istad.platform.iamapplicationservice.usecase.user.ResetUserPasswordUseCase;
import kh.edu.istad.platform.iamapplicationservice.usecase.user.RevokePermissionFromUserUseCase;
import kh.edu.istad.platform.iamapplicationservice.usecase.user.RevokeRoleFromUserUseCase;
import kh.edu.istad.platform.iamapplicationservice.usecase.user.UnlockUserUseCase;
import kh.edu.istad.platform.iamapplicationservice.usecase.security.ListLoginHistoryUseCase;
import kh.edu.istad.platform.iamapplicationservice.usecase.session.ListUserSessionsUseCase;
import kh.edu.istad.platform.iamapplicationservice.usecase.session.TerminateAllSessionsUseCase;
import kh.edu.istad.platform.iamapplicationservice.usecase.session.TerminateSessionUseCase;
import kh.edu.istad.platform.iamapplicationservice.usecase.user.UpdateUserProfileUseCase;
import kh.edu.istad.platform.iamapplicationservice.command.user.ResetUserPasswordCommand;
import kh.edu.istad.platform.iamrestapi.dto.request.AssignRolesToUserRequest;
import kh.edu.istad.platform.iamrestapi.dto.request.CreateUserRequest;
import kh.edu.istad.platform.iamrestapi.dto.request.GrantPermissionsToUserRequest;
import kh.edu.istad.platform.iamrestapi.dto.request.ResetPasswordRequest;
import kh.edu.istad.platform.iamrestapi.dto.request.UpdateUserProfileRequest;
import kh.edu.istad.platform.iamrestapi.dto.response.IdentityResponse;
import kh.edu.istad.platform.iamrestapi.dto.response.LoginAttemptResponse;
import kh.edu.istad.platform.iamrestapi.dto.response.PageResponse;
import kh.edu.istad.platform.iamrestapi.dto.response.PermissionGrantResponse;
import kh.edu.istad.platform.iamrestapi.dto.response.RoleAssignmentResponse;
import kh.edu.istad.platform.iamrestapi.dto.response.UserAuthoritiesResponse;
import kh.edu.istad.platform.iamrestapi.dto.response.UserResponse;
import kh.edu.istad.platform.iamrestapi.dto.response.UserSessionResponse;
import kh.edu.istad.platform.iamrestapi.dto.response.UserSummaryResponse;
import kh.edu.istad.platform.iamrestapi.mapper.ApiMapper;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final CreateUserUseCase createUserUseCase;
    private final ListUsersUseCase listUsersUseCase;
    private final GetUserUseCase getUserUseCase;
    private final UpdateUserProfileUseCase updateUserProfileUseCase;
    private final DeleteUserUseCase deleteUserUseCase;
    private final EnableUserUseCase enableUserUseCase;
    private final DisableUserUseCase disableUserUseCase;
    private final LockUserUseCase lockUserUseCase;
    private final UnlockUserUseCase unlockUserUseCase;
    private final ResetUserPasswordUseCase resetUserPasswordUseCase;
    private final GetUserAuthoritiesUseCase getUserAuthoritiesUseCase;
    private final AssignRolesToUserUseCase assignRolesToUserUseCase;
    private final RevokeRoleFromUserUseCase revokeRoleFromUserUseCase;
    private final GrantPermissionsToUserUseCase grantPermissionsToUserUseCase;
    private final RevokePermissionFromUserUseCase revokePermissionFromUserUseCase;
    private final ListUserIdentitiesUseCase listUserIdentitiesUseCase;
    private final UnlinkIdentityUseCase unlinkIdentityUseCase;
    private final ListUserSessionsUseCase listUserSessionsUseCase;
    private final TerminateSessionUseCase terminateSessionUseCase;
    private final TerminateAllSessionsUseCase terminateAllSessionsUseCase;
    private final ListLoginHistoryUseCase listLoginHistoryUseCase;

    public UserController(
            CreateUserUseCase createUserUseCase,
            ListUsersUseCase listUsersUseCase,
            GetUserUseCase getUserUseCase,
            UpdateUserProfileUseCase updateUserProfileUseCase,
            DeleteUserUseCase deleteUserUseCase,
            EnableUserUseCase enableUserUseCase,
            DisableUserUseCase disableUserUseCase,
            LockUserUseCase lockUserUseCase,
            UnlockUserUseCase unlockUserUseCase,
            ResetUserPasswordUseCase resetUserPasswordUseCase,
            GetUserAuthoritiesUseCase getUserAuthoritiesUseCase,
            AssignRolesToUserUseCase assignRolesToUserUseCase,
            RevokeRoleFromUserUseCase revokeRoleFromUserUseCase,
            GrantPermissionsToUserUseCase grantPermissionsToUserUseCase,
            RevokePermissionFromUserUseCase revokePermissionFromUserUseCase,
            ListUserIdentitiesUseCase listUserIdentitiesUseCase,
            UnlinkIdentityUseCase unlinkIdentityUseCase,
            ListUserSessionsUseCase listUserSessionsUseCase,
            TerminateSessionUseCase terminateSessionUseCase,
            TerminateAllSessionsUseCase terminateAllSessionsUseCase,
            ListLoginHistoryUseCase listLoginHistoryUseCase
    ) {
        this.createUserUseCase = createUserUseCase;
        this.listUsersUseCase = listUsersUseCase;
        this.getUserUseCase = getUserUseCase;
        this.updateUserProfileUseCase = updateUserProfileUseCase;
        this.deleteUserUseCase = deleteUserUseCase;
        this.enableUserUseCase = enableUserUseCase;
        this.disableUserUseCase = disableUserUseCase;
        this.lockUserUseCase = lockUserUseCase;
        this.unlockUserUseCase = unlockUserUseCase;
        this.resetUserPasswordUseCase = resetUserPasswordUseCase;
        this.getUserAuthoritiesUseCase = getUserAuthoritiesUseCase;
        this.assignRolesToUserUseCase = assignRolesToUserUseCase;
        this.revokeRoleFromUserUseCase = revokeRoleFromUserUseCase;
        this.grantPermissionsToUserUseCase = grantPermissionsToUserUseCase;
        this.revokePermissionFromUserUseCase = revokePermissionFromUserUseCase;
        this.listUserIdentitiesUseCase = listUserIdentitiesUseCase;
        this.unlinkIdentityUseCase = unlinkIdentityUseCase;
        this.listUserSessionsUseCase = listUserSessionsUseCase;
        this.terminateSessionUseCase = terminateSessionUseCase;
        this.terminateAllSessionsUseCase = terminateAllSessionsUseCase;
        this.listLoginHistoryUseCase = listLoginHistoryUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('user:create')")
    public UserResponse create(@Valid @RequestBody CreateUserRequest request) {
        return ApiMapper.toResponse(
                createUserUseCase.execute(ApiMapper.toCommand(request))
        );
    }

    @GetMapping
    @PreAuthorize("hasAuthority('user:read')")
    public PageResponse<UserSummaryResponse> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String sort
    ) {
        return ApiMapper.toPageResponse(
                listUsersUseCase.execute(new UserFilterQuery(page, size, sort)),
                ApiMapper::toResponse
        );
    }

    @GetMapping("/{uuid}")
    @PreAuthorize("hasAuthority('user:read')")
    public UserResponse get(@PathVariable UUID uuid) {
        return ApiMapper.toResponse(getUserUseCase.execute(uuid));
    }

    @PatchMapping("/{uuid}")
    @PreAuthorize("hasAuthority('user:update')")
    public UserResponse update(
            @PathVariable UUID uuid,
            @Valid @RequestBody UpdateUserProfileRequest request
    ) {
        return ApiMapper.toResponse(
                updateUserProfileUseCase.execute(ApiMapper.toCommand(uuid, request))
        );
    }

    @DeleteMapping("/{uuid}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAuthority('user:delete')")
    public void delete(@PathVariable UUID uuid) {
        deleteUserUseCase.execute(uuid);
    }

    @PostMapping("/{uuid}/enable")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAuthority('user:update')")
    public void enable(@PathVariable UUID uuid) {
        enableUserUseCase.execute(uuid);
    }

    @PostMapping("/{uuid}/disable")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAuthority('user:update')")
    public void disable(@PathVariable UUID uuid) {
        disableUserUseCase.execute(uuid);
    }

    @PostMapping("/{uuid}/lock")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAuthority('user:update')")
    public void lock(@PathVariable UUID uuid) {
        lockUserUseCase.execute(uuid);
    }

    @PostMapping("/{uuid}/unlock")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAuthority('user:update')")
    public void unlock(@PathVariable UUID uuid) {
        unlockUserUseCase.execute(uuid);
    }

    @PutMapping("/{uuid}/password")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAuthority('user:update')")
    public void resetPassword(
            @PathVariable UUID uuid,
            @Valid @RequestBody ResetPasswordRequest request
    ) {
        resetUserPasswordUseCase.execute(
                new ResetUserPasswordCommand(uuid, request.newPassword())
        );
    }

    @GetMapping("/{uuid}/authorities")
    @PreAuthorize("hasAuthority('user:read')")
    public UserAuthoritiesResponse authorities(@PathVariable UUID uuid) {
        return ApiMapper.toResponse(getUserAuthoritiesUseCase.execute(uuid));
    }

    @PostMapping("/{uuid}/roles")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('user:assign')")
    public List<RoleAssignmentResponse> assignRoles(
            @PathVariable UUID uuid,
            @Valid @RequestBody AssignRolesToUserRequest request
    ) {
        return assignRolesToUserUseCase
                .execute(ApiMapper.toCommand(uuid, request))
                .stream()
                .map(ApiMapper::toResponse)
                .toList();
    }

    @DeleteMapping("/{uuid}/roles/{roleUuid}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAuthority('user:assign')")
    public void revokeRole(
            @PathVariable UUID uuid,
            @PathVariable UUID roleUuid,
            @RequestParam(required = false) String reason
    ) {
        revokeRoleFromUserUseCase.execute(uuid, roleUuid, reason);
    }

    @PostMapping("/{uuid}/permissions")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('user:assign')")
    public List<PermissionGrantResponse> grantPermissions(
            @PathVariable UUID uuid,
            @Valid @RequestBody GrantPermissionsToUserRequest request
    ) {
        return grantPermissionsToUserUseCase
                .execute(ApiMapper.toCommand(uuid, request))
                .stream()
                .map(ApiMapper::toResponse)
                .toList();
    }

    @DeleteMapping("/{uuid}/permissions/{permissionUuid}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAuthority('user:assign')")
    public void revokePermission(
            @PathVariable UUID uuid,
            @PathVariable UUID permissionUuid,
            @RequestParam(required = false) String reason
    ) {
        revokePermissionFromUserUseCase.execute(uuid, permissionUuid, reason);
    }

    @GetMapping("/{uuid}/identities")
    @PreAuthorize("hasAuthority('user:read')")
    public List<IdentityResponse> identities(@PathVariable UUID uuid) {
        return listUserIdentitiesUseCase.execute(uuid)
                .stream()
                .map(ApiMapper::toResponse)
                .toList();
    }

    @DeleteMapping("/{uuid}/identities/{identityUuid}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAuthority('user:update')")
    public void unlinkIdentity(
            @PathVariable UUID uuid,
            @PathVariable UUID identityUuid
    ) {
        unlinkIdentityUseCase.execute(uuid, identityUuid);
    }

    // ------------------------------------------------------------------
    // Administration of a user's sessions and login history
    // ------------------------------------------------------------------

    @GetMapping("/{uuid}/sessions")
    @PreAuthorize("hasAuthority('user:read')")
    public List<UserSessionResponse> userSessions(@PathVariable UUID uuid) {
        return listUserSessionsUseCase
                .execute(uuid, null)
                .stream()
                .map(ApiMapper::toResponse)
                .toList();
    }

    @DeleteMapping("/{uuid}/sessions/{sessionUuid}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAuthority('user:update')")
    public void terminateUserSession(
            @PathVariable UUID uuid,
            @PathVariable UUID sessionUuid
    ) {
        terminateSessionUseCase.execute(uuid, sessionUuid, "Terminated by an administrator");
    }

    @DeleteMapping("/{uuid}/sessions")
    @PreAuthorize("hasAuthority('user:update')")
    public TerminateAllUsersSessionsResponse terminateAllUserSessions(
            @PathVariable UUID uuid
    ) {
        return new TerminateAllUsersSessionsResponse(
                terminateAllSessionsUseCase.execute(uuid, null)
        );
    }

    @GetMapping("/{uuid}/login-history")
    @PreAuthorize("hasAuthority('user:read')")
    public PageResponse<LoginAttemptResponse> userLoginHistory(
            @PathVariable UUID uuid,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return ApiMapper.toPageResponse(
                listLoginHistoryUseCase.execute(uuid, page, size),
                ApiMapper::toResponse
        );
    }

    public record TerminateAllUsersSessionsResponse(int revoked) {
    }
}

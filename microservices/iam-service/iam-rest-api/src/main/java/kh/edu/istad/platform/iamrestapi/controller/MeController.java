package kh.edu.istad.platform.iamrestapi.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import kh.edu.istad.platform.iamapplicationservice.command.user.ChangePasswordCommand;
import kh.edu.istad.platform.iamapplicationservice.usecase.identity.ListUserIdentitiesUseCase;
import kh.edu.istad.platform.iamapplicationservice.usecase.identity.UnlinkIdentityUseCase;
import kh.edu.istad.platform.iamapplicationservice.usecase.security.ListAccountActivityUseCase;
import kh.edu.istad.platform.iamapplicationservice.usecase.security.ListLoginHistoryUseCase;
import kh.edu.istad.platform.iamapplicationservice.usecase.session.ListUserSessionsUseCase;
import kh.edu.istad.platform.iamapplicationservice.usecase.session.TerminateAllSessionsUseCase;
import kh.edu.istad.platform.iamapplicationservice.usecase.session.TerminateSessionUseCase;
import kh.edu.istad.platform.iamapplicationservice.usecase.user.ChangePasswordUseCase;
import kh.edu.istad.platform.iamapplicationservice.usecase.user.DeactivateAccountUseCase;
import kh.edu.istad.platform.iamapplicationservice.usecase.user.GetUserUseCase;
import kh.edu.istad.platform.iamapplicationservice.usecase.user.UpdateUserProfileUseCase;
import kh.edu.istad.platform.iamrestapi.dto.request.ChangePasswordRequest;
import kh.edu.istad.platform.iamrestapi.dto.request.DeactivateAccountRequest;
import kh.edu.istad.platform.iamrestapi.dto.request.UpdateUserProfileRequest;
import kh.edu.istad.platform.iamrestapi.dto.response.AccountActivityResponse;
import kh.edu.istad.platform.iamrestapi.dto.response.IdentityResponse;
import kh.edu.istad.platform.iamrestapi.dto.response.LoginAttemptResponse;
import kh.edu.istad.platform.iamrestapi.dto.response.PageResponse;
import kh.edu.istad.platform.iamrestapi.dto.response.UserResponse;
import kh.edu.istad.platform.iamrestapi.dto.response.UserSessionResponse;
import kh.edu.istad.platform.iamrestapi.mapper.ApiMapper;
import kh.edu.istad.platform.iamrestapi.security.AuthenticatedUser;
import org.springframework.http.HttpStatus;
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
@RequestMapping("/api/me")
public class MeController {

    /**
     * Mirrors {@code SessionTrackingFilter.SESSION_KEY_ATTRIBUTE}: identifies the
     * caller's own device so it can be flagged and kept alive.
     */
    private static final String SESSION_KEY_ATTRIBUTE = "iam.sessionKey";

    private final GetUserUseCase getUserUseCase;
    private final UpdateUserProfileUseCase updateUserProfileUseCase;
    private final ChangePasswordUseCase changePasswordUseCase;
    private final ListUserIdentitiesUseCase listUserIdentitiesUseCase;
    private final UnlinkIdentityUseCase unlinkIdentityUseCase;
    private final ListUserSessionsUseCase listUserSessionsUseCase;
    private final TerminateSessionUseCase terminateSessionUseCase;
    private final TerminateAllSessionsUseCase terminateAllSessionsUseCase;
    private final ListLoginHistoryUseCase listLoginHistoryUseCase;
    private final ListAccountActivityUseCase listAccountActivityUseCase;
    private final DeactivateAccountUseCase deactivateAccountUseCase;

    public MeController(
            GetUserUseCase getUserUseCase,
            UpdateUserProfileUseCase updateUserProfileUseCase,
            ChangePasswordUseCase changePasswordUseCase,
            ListUserIdentitiesUseCase listUserIdentitiesUseCase,
            UnlinkIdentityUseCase unlinkIdentityUseCase,
            ListUserSessionsUseCase listUserSessionsUseCase,
            TerminateSessionUseCase terminateSessionUseCase,
            TerminateAllSessionsUseCase terminateAllSessionsUseCase,
            ListLoginHistoryUseCase listLoginHistoryUseCase,
            ListAccountActivityUseCase listAccountActivityUseCase,
            DeactivateAccountUseCase deactivateAccountUseCase
    ) {
        this.getUserUseCase = getUserUseCase;
        this.updateUserProfileUseCase = updateUserProfileUseCase;
        this.changePasswordUseCase = changePasswordUseCase;
        this.listUserIdentitiesUseCase = listUserIdentitiesUseCase;
        this.unlinkIdentityUseCase = unlinkIdentityUseCase;
        this.listUserSessionsUseCase = listUserSessionsUseCase;
        this.terminateSessionUseCase = terminateSessionUseCase;
        this.terminateAllSessionsUseCase = terminateAllSessionsUseCase;
        this.listLoginHistoryUseCase = listLoginHistoryUseCase;
        this.listAccountActivityUseCase = listAccountActivityUseCase;
        this.deactivateAccountUseCase = deactivateAccountUseCase;
    }

    @GetMapping
    public UserResponse me() {
        return ApiMapper.toResponse(
                getUserUseCase.execute(AuthenticatedUser.currentUuid())
        );
    }

    @PatchMapping
    public UserResponse updateProfile(
            @Valid @RequestBody UpdateUserProfileRequest request
    ) {
        return ApiMapper.toResponse(updateUserProfileUseCase.execute(
                ApiMapper.toCommand(AuthenticatedUser.currentUuid(), request)
        ));
    }

    @PutMapping("/password")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        if (!request.newPassword().equals(request.confirmPassword())) {
            throw new IllegalArgumentException("Passwords do not match");
        }

        changePasswordUseCase.execute(new ChangePasswordCommand(
                AuthenticatedUser.currentUuid(),
                request.currentPassword(),
                request.newPassword()
        ));
    }

    @GetMapping("/identities")
    public List<IdentityResponse> identities() {
        return listUserIdentitiesUseCase
                .execute(AuthenticatedUser.currentUuid())
                .stream()
                .map(ApiMapper::toResponse)
                .toList();
    }

    @DeleteMapping("/identities/{identityUuid}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void unlinkIdentity(@PathVariable UUID identityUuid) {
        unlinkIdentityUseCase.execute(
                AuthenticatedUser.currentUuid(),
                identityUuid
        );
    }

    // ------------------------------------------------------------------
    // Active sessions
    // ------------------------------------------------------------------

    @GetMapping("/sessions")
    public List<UserSessionResponse> sessions(HttpServletRequest request) {
        return listUserSessionsUseCase
                .execute(
                        AuthenticatedUser.currentUuid(),
                        currentSessionKey(request)
                )
                .stream()
                .map(ApiMapper::toResponse)
                .toList();
    }

    @DeleteMapping("/sessions/{sessionUuid}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void terminateSession(@PathVariable UUID sessionUuid) {
        terminateSessionUseCase.execute(
                AuthenticatedUser.currentUuid(),
                sessionUuid,
                "Terminated by the account owner"
        );
    }

    /**
     * Signs out every other device; the caller stays signed in.
     */
    @DeleteMapping("/sessions")
    public TerminateAllResponse terminateAllSessions(HttpServletRequest request) {
        int revoked = terminateAllSessionsUseCase.execute(
                AuthenticatedUser.currentUuid(),
                currentSessionKey(request)
        );

        return new TerminateAllResponse(revoked);
    }

    // ------------------------------------------------------------------
    // Login history and activity
    // ------------------------------------------------------------------

    @GetMapping("/login-history")
    public PageResponse<LoginAttemptResponse> loginHistory(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return ApiMapper.toPageResponse(
                listLoginHistoryUseCase.execute(
                        AuthenticatedUser.currentUuid(), page, size
                ),
                ApiMapper::toResponse
        );
    }

    @GetMapping("/activity")
    public PageResponse<AccountActivityResponse> activity(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return ApiMapper.toPageResponse(
                listAccountActivityUseCase.execute(
                        AuthenticatedUser.currentUuid(), page, size
                ),
                ApiMapper::toResponse
        );
    }

    // ------------------------------------------------------------------
    // Deactivation
    // ------------------------------------------------------------------

    @PostMapping("/deactivate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deactivate(
            @Valid @RequestBody(required = false) DeactivateAccountRequest request
    ) {
        deactivateAccountUseCase.execute(
                AuthenticatedUser.currentUuid(),
                request == null ? null : request.reason()
        );
    }

    private String currentSessionKey(HttpServletRequest request) {
        Object value = request.getAttribute(SESSION_KEY_ATTRIBUTE);
        return value == null ? null : value.toString();
    }

    public record TerminateAllResponse(int revoked) {
    }
}

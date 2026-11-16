package kh.edu.istad.platform.iamrestapi.controller;

import jakarta.validation.Valid;
import kh.edu.istad.platform.iamapplicationservice.command.user.ChangePasswordCommand;
import kh.edu.istad.platform.iamapplicationservice.usecase.identity.ListUserIdentitiesUseCase;
import kh.edu.istad.platform.iamapplicationservice.usecase.identity.UnlinkIdentityUseCase;
import kh.edu.istad.platform.iamapplicationservice.usecase.user.ChangePasswordUseCase;
import kh.edu.istad.platform.iamapplicationservice.usecase.user.GetUserUseCase;
import kh.edu.istad.platform.iamapplicationservice.usecase.user.UpdateUserProfileUseCase;
import kh.edu.istad.platform.iamrestapi.dto.request.ChangePasswordRequest;
import kh.edu.istad.platform.iamrestapi.dto.request.UpdateUserProfileRequest;
import kh.edu.istad.platform.iamrestapi.dto.response.IdentityResponse;
import kh.edu.istad.platform.iamrestapi.dto.response.UserResponse;
import kh.edu.istad.platform.iamrestapi.mapper.ApiMapper;
import kh.edu.istad.platform.iamrestapi.security.AuthenticatedUser;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/me")
public class MeController {

    private final GetUserUseCase getUserUseCase;
    private final UpdateUserProfileUseCase updateUserProfileUseCase;
    private final ChangePasswordUseCase changePasswordUseCase;
    private final ListUserIdentitiesUseCase listUserIdentitiesUseCase;
    private final UnlinkIdentityUseCase unlinkIdentityUseCase;

    public MeController(
            GetUserUseCase getUserUseCase,
            UpdateUserProfileUseCase updateUserProfileUseCase,
            ChangePasswordUseCase changePasswordUseCase,
            ListUserIdentitiesUseCase listUserIdentitiesUseCase,
            UnlinkIdentityUseCase unlinkIdentityUseCase
    ) {
        this.getUserUseCase = getUserUseCase;
        this.updateUserProfileUseCase = updateUserProfileUseCase;
        this.changePasswordUseCase = changePasswordUseCase;
        this.listUserIdentitiesUseCase = listUserIdentitiesUseCase;
        this.unlinkIdentityUseCase = unlinkIdentityUseCase;
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
}

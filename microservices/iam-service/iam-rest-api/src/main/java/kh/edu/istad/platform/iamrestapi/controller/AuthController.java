package kh.edu.istad.platform.iamrestapi.controller;

import jakarta.validation.Valid;
import kh.edu.istad.platform.iamapplicationservice.usecase.user.RegisterUserUseCase;
import kh.edu.istad.platform.iamrestapi.dto.request.RegisterUserRequest;
import kh.edu.istad.platform.iamrestapi.dto.response.UserResponse;
import kh.edu.istad.platform.iamrestapi.mapper.ApiMapper;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final RegisterUserUseCase registerUserUseCase;

    public AuthController(RegisterUserUseCase registerUserUseCase) {
        this.registerUserUseCase = registerUserUseCase;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse register(@Valid @RequestBody RegisterUserRequest request) {
        if (!request.password().equals(request.confirmPassword())) {
            throw new IllegalArgumentException("Passwords do not match");
        }

        return ApiMapper.toResponse(
                registerUserUseCase.execute(ApiMapper.toCommand(request))
        );
    }
}

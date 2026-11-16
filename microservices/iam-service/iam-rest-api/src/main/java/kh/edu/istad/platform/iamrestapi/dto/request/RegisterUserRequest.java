package kh.edu.istad.platform.iamrestapi.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterUserRequest(
        @NotBlank
        @Size(max = 50)
        String username,

        @NotBlank
        @Email
        @Size(max = 255)
        String email,

        @NotBlank
        @Size(min = 8, max = 100)
        String password,

        @NotBlank
        String confirmPassword,

        @NotBlank
        @Size(max = 100)
        String givenName,

        @NotBlank
        @Size(max = 100)
        String familyName,

        @Size(max = 10)
        String locale
) {
}

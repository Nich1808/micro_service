package kh.edu.istad.platform.iamrestapi.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record CreateUserRequest(
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
        @Size(max = 100)
        String givenName,

        @NotBlank
        @Size(max = 100)
        String familyName,

        @Size(max = 20)
        String gender,

        LocalDate birthdate,

        @Size(max = 20)
        String phoneNumber,

        @Size(max = 50)
        String zoneinfo,

        @Size(max = 10)
        String locale,

        @Valid
        AddressRequest address,

        List<UUID> roleUuids
) {
}

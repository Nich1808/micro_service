package kh.edu.istad.platform.iamrestapi.dto.request;

import jakarta.validation.constraints.Size;

public record DeactivateAccountRequest(
        @Size(max = 255)
        String reason
) {
}

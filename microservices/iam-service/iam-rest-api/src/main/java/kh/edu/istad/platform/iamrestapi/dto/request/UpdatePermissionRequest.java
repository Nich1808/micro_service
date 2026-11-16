package kh.edu.istad.platform.iamrestapi.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdatePermissionRequest(
        @NotBlank
        @Size(max = 50)
        String resource,

        @NotBlank
        @Size(max = 50)
        String action,

        @Size(max = 255)
        String description
) {
}

package kh.edu.istad.platform.iamapplicationservice.command.user;

import java.util.UUID;

public record ChangePasswordCommand(
        UUID userUuid,
        String currentPassword,
        String newPassword
) {
}

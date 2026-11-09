package kh.edu.istad.platform.iamapplicationservice.command.user;

import java.util.UUID;

public record ResetUserPasswordCommand(
        UUID userUuid,
        String newPassword
) {
}

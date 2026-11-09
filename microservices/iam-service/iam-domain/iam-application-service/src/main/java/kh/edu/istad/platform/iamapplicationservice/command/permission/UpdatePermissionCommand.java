package kh.edu.istad.platform.iamapplicationservice.command.permission;

import java.util.UUID;

public record UpdatePermissionCommand(
        UUID permissionUuid,
        String resource,
        String action,
        String description
) {}

package kh.edu.istad.platform.iamapplicationservice.command.permission;

public record CreatePermissionCommand(
        String resource,
        String action,
        String description
) {}

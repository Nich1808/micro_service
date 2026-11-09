package kh.edu.istad.platform.iamapplicationservice.command.role;

import java.util.UUID;

public record UpdateRoleCommand(
        UUID roleUuid,
        String name,
        String description
) {}

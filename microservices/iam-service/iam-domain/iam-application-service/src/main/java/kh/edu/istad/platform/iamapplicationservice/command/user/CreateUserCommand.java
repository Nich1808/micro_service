package kh.edu.istad.platform.iamapplicationservice.command.user;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record CreateUserCommand(
        String username,
        String email,
        String rawPassword,
        String givenName,
        String familyName,
        String gender,
        LocalDate birthdate,
        String phoneNumber,
        String zoneinfo,
        String locale,
        String streetAddress,
        String locality,
        String region,
        String postalCode,
        String country,
        List<UUID> roleUuids
) {
    public CreateUserCommand {
        roleUuids = roleUuids == null ? List.of() : List.copyOf(roleUuids);
    }
}

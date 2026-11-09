package kh.edu.istad.platform.iamapplicationservice.command.user;

public record CreateUserCommand(
        String username,
        String email,
        String rawPassword,
        String givenName,
        String familyName
) {
}

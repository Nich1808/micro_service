package kh.edu.istad.platform.iamapplicationservice.command.user;

public record RegisterUserCommand(
        String username,
        String email,
        String rawPassword,
        String givenName,
        String familyName
) {
}

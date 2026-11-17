package kh.edu.istad.platform.iamapplicationservice.command.security;

/**
 * Details of a sign-in attempt. Client information is resolved by the adapter that saw
 * the HTTP request, so the application layer stays transport-agnostic.
 */
public record LoginAttemptCommand(
        String identifier,
        boolean successful,
        String failureReason,
        String ipAddress,
        String device,
        String browser,
        String operatingSystem,
        String location,
        String userAgent
) {

    public static LoginAttemptCommand of(String identifier, boolean successful) {
        return new LoginAttemptCommand(
                identifier, successful, null, null, null, null, null, null, null
        );
    }
}

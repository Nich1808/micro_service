package kh.edu.istad.platform.iamrestapi.dto.admin;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.time.OffsetDateTime;
import java.util.List;

/**
 * Request/response payloads for the administration endpoints.
 */
public final class AdminDto {

    private AdminDto() {
    }

    public record AdminOverviewResponse(
            long totalUsers,
            long activeUsers,
            long lockedUsers,
            long disabledUsers,
            long totalRoles,
            long totalPermissions,
            long totalClients,
            long activeSessions,
            long loginAttemptsToday,
            long failedLoginsToday,
            List<RecentEvent> recentEvents
    ) {
        public record RecentEvent(
                String type,
                String description,
                String actor,
                OffsetDateTime createdAt
        ) {
        }
    }

    public record ClientRequest(
            @NotBlank
            @Size(max = 100)
            String clientId,

            @NotBlank
            @Size(max = 200)
            String clientName,

            @Size(max = 200)
            String clientSecret,

            @NotEmpty
            List<String> authenticationMethods,

            @NotEmpty
            List<String> grantTypes,

            List<String> redirectUris,

            List<String> postLogoutRedirectUris,

            List<String> scopes,

            Boolean requireProofKey,

            Boolean requireConsent,

            Long accessTokenTtlSeconds,

            Long refreshTokenTtlSeconds
    ) {
        public ClientRequest {
            authenticationMethods = copy(authenticationMethods);
            grantTypes = copy(grantTypes);
            redirectUris = copy(redirectUris);
            postLogoutRedirectUris = copy(postLogoutRedirectUris);
            scopes = copy(scopes);
        }

        private static List<String> copy(List<String> values) {
            return values == null ? List.of() : List.copyOf(values);
        }
    }

    public record ClientResponse(
            String clientId,
            String clientName,
            List<String> authenticationMethods,
            List<String> grantTypes,
            List<String> redirectUris,
            List<String> postLogoutRedirectUris,
            List<String> scopes,
            boolean requireProofKey,
            boolean requireConsent,
            Long accessTokenTtlSeconds,
            Long refreshTokenTtlSeconds,
            boolean confidential
    ) {
    }

    public record SystemSettingsResponse(
            int passwordMinLength,
            boolean passwordRequireUppercase,
            boolean passwordRequireDigit,
            boolean passwordRequireSymbol,
            int maxFailedLoginAttempts,
            int lockoutMinutes,
            int sessionIdleTimeoutMinutes,
            int sessionAbsoluteTimeoutHours,
            long accessTokenTtlMinutes,
            long refreshTokenTtlDays,
            boolean registrationEnabled,
            boolean socialLoginEnabled
    ) {
    }

    public record UpdateSystemSettingsRequest(
            Integer passwordMinLength,
            Boolean passwordRequireUppercase,
            Boolean passwordRequireDigit,
            Boolean passwordRequireSymbol,
            Integer maxFailedLoginAttempts,
            Integer lockoutMinutes,
            Integer sessionIdleTimeoutMinutes,
            Integer sessionAbsoluteTimeoutHours,
            Long accessTokenTtlMinutes,
            Long refreshTokenTtlDays,
            Boolean registrationEnabled,
            Boolean socialLoginEnabled
    ) {
    }
}

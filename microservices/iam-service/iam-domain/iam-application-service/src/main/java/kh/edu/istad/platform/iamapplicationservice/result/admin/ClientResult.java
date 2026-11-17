package kh.edu.istad.platform.iamapplicationservice.result.admin;

import java.util.List;

public record ClientResult(
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

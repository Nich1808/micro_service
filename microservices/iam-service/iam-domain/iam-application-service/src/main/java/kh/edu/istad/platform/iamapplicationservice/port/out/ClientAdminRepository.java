package kh.edu.istad.platform.iamapplicationservice.port.out;

import java.util.List;
import java.util.Optional;

/**
 * Administration of OAuth2/OIDC clients. Kept separate from {@link ClientRegistryQuery},
 * which is the read-only view used by the consent page.
 */
public interface ClientAdminRepository {

    List<ClientRecord> findAll();

    Optional<ClientRecord> findByClientId(String clientId);

    /**
     * Creates or replaces a client. The plain secret is hashed by the adapter; it is
     * never stored or returned in clear text.
     */
    ClientRecord save(ClientRecord client);

    void delete(String clientId);

    record ClientRecord(
            String internalId,
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
            String clientSecretHash
    ) {
        public ClientRecord {
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
}

package kh.edu.istad.platform.iamapplicationservice.port.out;

import java.util.List;
import java.util.Optional;

/**
 * Read model over the registered OAuth2/OIDC clients. Used by the consent page to
 * present a client name and the scopes the client is requesting.
 */
public interface ClientRegistryQuery {

    Optional<ClientDetails> findByClientId(String clientId);

    record ClientDetails(
            String clientId,
            String clientName,
            List<String> scopes
    ) {
        public ClientDetails {
            scopes = scopes == null ? List.of() : List.copyOf(scopes);
        }
    }
}

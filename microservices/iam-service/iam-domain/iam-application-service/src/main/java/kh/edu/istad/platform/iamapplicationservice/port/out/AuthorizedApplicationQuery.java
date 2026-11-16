package kh.edu.istad.platform.iamapplicationservice.port.out;

import java.time.OffsetDateTime;
import java.util.List;

/**
 * Read/revoke model over the authorizations granted to a principal, backing the
 * "connected apps" page.
 */
public interface AuthorizedApplicationQuery {

    List<AuthorizedApplication> findAllByPrincipal(String principalName);

    void revoke(String clientId, String principalName);

    record AuthorizedApplication(
            String clientId,
            String clientName,
            List<String> scopes,
            OffsetDateTime authorizedAt
    ) {
        public AuthorizedApplication {
            scopes = scopes == null ? List.of() : List.copyOf(scopes);
        }
    }
}

package kh.edu.istad.platform.iaminfrastructure.oauth;

import kh.edu.istad.platform.iamapplicationservice.port.out.AuthorizedApplicationQuery;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Reads and revokes the authorizations stored by the JDBC authorization service.
 * Principal names are IAM usernames.
 */
@Component
public class AuthorizedApplicationQueryAdapter implements AuthorizedApplicationQuery {

    private final JdbcTemplate jdbcTemplate;
    private final RegisteredClientRepository registeredClientRepository;

    public AuthorizedApplicationQueryAdapter(
            JdbcTemplate jdbcTemplate,
            RegisteredClientRepository registeredClientRepository
    ) {
        this.jdbcTemplate = jdbcTemplate;
        this.registeredClientRepository = registeredClientRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<AuthorizedApplication> findAllByPrincipal(String principalName) {
        if (principalName == null || principalName.isBlank()) {
            return List.of();
        }

        return jdbcTemplate.query(
                """
                        select distinct registered_client_id, authorized_scopes,
                               access_token_issued_at, authorization_code_issued_at
                        from oauth2_authorization
                        where principal_name = ?
                        """,
                (rs, rowNum) -> {
                    String clientId = rs.getString("registered_client_id");
                    RegisteredClient client =
                            registeredClientRepository.findById(clientId);

                    Timestamp issuedAt = rs.getTimestamp("access_token_issued_at") != null
                            ? rs.getTimestamp("access_token_issued_at")
                            : rs.getTimestamp("authorization_code_issued_at");

                    return new AuthorizedApplication(
                            client == null ? clientId : client.getClientId(),
                            client == null ? clientId : client.getClientName(),
                            parseScopes(rs.getString("authorized_scopes")),
                            issuedAt == null
                                    ? null
                                    : issuedAt.toInstant()
                                            .atOffset(OffsetDateTime.now().getOffset())
                    );
                },
                principalName
        );
    }

    @Override
    @Transactional
    public void revoke(String clientId, String principalName) {
        if (clientId == null || principalName == null) {
            return;
        }

        RegisteredClient client = registeredClientRepository.findByClientId(clientId);
        String registeredClientId = client == null ? clientId : client.getId();

        jdbcTemplate.update(
                "delete from oauth2_authorization_consent "
                        + "where registered_client_id = ? and principal_name = ?",
                registeredClientId,
                principalName
        );

        jdbcTemplate.update(
                "delete from oauth2_authorization "
                        + "where registered_client_id = ? and principal_name = ?",
                registeredClientId,
                principalName
        );
    }

    private static List<String> parseScopes(String scopes) {
        if (scopes == null || scopes.isBlank()) {
            return List.of();
        }

        return new ArrayList<>(Arrays.asList(scopes.split(",")));
    }
}

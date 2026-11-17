package kh.edu.istad.platform.iaminfrastructure.admin;

import kh.edu.istad.platform.iamapplicationservice.port.out.ClientAdminRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

/**
 * Client administration on top of the JDBC {@link RegisteredClientRepository}: saves go
 * through Spring Authorization Server so the row format stays canonical, and deletion is
 * done with SQL because the repository has no delete operation.
 */
@Component
public class ClientAdminAdapter implements ClientAdminRepository {

    private static final long DEFAULT_ACCESS_TTL_SECONDS = 3600L;
    private static final long DEFAULT_REFRESH_TTL_SECONDS = 604_800L;

    private final RegisteredClientRepository registeredClientRepository;
    private final JdbcTemplate jdbcTemplate;

    public ClientAdminAdapter(
            RegisteredClientRepository registeredClientRepository,
            JdbcTemplate jdbcTemplate
    ) {
        this.registeredClientRepository = registeredClientRepository;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClientRecord> findAll() {
        return jdbcTemplate.query(
                "select client_id from oauth2_registered_client order by client_id",
                (rs, rowNum) -> rs.getString("client_id")
        ).stream()
                .map(registeredClientRepository::findByClientId)
                .filter(java.util.Objects::nonNull)
                .map(this::toRecord)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ClientRecord> findByClientId(String clientId) {
        RegisteredClient client = registeredClientRepository.findByClientId(clientId);
        return client == null ? Optional.empty() : Optional.of(toRecord(client));
    }

    @Override
    @Transactional
    public ClientRecord save(ClientRecord record) {
        RegisteredClient existing =
                registeredClientRepository.findByClientId(record.clientId());

        RegisteredClient.Builder builder = existing == null
                ? RegisteredClient.withId(record.internalId() == null
                        ? UUID.randomUUID().toString()
                        : record.internalId())
                        .clientId(record.clientId())
                        .clientIdIssuedAt(java.time.Instant.now())
                : RegisteredClient.from(existing);

        builder.clientName(record.clientName());

        if (record.clientSecretHash() != null) {
            builder.clientSecret(record.clientSecretHash());
        }

        // Replace the collection values rather than appending to them.
        builder.clientAuthenticationMethods(methods -> {
            methods.clear();
            authenticationMethods(record).forEach(methods::add);
        });

        builder.authorizationGrantTypes(grants -> {
            grants.clear();
            grantTypes(record).forEach(grants::add);
        });

        builder.redirectUris(uris -> {
            uris.clear();
            uris.addAll(record.redirectUris());
        });

        builder.postLogoutRedirectUris(uris -> {
            uris.clear();
            uris.addAll(record.postLogoutRedirectUris());
        });

        builder.scopes(scopes -> {
            scopes.clear();
            scopes.addAll(record.scopes());
        });

        builder.clientSettings(ClientSettings.builder()
                .requireProofKey(record.requireProofKey())
                .requireAuthorizationConsent(record.requireConsent())
                .build());

        long accessTtl = record.accessTokenTtlSeconds() == null
                ? DEFAULT_ACCESS_TTL_SECONDS
                : record.accessTokenTtlSeconds();
        long refreshTtl = record.refreshTokenTtlSeconds() == null
                ? DEFAULT_REFRESH_TTL_SECONDS
                : record.refreshTokenTtlSeconds();

        builder.tokenSettings(TokenSettings.builder()
                .accessTokenTimeToLive(Duration.ofSeconds(accessTtl))
                .refreshTokenTimeToLive(Duration.ofSeconds(refreshTtl))
                .authorizationCodeTimeToLive(Duration.ofMinutes(3))
                .reuseRefreshTokens(false)
                .build());

        RegisteredClient saved = builder.build();
        registeredClientRepository.save(saved);
        return toRecord(saved);
    }

    @Override
    @Transactional
    public void delete(String clientId) {
        RegisteredClient client = registeredClientRepository.findByClientId(clientId);

        if (client == null) {
            return;
        }

        jdbcTemplate.update(
                "delete from oauth2_authorization_consent where registered_client_id = ?",
                client.getId()
        );
        jdbcTemplate.update(
                "delete from oauth2_authorization where registered_client_id = ?",
                client.getId()
        );
        jdbcTemplate.update(
                "delete from oauth2_registered_client where id = ?",
                client.getId()
        );
    }

    private Set<ClientAuthenticationMethod> authenticationMethods(ClientRecord record) {
        Set<ClientAuthenticationMethod> methods = new LinkedHashSet<>();

        for (String method : record.authenticationMethods()) {
            methods.add(new ClientAuthenticationMethod(method));
        }

        if (methods.isEmpty()) {
            methods.add(ClientAuthenticationMethod.NONE);
        }

        return methods;
    }

    private Set<AuthorizationGrantType> grantTypes(ClientRecord record) {
        Set<AuthorizationGrantType> grants = new LinkedHashSet<>();

        for (String grant : record.grantTypes()) {
            grants.add(new AuthorizationGrantType(grant));
        }

        if (grants.isEmpty()) {
            grants.add(AuthorizationGrantType.AUTHORIZATION_CODE);
        }

        return grants;
    }

    private ClientRecord toRecord(RegisteredClient client) {
        List<String> methods = client.getClientAuthenticationMethods().stream()
                .map(ClientAuthenticationMethod::getValue)
                .toList();

        return new ClientRecord(
                client.getId(),
                client.getClientId(),
                client.getClientName(),
                methods,
                client.getAuthorizationGrantTypes().stream()
                        .map(AuthorizationGrantType::getValue)
                        .toList(),
                new ArrayList<>(client.getRedirectUris()),
                new ArrayList<>(client.getPostLogoutRedirectUris()),
                new ArrayList<>(client.getScopes()),
                client.getClientSettings().isRequireProofKey(),
                client.getClientSettings().isRequireAuthorizationConsent(),
                client.getTokenSettings().getAccessTokenTimeToLive().toSeconds(),
                client.getTokenSettings().getRefreshTokenTimeToLive().toSeconds(),
                client.getClientSecret()
        );
    }
}

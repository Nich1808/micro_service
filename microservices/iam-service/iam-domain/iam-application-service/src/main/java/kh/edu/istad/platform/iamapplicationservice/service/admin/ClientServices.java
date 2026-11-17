package kh.edu.istad.platform.iamapplicationservice.service.admin;

import kh.edu.istad.platform.iamapplicationservice.command.admin.ClientCommand;
import kh.edu.istad.platform.iamapplicationservice.port.out.ClientAdminRepository;
import kh.edu.istad.platform.iamapplicationservice.port.out.PasswordHasher;
import kh.edu.istad.platform.iamapplicationservice.port.out.SystemSettingsRepository;
import kh.edu.istad.platform.iamapplicationservice.result.admin.ClientResult;
import kh.edu.istad.platform.iamapplicationservice.usecase.admin.AdminUseCases;

import java.util.List;
import java.util.Locale;
import java.util.UUID;

/**
 * OAuth2/OIDC client administration. Validation lives here so the adapter only maps.
 */
public final class ClientServices {

    private ClientServices() {
    }

    private static ClientResult toResult(ClientAdminRepository.ClientRecord record) {
        return new ClientResult(
                record.clientId(),
                record.clientName(),
                record.authenticationMethods(),
                record.grantTypes(),
                record.redirectUris(),
                record.postLogoutRedirectUris(),
                record.scopes(),
                record.requireProofKey(),
                record.requireConsent(),
                record.accessTokenTtlSeconds(),
                record.refreshTokenTtlSeconds(),
                record.clientSecretHash() != null
                        && !record.authenticationMethods().contains("none")
        );
    }

    private static void validate(ClientCommand command) {
        if (command == null) {
            throw new IllegalArgumentException("Client command is required");
        }

        if (command.clientId() == null || command.clientId().isBlank()) {
            throw new IllegalArgumentException("client_id is required");
        }

        if (command.clientName() == null || command.clientName().isBlank()) {
            throw new IllegalArgumentException("Client name is required");
        }

        if (command.grantTypes().isEmpty()) {
            throw new IllegalArgumentException("At least one grant type is required");
        }

        if (command.authenticationMethods().isEmpty()) {
            throw new IllegalArgumentException(
                    "At least one authentication method is required"
            );
        }

        boolean publicClient = command.authenticationMethods().stream()
                .anyMatch(method -> "none".equalsIgnoreCase(method));

        for (String redirectUri : command.redirectUris()) {
            if (!redirectUri.startsWith("http://") && !redirectUri.startsWith("https://")) {
                throw new IllegalArgumentException(
                        "Redirect URIs must be absolute http(s) URIs: " + redirectUri
                );
            }
        }

        if (publicClient && !command.requireProofKey()) {
            throw new IllegalArgumentException(
                    "Public clients must require PKCE (code_challenge)"
            );
        }
    }

    public static final class ListClients implements AdminUseCases.ListClients {

        private final ClientAdminRepository repository;

        public ListClients(ClientAdminRepository repository) {
            this.repository = repository;
        }

        @Override
        public List<ClientResult> execute() {
            return repository.findAll().stream()
                    .map(ClientServices::toResult)
                    .toList();
        }
    }

    public static final class GetClient implements AdminUseCases.GetClient {

        private final ClientAdminRepository repository;

        public GetClient(ClientAdminRepository repository) {
            this.repository = repository;
        }

        @Override
        public ClientResult execute(String clientId) {
            return repository.findByClientId(clientId)
                    .map(ClientServices::toResult)
                    .orElseThrow(() -> new IllegalArgumentException("Client not found"));
        }
    }

    public static final class CreateClient implements AdminUseCases.CreateClient {

        private final ClientAdminRepository repository;
        private final PasswordHasher passwordHasher;
        private final SystemSettingsRepository systemSettingsRepository;

        public CreateClient(
                ClientAdminRepository repository,
                PasswordHasher passwordHasher,
                SystemSettingsRepository systemSettingsRepository
        ) {
            this.repository = repository;
            this.passwordHasher = passwordHasher;
            this.systemSettingsRepository = systemSettingsRepository;
        }

        @Override
        public ClientResult execute(ClientCommand command) {
            validate(command);

            if (repository.findByClientId(command.clientId()).isPresent()) {
                throw new IllegalArgumentException("Client already exists");
            }

            boolean publicClient = command.authenticationMethods().stream()
                    .anyMatch(method -> "none".equalsIgnoreCase(method));

            if (!publicClient && (command.clientSecret() == null
                    || command.clientSecret().isBlank())) {
                throw new IllegalArgumentException(
                        "A client secret is required for a confidential client"
                );
            }

            return toResult(repository.save(new ClientAdminRepository.ClientRecord(
                    UUID.randomUUID().toString(),
                    normalise(command.clientId()),
                    command.clientName(),
                    command.authenticationMethods(),
                    command.grantTypes(),
                    command.redirectUris(),
                    command.postLogoutRedirectUris(),
                    command.scopes(),
                    command.requireProofKey(),
                    command.requireConsent(),
                    command.accessTokenTtlSeconds() != null
                            ? command.accessTokenTtlSeconds()
                            : systemSettingsRepository.load().accessTokenTtlMinutes() * 60,
                    command.refreshTokenTtlSeconds() != null
                            ? command.refreshTokenTtlSeconds()
                            : systemSettingsRepository.load().refreshTokenTtlDays() * 86_400,
                    publicClient || command.clientSecret() == null
                            ? null
                            : passwordHasher.hash(command.clientSecret())
            )));
        }
    }

    public static final class UpdateClient implements AdminUseCases.UpdateClient {

        private final ClientAdminRepository repository;
        private final PasswordHasher passwordHasher;

        public UpdateClient(
                ClientAdminRepository repository,
                PasswordHasher passwordHasher
        ) {
            this.repository = repository;
            this.passwordHasher = passwordHasher;
        }

        @Override
        public ClientResult execute(String clientId, ClientCommand command) {
            validate(command);

            ClientAdminRepository.ClientRecord existing = repository
                    .findByClientId(clientId)
                    .orElseThrow(() -> new IllegalArgumentException("Client not found"));

            boolean publicClient = command.authenticationMethods().stream()
                    .anyMatch(method -> "none".equalsIgnoreCase(method));

            String secretHash = publicClient
                    ? null
                    : command.clientSecret() == null || command.clientSecret().isBlank()
                            ? existing.clientSecretHash()
                            : passwordHasher.hash(command.clientSecret());

            return toResult(repository.save(new ClientAdminRepository.ClientRecord(
                    existing.internalId(),
                    existing.clientId(),
                    command.clientName(),
                    command.authenticationMethods(),
                    command.grantTypes(),
                    command.redirectUris(),
                    command.postLogoutRedirectUris(),
                    command.scopes(),
                    command.requireProofKey(),
                    command.requireConsent(),
                    command.accessTokenTtlSeconds() == null
                            ? existing.accessTokenTtlSeconds()
                            : command.accessTokenTtlSeconds(),
                    command.refreshTokenTtlSeconds() == null
                            ? existing.refreshTokenTtlSeconds()
                            : command.refreshTokenTtlSeconds(),
                    secretHash
            )));
        }
    }

    public static final class DeleteClient implements AdminUseCases.DeleteClient {

        private final ClientAdminRepository repository;

        public DeleteClient(ClientAdminRepository repository) {
            this.repository = repository;
        }

        @Override
        public void execute(String clientId) {
            if (repository.findByClientId(clientId).isEmpty()) {
                throw new IllegalArgumentException("Client not found");
            }

            repository.delete(clientId);
        }
    }

    private static String normalise(String clientId) {
        return clientId.trim().toLowerCase(Locale.ROOT);
    }
}

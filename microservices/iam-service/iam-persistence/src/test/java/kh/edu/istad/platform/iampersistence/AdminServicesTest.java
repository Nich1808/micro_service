package kh.edu.istad.platform.iampersistence;

import kh.edu.istad.platform.iamapplicationservice.command.admin.ClientCommand;
import kh.edu.istad.platform.iamapplicationservice.command.admin.UpdateSystemSettingsCommand;
import kh.edu.istad.platform.iamapplicationservice.port.out.ClientAdminRepository;
import kh.edu.istad.platform.iamapplicationservice.port.out.PasswordHasher;
import kh.edu.istad.platform.iamapplicationservice.port.out.SystemSettingsRepository;
import kh.edu.istad.platform.iamapplicationservice.service.admin.ClientServices;
import kh.edu.istad.platform.iamapplicationservice.service.admin.SystemSettingsServices;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Rules enforced by the administration services: client validation, secret handling and
 * the security policy bounds.
 */
class AdminServicesTest {

    private static final PasswordHasher HASHER = new PasswordHasher() {
        @Override
        public String hash(String rawPassword) {
            return "hash(" + rawPassword + ")";
        }

        @Override
        public boolean matches(String rawPassword, String encodedPassword) {
            return encodedPassword.equals(hash(rawPassword));
        }
    };

    // ------------------------------------------------------------------
    // Clients
    // ------------------------------------------------------------------

    @Test
    void createClientHashesTheSecret() {
        FakeClientRepo repo = new FakeClientRepo();

        ClientServices.CreateClient service =
                new ClientServices.CreateClient(repo, HASHER, new FakeSettingsRepo());

        service.execute(command(
                "app",
                List.of("client_secret_basic"),
                List.of("authorization_code"),
                "secret123"
        ));

        ClientAdminRepository.ClientRecord saved = repo.findByClientId("app").orElseThrow();

        assertEquals("hash(secret123)", saved.clientSecretHash());
    }

    @Test
    void publicClientRejectsRequestWithoutPkce() {
        FakeClientRepo repo = new FakeClientRepo();
        ClientServices.CreateClient service =
                new ClientServices.CreateClient(repo, HASHER, new FakeSettingsRepo());

        ClientCommand command = new ClientCommand(
                "spa", "SPA", null,
                List.of("none"),
                List.of("authorization_code"),
                List.of("http://localhost:5173/callback"),
                List.of(),
                List.of("openid"),
                false, true, null, null
        );

        Exception thrown = assertThrows(
                IllegalArgumentException.class,
                () -> service.execute(command)
        );

        assertTrue(thrown.getMessage().contains("PKCE"));
    }

    @Test
    void confidentialClientRequiresASecretOnCreate() {
        FakeClientRepo repo = new FakeClientRepo();
        ClientServices.CreateClient service =
                new ClientServices.CreateClient(repo, HASHER, new FakeSettingsRepo());

        Exception thrown = assertThrows(
                IllegalArgumentException.class,
                () -> service.execute(command(
                        "confidential",
                        List.of("client_secret_basic"),
                        List.of("authorization_code"),
                        null
                ))
        );

        assertTrue(thrown.getMessage().contains("secret"));
    }

    @Test
    void redirectUriMustBeAbsolute() {
        FakeClientRepo repo = new FakeClientRepo();
        ClientServices.CreateClient service =
                new ClientServices.CreateClient(repo, HASHER, new FakeSettingsRepo());

        Exception thrown = assertThrows(
                IllegalArgumentException.class,
                () -> service.execute(new ClientCommand(
                        "bad", "Bad", "s3cret",
                        List.of("client_secret_basic"),
                        List.of("authorization_code"),
                        List.of("/relative/callback"),
                        List.of(),
                        List.of("openid"),
                        true, true, null, null
                ))
        );

        assertTrue(thrown.getMessage().contains("http"));
    }

    @Test
    void updateWithoutSecretKeepsTheStoredHash() {
        FakeClientRepo repo = new FakeClientRepo();
        ClientServices.CreateClient create =
                new ClientServices.CreateClient(repo, HASHER, new FakeSettingsRepo());
        create.execute(command(
                "app",
                List.of("client_secret_basic"),
                List.of("authorization_code"),
                "original"
        ));

        ClientServices.UpdateClient update =
                new ClientServices.UpdateClient(repo, HASHER);

        update.execute("app", new ClientCommand(
                "app", "Renamed app", null,
                List.of("client_secret_basic"),
                List.of("authorization_code"),
                List.of("https://app.example.com/cb"),
                List.of(),
                List.of("openid", "profile"),
                true, true, null, null
        ));

        ClientAdminRepository.ClientRecord saved = repo.findByClientId("app").orElseThrow();

        assertEquals("Renamed app", saved.clientName());
        assertEquals("hash(original)", saved.clientSecretHash());
        assertEquals(List.of("openid", "profile"), saved.scopes());
    }

    @Test
    void duplicateClientIdIsRejected() {
        FakeClientRepo repo = new FakeClientRepo();
        ClientServices.CreateClient service =
                new ClientServices.CreateClient(repo, HASHER, new FakeSettingsRepo());

        service.execute(command("app", List.of("client_secret_basic"),
                List.of("authorization_code"), "s3cret"));

        assertThrows(IllegalArgumentException.class, () ->
                service.execute(command("app", List.of("client_secret_basic"),
                        List.of("authorization_code"), "other")));
    }

    @Test
    void deletingAnUnknownClientFails() {
        FakeClientRepo repo = new FakeClientRepo();
        ClientServices.DeleteClient service = new ClientServices.DeleteClient(repo);

        assertThrows(IllegalArgumentException.class, () -> service.execute("ghost"));
    }

    // ------------------------------------------------------------------
    // System settings
    // ------------------------------------------------------------------

    @Test
    void updateAppliesOnlyTheProvidedFields() {
        FakeSettingsRepo repo = new FakeSettingsRepo();
        SystemSettingsServices.Update service = new SystemSettingsServices.Update(repo);

        SystemSettingsRepository.SystemSettings updated = service.execute(
                new UpdateSystemSettingsCommand(
                        10, null, null, null,
                        null, null, null, null,
                        null, null, null, null
                )
        );

        assertEquals(10, updated.passwordMinLength());
        assertEquals(5, updated.maxFailedLoginAttempts());
        assertEquals(60, updated.sessionIdleTimeoutMinutes());
        assertTrue(updated.registrationEnabled());
    }

    @Test
    void negativeValuesAreRejected() {
        FakeSettingsRepo repo = new FakeSettingsRepo();
        SystemSettingsServices.Update service = new SystemSettingsServices.Update(repo);

        assertThrows(IllegalArgumentException.class, () ->
                service.execute(new UpdateSystemSettingsCommand(
                        0, null, null, null, null, null, null, null,
                        null, null, null, null
                )));

        assertThrows(IllegalArgumentException.class, () ->
                service.execute(new UpdateSystemSettingsCommand(
                        null, null, null, null, -1, null, null, null,
                        null, null, null, null
                )));
    }

    @Test
    void defaultsAreSane() {
        SystemSettingsRepository.SystemSettings defaults =
                SystemSettingsRepository.SystemSettings.defaults();

        assertEquals(8, defaults.passwordMinLength());
        assertEquals(5, defaults.maxFailedLoginAttempts());
        assertTrue(defaults.registrationEnabled());
        assertNotNull(defaults);
    }

    // ------------------------------------------------------------------
    // Fixtures
    // ------------------------------------------------------------------

    private static ClientCommand command(
            String clientId,
            List<String> methods,
            List<String> grants,
            String secret
    ) {
        return new ClientCommand(
                clientId, "App " + clientId, secret,
                methods, grants,
                List.of("https://app.example.com/callback"),
                List.of(),
                List.of("openid", "profile"),
                true, true, 3600L, 604_800L
        );
    }

    private static final class FakeClientRepo implements ClientAdminRepository {

        private final List<ClientRecord> records = new ArrayList<>();

        @Override
        public List<ClientRecord> findAll() {
            return List.copyOf(records);
        }

        @Override
        public Optional<ClientRecord> findByClientId(String clientId) {
            return records.stream()
                    .filter(record -> record.clientId().equals(clientId))
                    .findFirst();
        }

        @Override
        public ClientRecord save(ClientRecord client) {
            records.removeIf(record -> record.clientId().equals(client.clientId()));
            records.add(client);
            return client;
        }

        @Override
        public void delete(String clientId) {
            records.removeIf(record -> record.clientId().equals(clientId));
        }
    }

    private static final class FakeSettingsRepo implements SystemSettingsRepository {

        private SystemSettings current = SystemSettings.defaults();

        @Override
        public SystemSettings load() {
            return current;
        }

        @Override
        public SystemSettings save(SystemSettings settings) {
            current = settings;
            return settings;
        }
    }
}

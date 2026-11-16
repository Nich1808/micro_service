package kh.edu.istad.platform.iaminfrastructure.oauth;

import kh.edu.istad.platform.iamapplicationservice.port.out.ClientRegistryQuery;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Optional;

@Component
public class ClientRegistryQueryAdapter implements ClientRegistryQuery {

    private final RegisteredClientRepository registeredClientRepository;

    public ClientRegistryQueryAdapter(
            RegisteredClientRepository registeredClientRepository
    ) {
        this.registeredClientRepository = registeredClientRepository;
    }

    @Override
    public Optional<ClientDetails> findByClientId(String clientId) {
        if (clientId == null || clientId.isBlank()) {
            return Optional.empty();
        }

        RegisteredClient client =
                registeredClientRepository.findByClientId(clientId);

        if (client == null) {
            return Optional.empty();
        }

        return Optional.of(new ClientDetails(
                client.getClientId(),
                client.getClientName(),
                new ArrayList<>(client.getScopes())
        ));
    }
}

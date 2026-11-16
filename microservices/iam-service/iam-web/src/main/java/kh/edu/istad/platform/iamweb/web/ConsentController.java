package kh.edu.istad.platform.iamweb.web;

import kh.edu.istad.platform.iamapplicationservice.port.out.ClientRegistryQuery;
import kh.edu.istad.platform.iamweb.i18n.Messages;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Renders the authorization consent screen. Approval is submitted straight to the
 * authorization endpoint: selected {@code scope} values mean "allow", no scope means
 * "cancel".
 *
 * <p>A consent request is only meaningful when it carries the {@code client_id} and
 * {@code state} issued by the authorization endpoint, so malformed requests are answered
 * with 400 instead of a server error, and an unknown client or an empty scope list is
 * never presented as something the user can approve.</p>
 */
@Controller
public class ConsentController {

    private final ClientRegistryQuery clientRegistryQuery;
    private final Messages messages;

    public ConsentController(
            ClientRegistryQuery clientRegistryQuery,
            Messages messages
    ) {
        this.clientRegistryQuery = clientRegistryQuery;
        this.messages = messages;
    }

    @GetMapping("/oauth2/consent")
    public String consent(
            @RequestParam(name = "client_id", required = false) String clientId,
            @RequestParam(name = "state", required = false) String state,
            @RequestParam(name = "scope", required = false) List<String> scopes,
            Model model
    ) {
        if (clientId == null || clientId.isBlank() || state == null || state.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "The consent request is missing client_id or state"
            );
        }

        ClientRegistryQuery.ClientDetails client = clientRegistryQuery
                .findByClientId(clientId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Unknown client: " + clientId
                ));

        List<String> requestedScopes = splitScopes(scopes);

        if (requestedScopes.isEmpty()) {
            // Without at least one scope the "Allow" button would submit nothing, which
            // the authorization endpoint treats as a denial. Fall back to the scopes the
            // client is registered for.
            requestedScopes = client.scopes();
        }

        if (requestedScopes.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "The client is not allowed to request any scope"
            );
        }

        String clientName = client.clientName();

        model.addAttribute("messages", messages);
        model.addAttribute("clientId", client.clientId());
        model.addAttribute("clientName",
                clientName == null || clientName.isBlank()
                        ? client.clientId()
                        : clientName);
        model.addAttribute("state", state);
        model.addAttribute("scopes", requestedScopes);

        return "consent";
    }

    /**
     * The authorization endpoint sends the requested scopes as a single space-delimited
     * value, while each consent checkbox submits exactly one scope.
     */
    private List<String> splitScopes(List<String> scopes) {
        Set<String> merged = new LinkedHashSet<>();

        if (scopes != null) {
            for (String requested : scopes) {
                if (requested == null) {
                    continue;
                }

                for (String candidate : requested.trim().split("\\s+")) {
                    String scope = candidate.trim();

                    if (!scope.isEmpty()) {
                        merged.add(scope);
                    }
                }
            }
        }

        return new ArrayList<>(merged);
    }
}

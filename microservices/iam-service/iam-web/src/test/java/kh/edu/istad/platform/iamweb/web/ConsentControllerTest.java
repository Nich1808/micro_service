package kh.edu.istad.platform.iamweb.web;

import kh.edu.istad.platform.iamapplicationservice.port.out.ClientRegistryQuery;
import kh.edu.istad.platform.iamweb.i18n.Messages;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Guards the consent page contract: malformed requests are 400 (not 500), unknown clients
 * are rejected, and space-delimited scopes are split into one entry each.
 */
class ConsentControllerTest {

    private static final String STATE = "consent-state-abc";
    private static final String CLIENT_ID = "oidc-client";

    private ClientRegistryQuery clientRegistryQuery;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        clientRegistryQuery = mock(ClientRegistryQuery.class);

        ResourceBundleMessageSource source = new ResourceBundleMessageSource();
        source.setBasename("messages");
        source.setDefaultEncoding(StandardCharsets.UTF_8.name());
        source.setFallbackToSystemLocale(false);

        Messages messages = new Messages(source);

        mockMvc = MockMvcBuilders
                .standaloneSetup(new ConsentController(clientRegistryQuery, messages))
                // The JTE engine is exercised separately; here only the model matters, so
                // the view is rendered as a no-op instead of being dispatched again.
                .setViewResolvers((viewName, locale) -> (model, request, response) -> {
                })
                .build();
    }

    @Test
    void missingClientIdIsBadRequest() throws Exception {
        mockMvc.perform(get("/oauth2/consent").param("state", STATE))
                .andExpect(status().isBadRequest());
    }

    @Test
    void blankStateIsBadRequest() throws Exception {
        mockMvc.perform(get("/oauth2/consent")
                        .param("client_id", CLIENT_ID)
                        .param("state", "  "))
                .andExpect(status().isBadRequest());
    }

    @Test
    void unknownClientIsBadRequest() throws Exception {
        when(clientRegistryQuery.findByClientId("ghost-client"))
                .thenReturn(Optional.empty());

        mockMvc.perform(get("/oauth2/consent")
                        .param("client_id", "ghost-client")
                        .param("state", STATE)
                        .param("scope", "openid"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void spaceDelimitedScopesAreSplit() throws Exception {
        givenClient(List.of("openid", "profile", "email"));

        mockMvc.perform(get("/oauth2/consent")
                        .param("client_id", CLIENT_ID)
                        .param("state", STATE)
                        .param("scope", "openid profile email"))
                .andExpect(status().isOk())
                .andExpect(model().attribute(
                        "scopes", List.of("openid", "profile", "email")))
                .andExpect(model().attribute("state", STATE))
                .andExpect(model().attribute("clientName", "Demonic Web"));
    }

    @Test
    void repeatedScopeParametersAreMergedAndDeduplicated() throws Exception {
        givenClient(List.of("openid", "profile", "email"));

        mockMvc.perform(get("/oauth2/consent")
                        .param("client_id", CLIENT_ID)
                        .param("state", STATE)
                        .param("scope", "openid profile")
                        .param("scope", "email openid"))
                .andExpect(status().isOk())
                .andExpect(model().attribute(
                        "scopes", List.of("openid", "profile", "email")));
    }

    @Test
    void emptyScopeFallsBackToRegisteredClientScopes() throws Exception {
        givenClient(List.of("openid", "profile"));

        mockMvc.perform(get("/oauth2/consent")
                        .param("client_id", CLIENT_ID)
                        .param("state", STATE))
                .andExpect(status().isOk())
                .andExpect(model().attribute("scopes", List.of("openid", "profile")));
    }

    @Test
    void clientWithoutAnyScopeIsRejected() throws Exception {
        givenClient(List.of());

        mockMvc.perform(get("/oauth2/consent")
                        .param("client_id", CLIENT_ID)
                        .param("state", STATE))
                .andExpect(status().isBadRequest());
    }

    private void givenClient(List<String> registeredScopes) {
        when(clientRegistryQuery.findByClientId(CLIENT_ID))
                .thenReturn(Optional.of(new ClientRegistryQuery.ClientDetails(
                        CLIENT_ID,
                        "Demonic Web",
                        registeredScopes
                )));
    }
}

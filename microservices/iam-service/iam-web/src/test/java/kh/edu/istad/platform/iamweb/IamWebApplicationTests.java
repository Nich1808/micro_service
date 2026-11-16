package kh.edu.istad.platform.iamweb;

import gg.jte.ContentType;
import gg.jte.TemplateEngine;
import gg.jte.output.StringOutput;
import gg.jte.resolve.ResourceCodeResolver;
import kh.edu.istad.platform.iamweb.i18n.Messages;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.security.web.csrf.CsrfToken;

import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Renders the JTE pages outside a servlet container: this proves the templates
 * compile and that both language bundles are wired.
 */
class IamWebApplicationTests {

    private static TemplateEngine engine;
    private static Messages messages;

    @BeforeAll
    static void setUp() {
        engine = TemplateEngine.create(
                new ResourceCodeResolver("jte"),
                ContentType.Html
        );

        ResourceBundleMessageSource source = new ResourceBundleMessageSource();
        source.setBasename("messages");
        source.setDefaultEncoding(StandardCharsets.UTF_8.name());
        source.setFallbackToSystemLocale(false);

        messages = new Messages(source);
    }

    @AfterEach
    void resetLocale() {
        LocaleContextHolder.resetLocaleContext();
    }

    @Test
    void loginPageRendersInEnglish() {
        LocaleContextHolder.setLocale(Locale.forLanguageTag("en"));

        String html = render("login.jte", Map.of(
                "messages", messages,
                "providers", List.of("google", "github"),
                "error", false,
                "logout", false,
                "registered", false,
                "path", "/login"
        ));

        assertTrue(html.contains("Sign in"), html);
        assertTrue(html.contains("/oauth2/authorization/google"), html);
        assertFalse(html.contains("login.title"), "Bundle key leaked into markup");
    }

    @Test
    void loginPageRendersInKhmer() {
        LocaleContextHolder.setLocale(Locale.forLanguageTag("km"));

        String html = render("login.jte", Map.of(
                "messages", messages,
                "providers", List.of("google"),
                "error", false,
                "logout", false,
                "registered", false,
                "path", "/login"
        ));

        assertTrue(html.contains("ចូលគណនី"), html);
        assertTrue(html.contains("lang=\"km\""), html);
    }

    @Test
    void consentPageListsRequestedScopes() {
        LocaleContextHolder.setLocale(Locale.forLanguageTag("en"));

        String html = render("consent.jte", Map.of(
                "messages", messages,
                "clientId", "istad-web",
                "clientName", "ISTAD E-Commerce Web",
                "state", "state-123",
                "scopes", List.of("openid", "profile", "email")
        ));

        assertTrue(html.contains("ISTAD E-Commerce Web"), html);
        assertTrue(html.contains("value=\"profile\""), html);
        assertTrue(html.contains("action=\"/oauth2/authorize\""), html);
    }

    @Test
    void passwordPageRenders() {
        LocaleContextHolder.setLocale(Locale.forLanguageTag("en"));

        java.util.Map<String, Object> params = new LinkedHashMap<>();
        params.put("messages", messages);
        params.put("changed", false);
        params.put("error", null);

        String html = render("profile/password.jte", params);

        assertTrue(html.contains("Change password"), html);
    }

    private String render(String template, Map<String, Object> params) {
        Map<String, Object> model = new LinkedHashMap<>();
        model.put("csrfToken", new StubCsrfToken());
        model.putAll(params);

        StringOutput output = new StringOutput();
        engine.render(template, model, output);

        return output.toString();
    }

    /**
     * Minimal stand-in so templates that emit a CSRF field can be rendered offline.
     */
    static final class StubCsrfToken implements CsrfToken {

        @Override
        public String getHeaderName() {
            return "X-CSRF-TOKEN";
        }

        @Override
        public String getParameterName() {
            return "_csrf";
        }

        @Override
        public String getToken() {
            return "stub-token";
        }
    }
}

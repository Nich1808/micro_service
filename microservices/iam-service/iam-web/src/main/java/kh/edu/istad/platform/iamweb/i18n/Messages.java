package kh.edu.istad.platform.iamweb.i18n;

import org.springframework.context.MessageSource;

import java.util.Locale;
import org.springframework.context.i18n.LocaleContextHolder;

/**
 * Locale-aware lookup used by the JTE templates, so no text is hard-coded in a
 * template.
 */
public class Messages {

    private final MessageSource messageSource;

    public Messages(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    public String get(String key) {
        return messageSource.getMessage(
                key,
                null,
                key,
                LocaleContextHolder.getLocale()
        );
    }

    public String get(String key, Object... args) {
        return messageSource.getMessage(
                key,
                args,
                key,
                LocaleContextHolder.getLocale()
        );
    }

    /**
     * Human-readable label for an OAuth scope, falling back to a generic message for
     * scopes that have no translation.
     */
    public String scopeLabel(String scope) {
        String key = "consent.scope." + scope;
        String value = get(key);

        return key.equals(value) ? get("consent.scope.default", scope) : value;
    }

    /**
     * Display name for a social provider: "google" becomes "Google".
     */
    public String providerLabel(String provider) {
        if (provider == null || provider.isBlank()) {
            return "";
        }

        String normalised = provider.toLowerCase(Locale.ROOT);

        // Brand spellings that differ from plain capitalisation.
        return switch (normalised) {
            case "github" -> "GitHub";
            case "linkedin" -> "LinkedIn";
            case "youtube" -> "YouTube";
            default -> normalised.substring(0, 1).toUpperCase(Locale.ROOT)
                    + normalised.substring(1);
        };
    }

    public String language() {
        return LocaleContextHolder.getLocale().getLanguage();
    }

    public boolean isEnglish() {
        return "en".equals(language());
    }

    public boolean isKhmer() {
        return "km".equals(language());
    }

    public String otherLanguage() {
        return isEnglish() ? "km" : "en";
    }
}

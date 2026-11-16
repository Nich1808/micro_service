package kh.edu.istad.platform.iamweb.i18n;

import org.springframework.context.MessageSource;
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

package kh.edu.istad.platform.iamweb.view;

import gg.jte.TemplateEngine;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.ViewResolver;

import java.util.Locale;

/**
 * Resolves controller view names ("login", "profile/edit", ...) to JTE templates.
 */
public class JteViewResolver implements ViewResolver {

    private static final String SUFFIX = ".jte";

    private final TemplateEngine templateEngine;

    public JteViewResolver(TemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }

    @Override
    public View resolveViewName(String viewName, Locale locale) {
        if (viewName == null || viewName.isBlank()) {
            return null;
        }

        return new JteView(viewName + SUFFIX, templateEngine);
    }
}

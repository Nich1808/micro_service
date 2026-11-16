package kh.edu.istad.platform.iamweb.view;

import gg.jte.ContentType;
import gg.jte.TemplateEngine;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Serves the JTE pages. Templates are compiled during the build (see the
 * {@code jte { generate() }} block), which keeps them working inside the executable
 * jar instead of relying on runtime compilation.
 */
@Configuration
public class JteConfig {

    @Bean
    public TemplateEngine jteTemplateEngine() {
        return TemplateEngine.createPrecompiled(ContentType.Html);
    }

    @Bean
    public JteViewResolver jteViewResolver(TemplateEngine jteTemplateEngine) {
        return new JteViewResolver(jteTemplateEngine);
    }
}

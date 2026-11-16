package kh.edu.istad.platform.iamweb.web;

import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.stereotype.Component;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Lists the social providers that actually have a client id configured, so the pages
 * only render buttons that can work.
 */
@Component
public class
SocialProviderCatalog {

    private static final String PREFIX = "spring.security.oauth2.client.registration.";

    private final ConfigurableEnvironment environment;

    public SocialProviderCatalog(ConfigurableEnvironment environment) {
        this.environment = environment;
    }

    public List<String> all() {
        Set<String> providers = new LinkedHashSet<>();

        for (PropertySource<?> source : environment.getPropertySources()) {
            if (!(source instanceof EnumerablePropertySource<?> enumerable)) {
                continue;
            }

            for (String name : enumerable.getPropertyNames()) {
                if (!name.startsWith(PREFIX)) {
                    continue;
                }

                String remainder = name.substring(PREFIX.length());

                if (!remainder.contains(".")) {
                    continue;
                }

                String id = remainder.substring(0, remainder.indexOf('.'));

                if (environment.getProperty(PREFIX + id + ".client-id") != null) {
                    providers.add(id);
                }
            }
        }

        return List.copyOf(providers);
    }

    public List<String> excluding(List<String> alreadyLinked) {
        return all().stream()
                .filter(provider -> !alreadyLinked.contains(provider))
                .toList();
    }
}

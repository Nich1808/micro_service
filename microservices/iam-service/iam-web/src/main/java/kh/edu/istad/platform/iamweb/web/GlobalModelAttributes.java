package kh.edu.istad.platform.iamweb.web;

import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

/**
 * Exposes the CSRF token to every JTE template so the plain HTML forms can post back.
 * The token is injected by Spring Security's CsrfToken argument resolver, which also
 * triggers the deferred token to be loaded.
 */
@ControllerAdvice
public class GlobalModelAttributes {

    @ModelAttribute("csrfToken")
    public CsrfToken csrfToken(CsrfToken csrfToken) {
        return csrfToken;
    }
}

package kh.edu.istad.platform.iamweb.web;

import jakarta.servlet.http.HttpServletRequest;
import kh.edu.istad.platform.iamweb.i18n.Messages;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LoginController {

    private final Messages messages;
    private final SocialProviderCatalog socialProviderCatalog;

    public LoginController(
            Messages messages,
            SocialProviderCatalog socialProviderCatalog
    ) {
        this.messages = messages;
        this.socialProviderCatalog = socialProviderCatalog;
    }

    @GetMapping("/login")
    public String login(
            @RequestParam(required = false) String error,
            @RequestParam(required = false) String logout,
            @RequestParam(required = false) String registered,
            HttpServletRequest request,
            Model model
    ) {
        model.addAttribute("messages", messages);
        model.addAttribute("providers", socialProviderCatalog.all());
        model.addAttribute("error", error != null);
        model.addAttribute("logout", logout != null);
        model.addAttribute("registered", registered != null);
        model.addAttribute("path", request.getRequestURI());

        return "login";
    }
}

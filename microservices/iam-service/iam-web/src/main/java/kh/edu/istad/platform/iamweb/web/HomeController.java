package kh.edu.istad.platform.iamweb.web;

import kh.edu.istad.platform.iamapplicationservice.result.user.UserAuthoritiesResult;
import kh.edu.istad.platform.iamapplicationservice.usecase.user.GetUserAuthoritiesUseCase;
import kh.edu.istad.platform.iamweb.i18n.Messages;
import kh.edu.istad.platform.iamdomaincore.model.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class HomeController {

    private final CurrentUser currentUser;
    private final GetUserAuthoritiesUseCase getUserAuthoritiesUseCase;
    private final Messages messages;

    public HomeController(
            CurrentUser currentUser,
            GetUserAuthoritiesUseCase getUserAuthoritiesUseCase,
            Messages messages
    ) {
        this.currentUser = currentUser;
        this.getUserAuthoritiesUseCase = getUserAuthoritiesUseCase;
        this.messages = messages;
    }

    @GetMapping("/")
    public String index(Model model) {
        User user = currentUser.require();
        UserAuthoritiesResult authorities =
                getUserAuthoritiesUseCase.execute(user.uuid());

        List<String> roles = authorities.roles().stream()
                .map(UserAuthoritiesResult.RoleAuthority::name)
                .sorted()
                .toList();

        model.addAttribute("messages", messages);
        model.addAttribute("user", user);
        model.addAttribute("roles", roles);
        model.addAttribute("permissions", authorities.effectivePermissions());

        return "index";
    }
}

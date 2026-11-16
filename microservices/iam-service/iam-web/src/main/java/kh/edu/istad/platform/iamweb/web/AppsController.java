package kh.edu.istad.platform.iamweb.web;

import kh.edu.istad.platform.iamapplicationservice.port.out.AuthorizedApplicationQuery;
import kh.edu.istad.platform.iamweb.i18n.Messages;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/profile/apps")
public class AppsController {

    private final CurrentUser currentUser;
    private final AuthorizedApplicationQuery authorizedApplicationQuery;
    private final Messages messages;

    public AppsController(
            CurrentUser currentUser,
            AuthorizedApplicationQuery authorizedApplicationQuery,
            Messages messages
    ) {
        this.currentUser = currentUser;
        this.authorizedApplicationQuery = authorizedApplicationQuery;
        this.messages = messages;
    }

    @GetMapping
    public String apps(
            @RequestParam(required = false) String revoked,
            Model model
    ) {
        model.addAttribute("messages", messages);
        model.addAttribute("applications", authorizedApplicationQuery
                .findAllByPrincipal(currentUser.require().username()));
        model.addAttribute("revoked", revoked);

        return "profile/connected-apps";
    }

    @PostMapping("/{clientId}/revoke")
    public String revoke(@PathVariable String clientId) {
        authorizedApplicationQuery.revoke(clientId, currentUser.require().username());
        return "redirect:/profile/apps?revoked=" + clientId;
    }
}

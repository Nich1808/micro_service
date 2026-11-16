package kh.edu.istad.platform.iamweb.web;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import kh.edu.istad.platform.iamapplicationservice.result.identity.IdentityResult;
import kh.edu.istad.platform.iamapplicationservice.usecase.identity.ListUserIdentitiesUseCase;
import kh.edu.istad.platform.iamapplicationservice.usecase.identity.UnlinkIdentityUseCase;
import kh.edu.istad.platform.iamweb.i18n.Messages;
import kh.edu.istad.platform.iamdomaincore.model.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/profile/accounts")
public class AccountsController {

    private static final String LINK_UUID = "IAM_LINK_UUID";
    private static final String LINK_PROVIDER = "IAM_LINK_PROVIDER";
    private static final String ORIGINAL_AUTH = "IAM_LINK_ORIGINAL_AUTH";

    private final CurrentUser currentUser;
    private final ListUserIdentitiesUseCase listUserIdentitiesUseCase;
    private final UnlinkIdentityUseCase unlinkIdentityUseCase;
    private final Messages messages;
    private final SocialProviderCatalog socialProviderCatalog;

    public AccountsController(
            CurrentUser currentUser,
            ListUserIdentitiesUseCase listUserIdentitiesUseCase,
            UnlinkIdentityUseCase unlinkIdentityUseCase,
            Messages messages,
            SocialProviderCatalog socialProviderCatalog
    ) {
        this.currentUser = currentUser;
        this.listUserIdentitiesUseCase = listUserIdentitiesUseCase;
        this.unlinkIdentityUseCase = unlinkIdentityUseCase;
        this.messages = messages;
        this.socialProviderCatalog = socialProviderCatalog;
    }

    @GetMapping
    public String accounts(
            @RequestParam(required = false) String linked,
            @RequestParam(required = false) String unlinked,
            @RequestParam(required = false) String error,
            Model model
    ) {
        User user = currentUser.require();

        List<IdentityResult> identities = listUserIdentitiesUseCase.execute(user.uuid());

        List<String> linkedProviders = identities.stream()
                .map(identity -> identity.provider().name().toLowerCase())
                .toList();

        model.addAttribute("messages", messages);
        model.addAttribute("identities", identities);
        model.addAttribute("linkedProviders", linkedProviders);
        model.addAttribute("providers", socialProviderCatalog.excluding(linkedProviders));
        model.addAttribute("hasPassword", user.passwordHash() != null
                && !user.passwordHash().isBlank());
        model.addAttribute("linked", linked);
        model.addAttribute("unlinked", unlinked != null);
        model.addAttribute("error", error);

        return "profile/linked-accounts";
    }

    /**
     * Starts the link flow: remember who is linking, then hand over to the provider.
     */
    @GetMapping("/link/{provider}")
    public String link(
            @PathVariable String provider,
            HttpServletRequest request
    ) {
        User user = currentUser.require();

        HttpSession session = request.getSession(true);
        session.setAttribute(LINK_UUID, user.uuid().toString());
        session.setAttribute(LINK_PROVIDER, provider);
        session.setAttribute(ORIGINAL_AUTH, currentUser.authentication());

        return "redirect:/oauth2/authorization/" + provider;
    }

    @PostMapping("/{identityUuid}/unlink")
    public String unlink(@PathVariable UUID identityUuid) {
        unlinkIdentityUseCase.execute(currentUser.require().uuid(), identityUuid);
        return "redirect:/profile/accounts?unlinked";
    }

}

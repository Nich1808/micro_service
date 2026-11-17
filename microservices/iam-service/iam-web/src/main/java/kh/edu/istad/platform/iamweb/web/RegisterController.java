package kh.edu.istad.platform.iamweb.web;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import kh.edu.istad.platform.iamapplicationservice.command.user.RegisterUserCommand;
import kh.edu.istad.platform.iamapplicationservice.usecase.user.RegisterUserUseCase;
import kh.edu.istad.platform.iamweb.i18n.Messages;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.LinkedHashMap;
import java.util.Map;

@Controller
public class RegisterController {

    private final RegisterUserUseCase registerUserUseCase;
    private final Messages messages;

    public RegisterController(
            RegisterUserUseCase registerUserUseCase,
            Messages messages
    ) {
        this.registerUserUseCase = registerUserUseCase;
        this.messages = messages;
    }

    @GetMapping("/register")
    public String form(Model model) {
        if (!model.containsAttribute("form")) {
            model.addAttribute("form", new RegisterForm("", "", "", "", "", "", "en"));
        }

        model.addAttribute("messages", messages);
        model.addAttribute("errors", Map.of());

        return "register";
    }

    @PostMapping("/register")
    public String submit(
            @Valid @ModelAttribute("form") RegisterForm form,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        Map<String, String> errors = new LinkedHashMap<>();

        bindingResult.getFieldErrors().forEach(error ->
                errors.putIfAbsent(error.getField(), fieldMessage(error)));

        if (!form.password().equals(form.confirmPassword())) {
            errors.put("confirmPassword", messages.get("register.passwordMismatch"));
        }

        if (errors.isEmpty()) {
            try {
                registerUserUseCase.execute(new RegisterUserCommand(
                        form.username(),
                        form.email(),
                        form.password(),
                        form.givenName(),
                        form.familyName(),
                        form.locale()
                ));

                return "redirect:/login?registered";
            } catch (IllegalArgumentException | IllegalStateException ex) {
                errors.put("username", ex.getMessage());
            }
        }

        model.addAttribute("messages", messages);
        model.addAttribute("errors", errors);

        return "register";
    }

    private String fieldMessage(FieldError error) {
        String code = error.getField() + ".invalid";

        String translated = messages.get(code);

        return translated.equals(code) ? error.getDefaultMessage() : translated;
    }

    public record RegisterForm(
            @NotBlank
            @Size(max = 50)
            String username,

            @NotBlank
            @Email
            @Size(max = 255)
            String email,

            @NotBlank
            @Size(min = 8, max = 100)
            String password,

            @NotBlank
            String confirmPassword,

            @NotBlank
            @Size(max = 100)
            String givenName,

            @NotBlank
            @Size(max = 100)
            String familyName,

            String locale
    ) {
    }
}

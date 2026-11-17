package kh.edu.istad.platform.iamweb.web;

import jakarta.validation.Valid;
import kh.edu.istad.platform.iamapplicationservice.command.user.ChangePasswordCommand;
import kh.edu.istad.platform.iamapplicationservice.command.user.UpdateUserProfileCommand;
import kh.edu.istad.platform.iamapplicationservice.usecase.user.ChangePasswordUseCase;
import kh.edu.istad.platform.iamapplicationservice.usecase.user.UpdateUserProfileUseCase;
import kh.edu.istad.platform.iamweb.i18n.Messages;
import kh.edu.istad.platform.iamdomaincore.model.User;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDate;

@Controller
@RequestMapping("/profile")
public class ProfileController {

    private final CurrentUser currentUser;
    private final UpdateUserProfileUseCase updateUserProfileUseCase;
    private final ChangePasswordUseCase changePasswordUseCase;
    private final Messages messages;

    public ProfileController(
            CurrentUser currentUser,
            UpdateUserProfileUseCase updateUserProfileUseCase,
            ChangePasswordUseCase changePasswordUseCase,
            Messages messages
    ) {
        this.currentUser = currentUser;
        this.updateUserProfileUseCase = updateUserProfileUseCase;
        this.changePasswordUseCase = changePasswordUseCase;
        this.messages = messages;
    }

    @GetMapping("/edit")
    public String editForm(Model model) {
        User user = currentUser.require();

        model.addAttribute("messages", messages);
        model.addAttribute("form", ProfileForm.from(user));
        model.addAttribute("saved", false);
        model.addAttribute("error", null);

        return "profile/edit";
    }

    @PostMapping("/edit")
    public String edit(
            @Valid @ModelAttribute("form") ProfileForm form,
            BindingResult bindingResult,
            Model model
    ) {
        User user = currentUser.require();
        String error = null;

        if (!bindingResult.hasErrors()) {
            try {
                updateUserProfileUseCase.execute(new UpdateUserProfileCommand(
                        user.uuid(),
                        null,
                        null,
                        form.givenName(),
                        form.familyName(),
                        form.middleName(),
                        form.nickname(),
                        form.gender(),
                        form.birthdate(),
                        form.profileUrl(),
                        form.pictureUrl(),
                        form.websiteUrl(),
                        form.zoneinfo(),
                        form.locale(),
                        form.phoneNumber(),
                        form.streetAddress(),
                        form.locality(),
                        form.region(),
                        form.postalCode(),
                        form.country()
                ));

                return "redirect:/profile/edit?saved";
            } catch (IllegalArgumentException | IllegalStateException ex) {
                error = ex.getMessage();
            }
        }

        model.addAttribute("messages", messages);
        model.addAttribute("saved", false);
        model.addAttribute("error", error);

        return "profile/edit";
    }

    @GetMapping("/password")
    public String passwordForm(Model model) {
        model.addAttribute("messages", messages);
        model.addAttribute("changed", false);
        model.addAttribute("error", null);

        return "profile/password";
    }

    @PostMapping("/password")
    public String changePassword(
            @Valid @ModelAttribute PasswordForm form,
            BindingResult bindingResult,
            Model model
    ) {
        String error = null;

        if (!bindingResult.hasErrors()) {
            if (!form.newPassword().equals(form.confirmPassword())) {
                error = messages.get("password.mismatch");
            } else {
                try {
                    changePasswordUseCase.execute(new ChangePasswordCommand(
                            currentUser.require().uuid(),
                            form.currentPassword(),
                            form.newPassword()
                    ));

                    return "redirect:/profile/password?changed";
                } catch (IllegalArgumentException | IllegalStateException ex) {
                    error = ex.getMessage();
                }
            }
        } else {
            error = bindingResult.getFieldError() == null
                    ? messages.get("common.required")
                    : bindingResult.getFieldError().getDefaultMessage();
        }

        model.addAttribute("messages", messages);
        model.addAttribute("changed", false);
        model.addAttribute("error", error);

        return "profile/password";
    }

    public record ProfileForm(
            String givenName,
            String familyName,
            String middleName,
            String nickname,
            String gender,
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate birthdate,
            String profileUrl,
            String pictureUrl,
            String websiteUrl,
            String zoneinfo,
            String locale,
            String phoneNumber,
            String streetAddress,
            String locality,
            String region,
            String postalCode,
            String country
    ) {
        static ProfileForm from(User user) {
            return new ProfileForm(
                    user.givenName(),
                    user.familyName(),
                    user.middleName(),
                    user.nickname(),
                    user.gender(),
                    user.birthdate(),
                    user.profileUrl(),
                    user.pictureUrl(),
                    user.websiteUrl(),
                    user.zoneinfo(),
                    user.locale(),
                    user.phoneNumber(),
                    user.streetAddress(),
                    user.locality(),
                    user.region(),
                    user.postalCode(),
                    user.country()
            );
        }
    }

    public record PasswordForm(
            String currentPassword,
            String newPassword,
            String confirmPassword
    ) {
    }
}

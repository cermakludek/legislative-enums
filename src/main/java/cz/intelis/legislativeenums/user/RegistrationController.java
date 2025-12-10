package cz.intelis.legislativeenums.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Controller for handling user registration.
 *
 * @author Legislative Codelists Team
 */
@Controller
@RequestMapping("/register")
@RequiredArgsConstructor
public class RegistrationController {

    private final UserService userService;

    /**
     * Displays the registration form.
     *
     * @param model the Spring MVC model
     * @return the view name for the registration page
     */
    @GetMapping
    public String showRegistrationForm(Model model) {
        model.addAttribute("registration", new RegistrationDTO());
        model.addAttribute("usagePlans", User.UsagePlan.values());
        return "register";
    }

    /**
     * Processes the registration form submission.
     *
     * @param dto the registration data from the form
     * @param result the binding result for validation errors
     * @param model the Spring MVC model
     * @param redirectAttributes attributes for flash messages
     * @return redirect to success page with API key on success, or back to form on error
     */
    @PostMapping
    public String register(@Valid @ModelAttribute("registration") RegistrationDTO dto,
                          BindingResult result,
                          Model model,
                          RedirectAttributes redirectAttributes) {

        // Validate password matching
        if (!dto.isPasswordMatching()) {
            result.rejectValue("confirmPassword", "validation.password.mismatch",
                "Passwords do not match");
        }

        if (result.hasErrors()) {
            model.addAttribute("usagePlans", User.UsagePlan.values());
            return "register";
        }

        try {
            RegistrationResult registrationResult = userService.registerUser(dto);
            redirectAttributes.addFlashAttribute("registrationResult", registrationResult);
            return "redirect:/register/success";
        } catch (RuntimeException e) {
            model.addAttribute("usagePlans", User.UsagePlan.values());
            model.addAttribute("errorMessage", e.getMessage());
            return "register";
        }
    }

    /**
     * Displays the registration success page with the generated API key.
     *
     * @param model the Spring MVC model
     * @return the view name for the success page
     */
    @GetMapping("/success")
    public String showSuccessPage(Model model) {
        // If no registration result (direct access), redirect to registration
        if (!model.containsAttribute("registrationResult")) {
            return "redirect:/register";
        }
        return "register-success";
    }
}

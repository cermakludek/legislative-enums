package cz.intelis.legislativeenums.user;

import cz.intelis.legislativeenums.apikey.ApiKeyDTO;
import cz.intelis.legislativeenums.apikey.ApiKeyService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

/**
 * Controller for user profile management.
 * Allows logged-in users to view and manage their profile and API keys.
 */
@Controller
@RequestMapping("/web/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final UserService userService;
    private final ApiKeyService apiKeyService;

    /**
     * Displays the current user's profile page.
     */
    @GetMapping
    public String profile(Model model, Authentication authentication) {
        UserDTO user = userService.findByUsername(authentication.getName());
        List<ApiKeyDTO> apiKeys = apiKeyService.findByUserId(user.getId());

        model.addAttribute("user", user);
        model.addAttribute("apiKeys", apiKeys);
        return "profile/view";
    }

    /**
     * Regenerates the API key for the current user.
     */
    @PostMapping("/regenerate-api-key")
    public String regenerateApiKey(Authentication authentication, RedirectAttributes redirectAttributes) {
        try {
            UserDTO user = userService.findByUsername(authentication.getName());
            String newApiKey = apiKeyService.regenerateForUser(user.getId());
            redirectAttributes.addFlashAttribute("successMessage", "API klíč byl úspěšně přegenerován");
            redirectAttributes.addFlashAttribute("newApiKey", newApiKey);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Chyba při přegenerování API klíče: " + e.getMessage());
        }
        return "redirect:/web/profile";
    }
}

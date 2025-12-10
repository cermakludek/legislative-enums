package cz.intelis.legislativeenums.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Web controller for managing User entities via Thymeleaf UI.
 * Provides CRUD operations for user management (admin only).
 *
 * @author Legislative Codelists Team
 */
@Controller
@RequestMapping("/web/users")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class UserWebController {

    private final UserService userService;

    /**
     * Displays the list of all users.
     *
     * @param model the Spring MVC model
     * @return the view name for the users list page
     */
    @GetMapping
    public String list(Model model) {
        model.addAttribute("users", userService.findAll());
        model.addAttribute("roles", User.UserRole.values());
        model.addAttribute("usagePlans", User.UsagePlan.values());
        return "users/list";
    }

    /**
     * Displays the form for editing an existing user.
     *
     * @param id the ID of the user to edit
     * @param model the Spring MVC model
     * @return the view name for the user form page
     */
    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("user", userService.findById(id));
        model.addAttribute("roles", User.UserRole.values());
        model.addAttribute("usagePlans", User.UsagePlan.values());
        return "users/form";
    }

    /**
     * Processes the update of an existing user.
     *
     * @param id the ID of the user to update
     * @param dto the updated user data from the form
     * @param result the binding result for validation errors
     * @param model the Spring MVC model
     * @param redirectAttributes attributes for flash messages
     * @return redirect to the users list on success, or back to form on error
     */
    @PostMapping("/{id}")
    public String update(@PathVariable Long id,
                        @Valid @ModelAttribute("user") UserDTO dto,
                        BindingResult result,
                        Model model,
                        RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("roles", User.UserRole.values());
            model.addAttribute("usagePlans", User.UsagePlan.values());
            return "users/form";
        }

        try {
            userService.update(id, dto);
            redirectAttributes.addFlashAttribute("successMessage", "User updated successfully");
            return "redirect:/web/users";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/web/users/" + id + "/edit";
        }
    }

    /**
     * Deletes a user by their ID.
     *
     * @param id the ID of the user to delete
     * @param redirectAttributes attributes for flash messages
     * @return redirect to the users list
     */
    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            userService.delete(id);
            redirectAttributes.addFlashAttribute("successMessage", "User deleted successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/web/users";
    }
}

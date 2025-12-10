package cz.intelis.legislativeenums.flag;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Web controller for managing Flag entities via Thymeleaf UI.
 * Provides CRUD operations for flag management (admin only).
 *
 * @author Legislative Codelists Team
 */
@Controller
@RequestMapping("/web/flags")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN', 'CONTRIBUTOR')")
public class FlagWebController {

    private final FlagService flagService;

    /**
     * Displays the list of flags.
     *
     * @param showAll if true, shows all records; if false (default), shows only currently valid
     * @param model the Spring MVC model
     * @return the view name for the flags list page
     */
    @GetMapping
    public String list(@RequestParam(required = false, defaultValue = "false") boolean showAll, Model model) {
        model.addAttribute("flags", showAll
            ? flagService.findAll()
            : flagService.findAllCurrentlyValid());
        model.addAttribute("showAll", showAll);
        return "flags/list";
    }

    /**
     * Displays the form for creating a new flag.
     *
     * @param model the Spring MVC model
     * @return the view name for the flag form page
     */
    @GetMapping("/create")
    public String createForm(Model model) {
        model.addAttribute("flag", new FlagDTO());
        return "flags/form";
    }

    /**
     * Processes the creation of a new flag.
     *
     * @param dto the flag data from the form
     * @param result the binding result for validation errors
     * @param redirectAttributes attributes for flash messages
     * @return redirect to the flags list on success, or back to form on error
     */
    @PostMapping
    public String create(@Valid @ModelAttribute("flag") FlagDTO dto,
                        BindingResult result,
                        RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "flags/form";
        }

        try {
            flagService.create(dto);
            redirectAttributes.addFlashAttribute("successMessage", "Flag created successfully");
            return "redirect:/web/flags";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/web/flags/create";
        }
    }

    /**
     * Displays the form for editing an existing flag.
     *
     * @param id the ID of the flag to edit
     * @param model the Spring MVC model
     * @return the view name for the flag form page
     */
    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("flag", flagService.findById(id));
        return "flags/form";
    }

    /**
     * Processes the update of an existing flag.
     *
     * @param id the ID of the flag to update
     * @param dto the updated flag data from the form
     * @param result the binding result for validation errors
     * @param redirectAttributes attributes for flash messages
     * @return redirect to the flags list on success, or back to form on error
     */
    @PostMapping("/{id}")
    public String update(@PathVariable Long id,
                        @Valid @ModelAttribute("flag") FlagDTO dto,
                        BindingResult result,
                        RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "flags/form";
        }

        try {
            flagService.update(id, dto);
            redirectAttributes.addFlashAttribute("successMessage", "Flag updated successfully");
            return "redirect:/web/flags";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/web/flags/" + id + "/edit";
        }
    }

    /**
     * Deletes a flag by its ID.
     *
     * @param id the ID of the flag to delete
     * @param redirectAttributes attributes for flash messages
     * @return redirect to the flags list
     */
    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            flagService.delete(id);
            redirectAttributes.addFlashAttribute("successMessage", "Flag deleted successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/web/flags";
    }
}

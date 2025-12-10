package cz.intelis.legislativeenums.registry;

import cz.intelis.legislativeenums.apikey.ApiKeyRepository;
import cz.intelis.legislativeenums.flag.FlagService;
import cz.intelis.legislativeenums.user.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Web controller for managing CodelistRegistry entities via Thymeleaf UI.
 * Provides the main dashboard view and CRUD operations for codelist registry.
 *
 * @author Legislative Codelists Team
 */
@Controller
@RequestMapping("/web/codelists")
@RequiredArgsConstructor
public class CodelistRegistryWebController {

    private final CodelistRegistryService codelistRegistryService;
    private final FlagService flagService;
    private final UserRepository userRepository;
    private final ApiKeyRepository apiKeyRepository;

    /**
     * Displays the main dashboard with list of all available codelists.
     *
     * @param flagId optional flag ID to filter codelists
     * @param model the Spring MVC model
     * @param userDetails the authenticated user
     * @return the view name for the codelists dashboard page
     */
    @GetMapping
    public String dashboard(@RequestParam(required = false) Long flagId,
                           Model model,
                           @AuthenticationPrincipal UserDetails userDetails) {
        if (flagId != null) {
            model.addAttribute("codelists", codelistRegistryService.findByFlag(flagId));
            model.addAttribute("selectedFlagId", flagId);
        } else {
            model.addAttribute("codelists", codelistRegistryService.findAllCurrentlyValid());
        }
        model.addAttribute("flags", flagService.findAllActive());

        // Get user's API key for API examples
        if (userDetails != null) {
            userRepository.findByUsername(userDetails.getUsername()).ifPresent(user -> {
                var apiKeys = apiKeyRepository.findByUserId(user.getId());
                if (!apiKeys.isEmpty()) {
                    model.addAttribute("userApiKey", apiKeys.get(0).getApiKey());
                }
            });
        }

        return "codelists/dashboard";
    }

    /**
     * Displays the admin list of codelist registry entries.
     *
     * @param showAll if true, shows all records; if false (default), shows only currently valid
     * @param model the Spring MVC model
     * @return the view name for the codelist registry list page
     */
    @GetMapping("/admin")
    @PreAuthorize("hasAnyRole('ADMIN', 'CONTRIBUTOR')")
    public String list(@RequestParam(required = false, defaultValue = "false") boolean showAll, Model model) {
        model.addAttribute("codelists", showAll
            ? codelistRegistryService.findAll()
            : codelistRegistryService.findAllCurrentlyValid());
        model.addAttribute("showAll", showAll);
        return "codelists/list";
    }

    /**
     * Displays the form for creating a new codelist registry entry.
     *
     * @param model the Spring MVC model
     * @return the view name for the codelist registry form page
     */
    @GetMapping("/create")
    @PreAuthorize("hasAnyRole('ADMIN', 'CONTRIBUTOR')")
    public String createForm(Model model) {
        model.addAttribute("codelist", new CodelistRegistryDTO());
        model.addAttribute("allFlags", flagService.findAllActive());
        return "codelists/form";
    }

    /**
     * Processes the creation of a new codelist registry entry.
     *
     * @param dto the codelist registry data from the form
     * @param result the binding result for validation errors
     * @param redirectAttributes attributes for flash messages
     * @return redirect to the codelist registry list on success, or back to form on error
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'CONTRIBUTOR')")
    public String create(@Valid @ModelAttribute("codelist") CodelistRegistryDTO dto,
                        BindingResult result,
                        Model model,
                        RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("allFlags", flagService.findAllActive());
            return "codelists/form";
        }

        try {
            codelistRegistryService.create(dto);
            redirectAttributes.addFlashAttribute("successMessage", "Codelist registry entry created successfully");
            return "redirect:/web/codelists/admin";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/web/codelists/create";
        }
    }

    /**
     * Displays the form for editing an existing codelist registry entry.
     *
     * @param id the ID of the codelist registry entry to edit
     * @param model the Spring MVC model
     * @return the view name for the codelist registry form page
     */
    @GetMapping("/{id}/edit")
    @PreAuthorize("hasAnyRole('ADMIN', 'CONTRIBUTOR')")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("codelist", codelistRegistryService.findById(id));
        model.addAttribute("allFlags", flagService.findAllActive());
        return "codelists/form";
    }

    /**
     * Processes the update of an existing codelist registry entry.
     *
     * @param id the ID of the codelist registry entry to update
     * @param dto the updated codelist registry data from the form
     * @param result the binding result for validation errors
     * @param redirectAttributes attributes for flash messages
     * @return redirect to the codelist registry list on success, or back to form on error
     */
    @PostMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CONTRIBUTOR')")
    public String update(@PathVariable Long id,
                        @Valid @ModelAttribute("codelist") CodelistRegistryDTO dto,
                        BindingResult result,
                        Model model,
                        RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("allFlags", flagService.findAllActive());
            return "codelists/form";
        }

        try {
            codelistRegistryService.update(id, dto);
            redirectAttributes.addFlashAttribute("successMessage", "Codelist registry entry updated successfully");
            return "redirect:/web/codelists/admin";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/web/codelists/" + id + "/edit";
        }
    }

    /**
     * Deletes a codelist registry entry by its ID.
     *
     * @param id the ID of the codelist registry entry to delete
     * @param redirectAttributes attributes for flash messages
     * @return redirect to the codelist registry list
     */
    @PostMapping("/{id}/delete")
    @PreAuthorize("hasAnyRole('ADMIN', 'CONTRIBUTOR')")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            codelistRegistryService.delete(id);
            redirectAttributes.addFlashAttribute("successMessage", "Codelist registry entry deleted successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/web/codelists/admin";
    }
}

package cz.intelis.legislativeenums.networktype;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Web controller for managing NetworkType entities via Thymeleaf UI.
 * Provides CRUD operations through HTML forms and views for network type
 * classifications (Rozdělení sítí z hlediska vedení).
 *
 * @author Legislative Codelists Team
 */
@Controller
@RequestMapping("/web/network-types")
@RequiredArgsConstructor
public class NetworkTypeWebController {

    private final NetworkTypeService networkTypeService;

    /**
     * Displays the list of network types.
     *
     * @param showAll if true, shows all records; if false (default), shows only currently valid
     * @param model the Spring MVC model
     * @return the view name for the network types list page
     */
    @GetMapping
    public String list(@RequestParam(required = false, defaultValue = "false") boolean showAll, Model model) {
        model.addAttribute("networkTypes", showAll
            ? networkTypeService.findAll()
            : networkTypeService.findAllCurrentlyValid());
        model.addAttribute("showAll", showAll);
        return "network-types/list";
    }

    /**
     * Displays the form for creating a new network type.
     *
     * @param model the Spring MVC model
     * @return the view name for the network type form page
     */
    @GetMapping("/create")
    @PreAuthorize("hasAnyRole('ADMIN', 'CONTRIBUTOR')")
    public String createForm(Model model) {
        model.addAttribute("networkType", new NetworkTypeDTO());
        return "network-types/form";
    }

    /**
     * Processes the creation of a new network type.
     *
     * @param dto the network type data from the form
     * @param result the binding result for validation errors
     * @param redirectAttributes attributes for flash messages
     * @return redirect to the network types list on success, or back to form on error
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'CONTRIBUTOR')")
    public String create(@Valid @ModelAttribute("networkType") NetworkTypeDTO dto,
                        BindingResult result,
                        RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "network-types/form";
        }

        try {
            networkTypeService.create(dto);
            redirectAttributes.addFlashAttribute("successMessage", "Network type created successfully");
            return "redirect:/web/network-types";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/web/network-types/create";
        }
    }

    /**
     * Displays the form for editing an existing network type.
     *
     * @param id the ID of the network type to edit
     * @param model the Spring MVC model
     * @return the view name for the network type form page
     */
    @GetMapping("/{id}/edit")
    @PreAuthorize("hasAnyRole('ADMIN', 'CONTRIBUTOR')")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("networkType", networkTypeService.findById(id));
        return "network-types/form";
    }

    /**
     * Processes the update of an existing network type.
     *
     * @param id the ID of the network type to update
     * @param dto the updated network type data from the form
     * @param result the binding result for validation errors
     * @param redirectAttributes attributes for flash messages
     * @return redirect to the network types list on success, or back to form on error
     */
    @PostMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CONTRIBUTOR')")
    public String update(@PathVariable Long id,
                        @Valid @ModelAttribute("networkType") NetworkTypeDTO dto,
                        BindingResult result,
                        RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "network-types/form";
        }

        try {
            networkTypeService.update(id, dto);
            redirectAttributes.addFlashAttribute("successMessage", "Network type updated successfully");
            return "redirect:/web/network-types";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/web/network-types/" + id + "/edit";
        }
    }

    /**
     * Deletes a network type by its ID.
     *
     * @param id the ID of the network type to delete
     * @param redirectAttributes attributes for flash messages
     * @return redirect to the network types list
     */
    @PostMapping("/{id}/delete")
    @PreAuthorize("hasAnyRole('ADMIN', 'CONTRIBUTOR')")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            networkTypeService.delete(id);
            redirectAttributes.addFlashAttribute("successMessage", "Network type deleted successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/web/network-types";
    }
}

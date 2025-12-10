package cz.intelis.legislativeenums.voltagelevel;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Web controller for managing VoltageLevel entities via Thymeleaf UI.
 * Provides CRUD operations through HTML forms and views for voltage level
 * classifications (Rozdělení napětí dle velikosti).
 *
 * @author Legislative Codelists Team
 */
@Controller
@RequestMapping("/web/voltage-levels")
@RequiredArgsConstructor
public class VoltageLevelWebController {

    private final VoltageLevelService voltageLevelService;

    /**
     * Displays the list of voltage levels.
     *
     * @param showAll if true, shows all records; if false (default), shows only currently valid
     * @param model the Spring MVC model
     * @return the view name for the voltage levels list page
     */
    @GetMapping
    public String list(@RequestParam(required = false, defaultValue = "false") boolean showAll, Model model) {
        model.addAttribute("voltageLevels", showAll
            ? voltageLevelService.findAll()
            : voltageLevelService.findAllCurrentlyValid());
        model.addAttribute("showAll", showAll);
        return "voltage-levels/list";
    }

    /**
     * Displays the form for creating a new voltage level.
     *
     * @param model the Spring MVC model
     * @return the view name for the voltage level form page
     */
    @GetMapping("/create")
    @PreAuthorize("hasAnyRole('ADMIN', 'CONTRIBUTOR')")
    public String createForm(Model model) {
        model.addAttribute("voltageLevel", new VoltageLevelDTO());
        return "voltage-levels/form";
    }

    /**
     * Processes the creation of a new voltage level.
     *
     * @param dto the voltage level data from the form
     * @param result the binding result for validation errors
     * @param redirectAttributes attributes for flash messages
     * @return redirect to the voltage levels list on success, or back to form on error
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'CONTRIBUTOR')")
    public String create(@Valid @ModelAttribute("voltageLevel") VoltageLevelDTO dto,
                        BindingResult result,
                        RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "voltage-levels/form";
        }

        try {
            voltageLevelService.create(dto);
            redirectAttributes.addFlashAttribute("successMessage", "Voltage level created successfully");
            return "redirect:/web/voltage-levels";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/web/voltage-levels/create";
        }
    }

    /**
     * Displays the form for editing an existing voltage level.
     *
     * @param id the ID of the voltage level to edit
     * @param model the Spring MVC model
     * @return the view name for the voltage level form page
     */
    @GetMapping("/{id}/edit")
    @PreAuthorize("hasAnyRole('ADMIN', 'CONTRIBUTOR')")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("voltageLevel", voltageLevelService.findById(id));
        return "voltage-levels/form";
    }

    /**
     * Processes the update of an existing voltage level.
     *
     * @param id the ID of the voltage level to update
     * @param dto the updated voltage level data from the form
     * @param result the binding result for validation errors
     * @param redirectAttributes attributes for flash messages
     * @return redirect to the voltage levels list on success, or back to form on error
     */
    @PostMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CONTRIBUTOR')")
    public String update(@PathVariable Long id,
                        @Valid @ModelAttribute("voltageLevel") VoltageLevelDTO dto,
                        BindingResult result,
                        RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "voltage-levels/form";
        }

        try {
            voltageLevelService.update(id, dto);
            redirectAttributes.addFlashAttribute("successMessage", "Voltage level updated successfully");
            return "redirect:/web/voltage-levels";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/web/voltage-levels/" + id + "/edit";
        }
    }

    /**
     * Deletes a voltage level by its ID.
     *
     * @param id the ID of the voltage level to delete
     * @param redirectAttributes attributes for flash messages
     * @return redirect to the voltage levels list
     */
    @PostMapping("/{id}/delete")
    @PreAuthorize("hasAnyRole('ADMIN', 'CONTRIBUTOR')")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            voltageLevelService.delete(id);
            redirectAttributes.addFlashAttribute("successMessage", "Voltage level deleted successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/web/voltage-levels";
    }
}

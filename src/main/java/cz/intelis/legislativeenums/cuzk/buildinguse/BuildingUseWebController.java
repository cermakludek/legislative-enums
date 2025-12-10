package cz.intelis.legislativeenums.cuzk.buildinguse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Web controller for managing BuildingUse entities via Thymeleaf UI.
 * Based on ČÚZK codelist SC_ZP_VYUZITI_BUD (Způsob využití stavby).
 *
 * @author Legislative Codelists Team
 */
@Controller
@RequestMapping("/web/cuzk/building-uses")
@RequiredArgsConstructor
public class BuildingUseWebController {

    private final BuildingUseService service;

    /**
     * Displays the list of building uses.
     *
     * @param showAll if true, shows all records; if false (default), shows only currently valid
     * @param model the Spring MVC model
     * @return the view name for the building uses list page
     */
    @GetMapping
    public String list(@RequestParam(required = false, defaultValue = "false") boolean showAll, Model model) {
        model.addAttribute("buildingUses", showAll
            ? service.findAll()
            : service.findAllCurrentlyValid());
        model.addAttribute("showAll", showAll);
        return "cuzk/building-uses/list";
    }

    @GetMapping("/create")
    @PreAuthorize("hasAnyRole('ADMIN', 'CONTRIBUTOR')")
    public String createForm(Model model) {
        model.addAttribute("buildingUse", new BuildingUseDTO());
        return "cuzk/building-uses/form";
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'CONTRIBUTOR')")
    public String create(@Valid @ModelAttribute("buildingUse") BuildingUseDTO dto,
                        BindingResult result,
                        RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "cuzk/building-uses/form";
        }

        try {
            service.create(dto);
            redirectAttributes.addFlashAttribute("successMessage", "Building use created successfully");
            return "redirect:/web/cuzk/building-uses";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/web/cuzk/building-uses/create";
        }
    }

    @GetMapping("/{id}/edit")
    @PreAuthorize("hasAnyRole('ADMIN', 'CONTRIBUTOR')")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("buildingUse", service.findById(id));
        return "cuzk/building-uses/form";
    }

    @PostMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CONTRIBUTOR')")
    public String update(@PathVariable Long id,
                        @Valid @ModelAttribute("buildingUse") BuildingUseDTO dto,
                        BindingResult result,
                        RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "cuzk/building-uses/form";
        }

        try {
            service.update(id, dto);
            redirectAttributes.addFlashAttribute("successMessage", "Building use updated successfully");
            return "redirect:/web/cuzk/building-uses";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/web/cuzk/building-uses/" + id + "/edit";
        }
    }

    @PostMapping("/{id}/delete")
    @PreAuthorize("hasAnyRole('ADMIN', 'CONTRIBUTOR')")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            service.delete(id);
            redirectAttributes.addFlashAttribute("successMessage", "Building use deleted successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/web/cuzk/building-uses";
    }
}

package cz.intelis.legislativeenums.cuzk.landtype;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Web controller for managing LandType entities via Thymeleaf UI.
 * Based on ČÚZK codelist SC_D_POZEMKU (Druh pozemku).
 *
 * @author Legislative Codelists Team
 */
@Controller
@RequestMapping("/web/cuzk/land-types")
@RequiredArgsConstructor
public class LandTypeWebController {

    private final LandTypeService service;

    /**
     * Displays the list of land types.
     *
     * @param showAll if true, shows all records; if false (default), shows only currently valid
     * @param model the Spring MVC model
     * @return the view name for the land types list page
     */
    @GetMapping
    public String list(@RequestParam(required = false, defaultValue = "false") boolean showAll, Model model) {
        model.addAttribute("landTypes", showAll
            ? service.findAll()
            : service.findAllCurrentlyValid());
        model.addAttribute("showAll", showAll);
        return "cuzk/land-types/list";
    }

    @GetMapping("/create")
    @PreAuthorize("hasAnyRole('ADMIN', 'CONTRIBUTOR')")
    public String createForm(Model model) {
        model.addAttribute("landType", new LandTypeDTO());
        return "cuzk/land-types/form";
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'CONTRIBUTOR')")
    public String create(@Valid @ModelAttribute("landType") LandTypeDTO dto,
                        BindingResult result,
                        RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "cuzk/land-types/form";
        }

        try {
            service.create(dto);
            redirectAttributes.addFlashAttribute("successMessage", "Land type created successfully");
            return "redirect:/web/cuzk/land-types";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/web/cuzk/land-types/create";
        }
    }

    @GetMapping("/{id}/edit")
    @PreAuthorize("hasAnyRole('ADMIN', 'CONTRIBUTOR')")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("landType", service.findById(id));
        return "cuzk/land-types/form";
    }

    @PostMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CONTRIBUTOR')")
    public String update(@PathVariable Long id,
                        @Valid @ModelAttribute("landType") LandTypeDTO dto,
                        BindingResult result,
                        RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "cuzk/land-types/form";
        }

        try {
            service.update(id, dto);
            redirectAttributes.addFlashAttribute("successMessage", "Land type updated successfully");
            return "redirect:/web/cuzk/land-types";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/web/cuzk/land-types/" + id + "/edit";
        }
    }

    @PostMapping("/{id}/delete")
    @PreAuthorize("hasAnyRole('ADMIN', 'CONTRIBUTOR')")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            service.delete(id);
            redirectAttributes.addFlashAttribute("successMessage", "Land type deleted successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/web/cuzk/land-types";
    }
}

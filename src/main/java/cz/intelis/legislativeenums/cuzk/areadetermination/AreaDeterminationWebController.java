package cz.intelis.legislativeenums.cuzk.areadetermination;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Web controller for managing AreaDetermination entities via Thymeleaf UI.
 * Based on ČÚZK codelist SC_ZP_URCENI_VYMERY (Způsob určení výměry).
 *
 * @author Legislative Codelists Team
 */
@Controller
@RequestMapping("/web/cuzk/area-determinations")
@RequiredArgsConstructor
public class AreaDeterminationWebController {

    private final AreaDeterminationService service;

    /**
     * Displays the list of area determinations.
     *
     * @param showAll if true, shows all records; if false (default), shows only currently valid
     * @param model the Spring MVC model
     * @return the view name for the area determinations list page
     */
    @GetMapping
    public String list(@RequestParam(required = false, defaultValue = "false") boolean showAll, Model model) {
        model.addAttribute("areaDeterminations", showAll
            ? service.findAll()
            : service.findAllCurrentlyValid());
        model.addAttribute("showAll", showAll);
        return "cuzk/area-determinations/list";
    }

    @GetMapping("/create")
    @PreAuthorize("hasAnyRole('ADMIN', 'CONTRIBUTOR')")
    public String createForm(Model model) {
        model.addAttribute("areaDetermination", new AreaDeterminationDTO());
        return "cuzk/area-determinations/form";
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'CONTRIBUTOR')")
    public String create(@Valid @ModelAttribute("areaDetermination") AreaDeterminationDTO dto,
                        BindingResult result,
                        RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "cuzk/area-determinations/form";
        }

        try {
            service.create(dto);
            redirectAttributes.addFlashAttribute("successMessage", "Area determination created successfully");
            return "redirect:/web/cuzk/area-determinations";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/web/cuzk/area-determinations/create";
        }
    }

    @GetMapping("/{id}/edit")
    @PreAuthorize("hasAnyRole('ADMIN', 'CONTRIBUTOR')")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("areaDetermination", service.findById(id));
        return "cuzk/area-determinations/form";
    }

    @PostMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CONTRIBUTOR')")
    public String update(@PathVariable Long id,
                        @Valid @ModelAttribute("areaDetermination") AreaDeterminationDTO dto,
                        BindingResult result,
                        RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "cuzk/area-determinations/form";
        }

        try {
            service.update(id, dto);
            redirectAttributes.addFlashAttribute("successMessage", "Area determination updated successfully");
            return "redirect:/web/cuzk/area-determinations";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/web/cuzk/area-determinations/" + id + "/edit";
        }
    }

    @PostMapping("/{id}/delete")
    @PreAuthorize("hasAnyRole('ADMIN', 'CONTRIBUTOR')")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            service.delete(id);
            redirectAttributes.addFlashAttribute("successMessage", "Area determination deleted successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/web/cuzk/area-determinations";
    }
}

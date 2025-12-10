package cz.intelis.legislativeenums.cuzk.unittype;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/web/cuzk/unit-types")
@RequiredArgsConstructor
public class UnitTypeWebController {

    private final UnitTypeService service;

    /**
     * Displays the list of unit types.
     *
     * @param showAll if true, shows all records; if false (default), shows only currently valid
     * @param model the Spring MVC model
     * @return the view name for the unit types list page
     */
    @GetMapping
    public String list(@RequestParam(required = false, defaultValue = "false") boolean showAll, Model model) {
        model.addAttribute("items", showAll
            ? service.findAll()
            : service.findAllCurrentlyValid());
        model.addAttribute("showAll", showAll);
        return "cuzk/unit-types/list";
    }

    @GetMapping("/create")
    @PreAuthorize("hasAnyRole('ADMIN', 'CONTRIBUTOR')")
    public String createForm(Model model) {
        model.addAttribute("item", new UnitTypeDTO());
        return "cuzk/unit-types/form";
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'CONTRIBUTOR')")
    public String create(@Valid @ModelAttribute("item") UnitTypeDTO dto,
                        BindingResult result,
                        RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "cuzk/unit-types/form";
        }
        try {
            service.create(dto);
            redirectAttributes.addFlashAttribute("successMessage", "Record created successfully");
            return "redirect:/web/cuzk/unit-types";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/web/cuzk/unit-types/create";
        }
    }

    @GetMapping("/{id}/edit")
    @PreAuthorize("hasAnyRole('ADMIN', 'CONTRIBUTOR')")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("item", service.findById(id));
        return "cuzk/unit-types/form";
    }

    @PostMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CONTRIBUTOR')")
    public String update(@PathVariable Long id,
                        @Valid @ModelAttribute("item") UnitTypeDTO dto,
                        BindingResult result,
                        RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "cuzk/unit-types/form";
        }
        try {
            service.update(id, dto);
            redirectAttributes.addFlashAttribute("successMessage", "Record updated successfully");
            return "redirect:/web/cuzk/unit-types";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/web/cuzk/unit-types/" + id + "/edit";
        }
    }

    @PostMapping("/{id}/delete")
    @PreAuthorize("hasAnyRole('ADMIN', 'CONTRIBUTOR')")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            service.delete(id);
            redirectAttributes.addFlashAttribute("successMessage", "Record deleted successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/web/cuzk/unit-types";
    }
}

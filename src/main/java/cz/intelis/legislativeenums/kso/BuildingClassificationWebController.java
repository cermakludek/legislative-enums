package cz.intelis.legislativeenums.kso;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

/**
 * Web controller for managing BuildingClassification entities via Thymeleaf UI.
 * Provides CRUD operations through HTML forms and views for building classifications (KSO).
 *
 * @author Legislative Codelists Team
 */
@Controller
@RequestMapping("/web/building-classifications")
@RequiredArgsConstructor
public class BuildingClassificationWebController {

    private final BuildingClassificationService service;

    /**
     * Displays the tree view of all building classifications.
     */
    @GetMapping
    public String list(Model model) {
        model.addAttribute("tree", service.findTree());
        return "kso/list";
    }

    /**
     * Displays the form for creating a new classification.
     */
    @GetMapping("/create")
    @PreAuthorize("hasAnyRole('ADMIN', 'CONTRIBUTOR')")
    public String createForm(Model model, @RequestParam(required = false) Integer level) {
        BuildingClassificationDTO dto = new BuildingClassificationDTO();
        dto.setLevel(level != null ? level : 1);
        model.addAttribute("item", dto);
        model.addAttribute("possibleParents", service.getPossibleParents(dto.getLevel()));
        return "kso/form";
    }

    /**
     * Processes the creation of a new classification.
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'CONTRIBUTOR')")
    public String create(@Valid @ModelAttribute("item") BuildingClassificationDTO dto,
                        BindingResult result,
                        Model model,
                        RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("possibleParents", service.getPossibleParents(dto.getLevel()));
            return "kso/form";
        }

        try {
            service.create(dto);
            redirectAttributes.addFlashAttribute("successMessage", "Building classification created successfully");
            return "redirect:/web/building-classifications";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/web/building-classifications/create?level=" + dto.getLevel();
        }
    }

    /**
     * Displays the form for editing an existing classification.
     */
    @GetMapping("/{id}/edit")
    @PreAuthorize("hasAnyRole('ADMIN', 'CONTRIBUTOR')")
    public String editForm(@PathVariable Long id, Model model) {
        BuildingClassificationDTO dto = service.findById(id);
        model.addAttribute("item", dto);
        model.addAttribute("possibleParents", service.getPossibleParents(dto.getLevel()));
        return "kso/form";
    }

    /**
     * Processes the update of an existing classification.
     */
    @PostMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CONTRIBUTOR')")
    public String update(@PathVariable Long id,
                        @Valid @ModelAttribute("item") BuildingClassificationDTO dto,
                        BindingResult result,
                        Model model,
                        RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("possibleParents", service.getPossibleParents(dto.getLevel()));
            return "kso/form";
        }

        try {
            service.update(id, dto);
            redirectAttributes.addFlashAttribute("successMessage", "Building classification updated successfully");
            return "redirect:/web/building-classifications";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/web/building-classifications/" + id + "/edit";
        }
    }

    /**
     * Deletes a classification by its ID.
     */
    @PostMapping("/{id}/delete")
    @PreAuthorize("hasAnyRole('ADMIN', 'CONTRIBUTOR')")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            service.delete(id);
            redirectAttributes.addFlashAttribute("successMessage", "Building classification deleted successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/web/building-classifications";
    }

    /**
     * AJAX endpoint to get possible parents for a given level.
     */
    @GetMapping("/parents")
    @ResponseBody
    public List<BuildingClassificationDTO> getPossibleParents(@RequestParam Integer level) {
        return service.getPossibleParents(level);
    }
}

package cz.intelis.legislativeenums.kso;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("BuildingClassificationWebController Unit Tests")
class BuildingClassificationWebControllerTest {

    @Mock
    private BuildingClassificationService service;

    @Mock
    private Model model;

    @Mock
    private BindingResult bindingResult;

    @Mock
    private RedirectAttributes redirectAttributes;

    @InjectMocks
    private BuildingClassificationWebController controller;

    private BuildingClassificationDTO testDTO;
    private List<BuildingClassificationDTO> testTree;
    private List<BuildingClassificationDTO> possibleParents;

    @BeforeEach
    void setUp() {
        testDTO = new BuildingClassificationDTO();
        testDTO.setId(1L);
        testDTO.setCode("KSO001");
        testDTO.setNameCs("Test Building");
        testDTO.setNameEn("Test Building");
        testDTO.setLevel(1);
        BuildingClassificationDTO parent = new BuildingClassificationDTO();
        parent.setId(2L);
        parent.setCode("KSO002");
        parent.setNameCs("Parent Building");
        parent.setNameEn("Parent Building");
        parent.setLevel(1);
        possibleParents = Arrays.asList(parent);

        BuildingClassificationDTO child = new BuildingClassificationDTO();
        child.setId(3L);
        child.setCode("KSO003");
        child.setNameCs("Child Building");
        child.setNameEn("Child Building");
        child.setLevel(2);
        testDTO.setChildren(Arrays.asList(child));
        testTree = Arrays.asList(testDTO, parent);
    }

    @Test
    @DisplayName("list() should return tree view with all building classifications")
    void testList() {
        // Given
        when(service.findTree()).thenReturn(testTree);

        // When
        String viewName = controller.list(model);

        // Then
        assertThat(viewName).isEqualTo("kso/list");
        verify(service).findTree();
        verify(model).addAttribute("tree", testTree);
    }

    @Test
    @DisplayName("createForm() should display create form with default level 1")
    void testCreateForm_withoutLevel() {
        // Given
        when(service.getPossibleParents(1)).thenReturn(List.of());

        // When
        String viewName = controller.createForm(model, null);

        // Then
        assertThat(viewName).isEqualTo("kso/form");
        verify(model).addAttribute(eq("item"), any(BuildingClassificationDTO.class));
        verify(model).addAttribute("possibleParents", List.of());
        verify(service).getPossibleParents(1);
    }

    @Test
    @DisplayName("createForm() should display create form with specified level")
    void testCreateForm_withLevel() {
        // Given
        when(service.getPossibleParents(2)).thenReturn(possibleParents);

        // When
        String viewName = controller.createForm(model, 2);

        // Then
        assertThat(viewName).isEqualTo("kso/form");
        verify(model).addAttribute(eq("item"), any(BuildingClassificationDTO.class));
        verify(model).addAttribute("possibleParents", possibleParents);
        verify(service).getPossibleParents(2);
    }

    @Test
    @DisplayName("create() should successfully create building classification")
    void testCreate_success() {
        // Given
        when(bindingResult.hasErrors()).thenReturn(false);
        when(service.create(any(BuildingClassificationDTO.class))).thenReturn(testDTO);

        // When
        String viewName = controller.create(testDTO, bindingResult, model, redirectAttributes);

        // Then
        assertThat(viewName).isEqualTo("redirect:/web/building-classifications");
        verify(service).create(testDTO);
        verify(redirectAttributes).addFlashAttribute("successMessage", "Building classification created successfully");
        verify(model, never()).addAttribute(anyString(), any());
    }

    @Test
    @DisplayName("create() should return form view when validation errors exist")
    void testCreate_withValidationErrors() {
        // Given
        when(bindingResult.hasErrors()).thenReturn(true);
        when(service.getPossibleParents(1)).thenReturn(possibleParents);

        // When
        String viewName = controller.create(testDTO, bindingResult, model, redirectAttributes);

        // Then
        assertThat(viewName).isEqualTo("kso/form");
        verify(service, never()).create(any());
        verify(model).addAttribute("possibleParents", possibleParents);
        verify(redirectAttributes, never()).addFlashAttribute(anyString(), anyString());
    }

    @Test
    @DisplayName("create() should handle exception and redirect to create form")
    void testCreate_withException() {
        // Given
        when(bindingResult.hasErrors()).thenReturn(false);
        when(service.create(any(BuildingClassificationDTO.class)))
                .thenThrow(new RuntimeException("Building classification with code KSO001 already exists"));

        // When
        String viewName = controller.create(testDTO, bindingResult, model, redirectAttributes);

        // Then
        assertThat(viewName).isEqualTo("redirect:/web/building-classifications/create?level=" + testDTO.getLevel());
        verify(service).create(testDTO);
        verify(redirectAttributes).addFlashAttribute("errorMessage", "Building classification with code KSO001 already exists");
    }

    @Test
    @DisplayName("editForm() should display edit form with existing building classification")
    void testEditForm() {
        // Given
        when(service.findById(1L)).thenReturn(testDTO);
        when(service.getPossibleParents(1)).thenReturn(List.of());

        // When
        String viewName = controller.editForm(1L, model);

        // Then
        assertThat(viewName).isEqualTo("kso/form");
        verify(service).findById(1L);
        verify(model).addAttribute("item", testDTO);
        verify(model).addAttribute("possibleParents", List.of());
        verify(service).getPossibleParents(1);
    }

    @Test
    @DisplayName("update() should successfully update building classification")
    void testUpdate_success() {
        // Given
        when(bindingResult.hasErrors()).thenReturn(false);
        when(service.update(eq(1L), any(BuildingClassificationDTO.class))).thenReturn(testDTO);

        // When
        String viewName = controller.update(1L, testDTO, bindingResult, model, redirectAttributes);

        // Then
        assertThat(viewName).isEqualTo("redirect:/web/building-classifications");
        verify(service).update(1L, testDTO);
        verify(redirectAttributes).addFlashAttribute("successMessage", "Building classification updated successfully");
        verify(model, never()).addAttribute(anyString(), any());
    }

    @Test
    @DisplayName("update() should return form view when validation errors exist")
    void testUpdate_withValidationErrors() {
        // Given
        when(bindingResult.hasErrors()).thenReturn(true);
        when(service.getPossibleParents(1)).thenReturn(possibleParents);

        // When
        String viewName = controller.update(1L, testDTO, bindingResult, model, redirectAttributes);

        // Then
        assertThat(viewName).isEqualTo("kso/form");
        verify(service, never()).update(anyLong(), any());
        verify(model).addAttribute("possibleParents", possibleParents);
        verify(redirectAttributes, never()).addFlashAttribute(anyString(), anyString());
    }

    @Test
    @DisplayName("update() should handle exception and redirect to edit form")
    void testUpdate_withException() {
        // Given
        when(bindingResult.hasErrors()).thenReturn(false);
        when(service.update(eq(1L), any(BuildingClassificationDTO.class)))
                .thenThrow(new RuntimeException("Building classification with code KSO001 already exists"));

        // When
        String viewName = controller.update(1L, testDTO, bindingResult, model, redirectAttributes);

        // Then
        assertThat(viewName).isEqualTo("redirect:/web/building-classifications/" + 1L + "/edit");
        verify(service).update(1L, testDTO);
        verify(redirectAttributes).addFlashAttribute("errorMessage", "Building classification with code KSO001 already exists");
    }

    @Test
    @DisplayName("delete() should successfully delete building classification")
    void testDelete_success() {
        // Given
        doNothing().when(service).delete(1L);

        // When
        String viewName = controller.delete(1L, redirectAttributes);

        // Then
        assertThat(viewName).isEqualTo("redirect:/web/building-classifications");
        verify(service).delete(1L);
        verify(redirectAttributes).addFlashAttribute("successMessage", "Building classification deleted successfully");
    }

    @Test
    @DisplayName("delete() should handle exception when deleting building classification")
    void testDelete_withException() {
        // Given
        doThrow(new RuntimeException("Cannot delete classification with children. Delete children first."))
                .when(service).delete(1L);

        // When
        String viewName = controller.delete(1L, redirectAttributes);

        // Then
        assertThat(viewName).isEqualTo("redirect:/web/building-classifications");
        verify(service).delete(1L);
        verify(redirectAttributes).addFlashAttribute("errorMessage", "Cannot delete classification with children. Delete children first.");
    }

    @Test
    @DisplayName("getPossibleParents() should return list of possible parents for level 2")
    void testGetPossibleParents() {
        // Given
        when(service.getPossibleParents(2)).thenReturn(possibleParents);

        // When
        List<BuildingClassificationDTO> result = controller.getPossibleParents(2);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getCode()).isEqualTo("KSO002");
        verify(service).getPossibleParents(2);
    }

    @Test
    @DisplayName("getPossibleParents() should return empty list for level 1")
    void testGetPossibleParents_level1() {
        // Given
        when(service.getPossibleParents(1)).thenReturn(List.of());

        // When
        List<BuildingClassificationDTO> result = controller.getPossibleParents(1);

        // Then
        assertThat(result).isEmpty();
        verify(service).getPossibleParents(1);
    }
}

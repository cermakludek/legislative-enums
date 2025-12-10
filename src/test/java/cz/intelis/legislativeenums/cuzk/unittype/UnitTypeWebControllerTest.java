package cz.intelis.legislativeenums.cuzk.unittype;

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
@DisplayName("UnitTypeWebController Unit Tests")
class UnitTypeWebControllerTest {

    @Mock
    private UnitTypeService service;

    @Mock
    private Model model;

    @Mock
    private BindingResult bindingResult;

    @Mock
    private RedirectAttributes redirectAttributes;

    @InjectMocks
    private UnitTypeWebController controller;

    private UnitTypeDTO testDTO;

    @BeforeEach
    void setUp() {
        testDTO = new UnitTypeDTO();
        testDTO.setId(1L);
        testDTO.setCode("UT01");
        testDTO.setNameCs("Testovací typ jednotky");
        testDTO.setNameEn("Test unit type");
        testDTO.setDescriptionCs("Popis v češtině");
        testDTO.setDescriptionEn("Description in English");
        testDTO.setAbbreviation("TUT");
        testDTO.setCivilCode(true);
        testDTO.setSortOrder(1);
    }

    @Test
    @DisplayName("list() should add currently valid items to model and return list view")
    void testList() {
        // Arrange
        List<UnitTypeDTO> items = Arrays.asList(testDTO);
        when(service.findAllCurrentlyValid()).thenReturn(items);

        // Act
        String viewName = controller.list(false, model);

        // Assert
        assertThat(viewName).isEqualTo("cuzk/unit-types/list");
        verify(service).findAllCurrentlyValid();
        verify(model).addAttribute("items", items);
    }

    @Test
    @DisplayName("list() should add all items to model when showAll is true")
    void testListShowAll() {
        // Arrange
        List<UnitTypeDTO> items = Arrays.asList(testDTO);
        when(service.findAll()).thenReturn(items);

        // Act
        String viewName = controller.list(true, model);

        // Assert
        assertThat(viewName).isEqualTo("cuzk/unit-types/list");
        verify(service).findAll();
        verify(model).addAttribute("items", items);
    }

    @Test
    @DisplayName("createForm() should add new DTO to model and return form view")
    void testCreateForm() {
        // Act
        String viewName = controller.createForm(model);

        // Assert
        assertThat(viewName).isEqualTo("cuzk/unit-types/form");
        verify(model).addAttribute(eq("item"), any(UnitTypeDTO.class));
    }

    @Test
    @DisplayName("create() should create record and redirect on success")
    void testCreateSuccess() {
        // Arrange
        when(bindingResult.hasErrors()).thenReturn(false);
        when(service.create(testDTO)).thenReturn(testDTO);

        // Act
        String viewName = controller.create(testDTO, bindingResult, redirectAttributes);

        // Assert
        assertThat(viewName).isEqualTo("redirect:/web/cuzk/unit-types");
        verify(service).create(testDTO);
        verify(redirectAttributes).addFlashAttribute("successMessage", "Record created successfully");
    }

    @Test
    @DisplayName("create() should return form view when validation errors exist")
    void testCreateValidationError() {
        // Arrange
        when(bindingResult.hasErrors()).thenReturn(true);

        // Act
        String viewName = controller.create(testDTO, bindingResult, redirectAttributes);

        // Assert
        assertThat(viewName).isEqualTo("cuzk/unit-types/form");
        verify(service, never()).create(any());
        verify(redirectAttributes, never()).addFlashAttribute(anyString(), anyString());
    }

    @Test
    @DisplayName("create() should handle exception and redirect to create form")
    void testCreateException() {
        // Arrange
        when(bindingResult.hasErrors()).thenReturn(false);
        when(service.create(testDTO)).thenThrow(new RuntimeException("Creation failed"));

        // Act
        String viewName = controller.create(testDTO, bindingResult, redirectAttributes);

        // Assert
        assertThat(viewName).isEqualTo("redirect:/web/cuzk/unit-types/create");
        verify(service).create(testDTO);
        verify(redirectAttributes).addFlashAttribute("errorMessage", "Creation failed");
    }

    @Test
    @DisplayName("editForm() should add item to model and return form view")
    void testEditForm() {
        // Arrange
        Long id = 1L;
        when(service.findById(id)).thenReturn(testDTO);

        // Act
        String viewName = controller.editForm(id, model);

        // Assert
        assertThat(viewName).isEqualTo("cuzk/unit-types/form");
        verify(service).findById(id);
        verify(model).addAttribute("item", testDTO);
    }

    @Test
    @DisplayName("update() should update record and redirect on success")
    void testUpdateSuccess() {
        // Arrange
        Long id = 1L;
        when(bindingResult.hasErrors()).thenReturn(false);
        when(service.update(id, testDTO)).thenReturn(testDTO);

        // Act
        String viewName = controller.update(id, testDTO, bindingResult, redirectAttributes);

        // Assert
        assertThat(viewName).isEqualTo("redirect:/web/cuzk/unit-types");
        verify(service).update(id, testDTO);
        verify(redirectAttributes).addFlashAttribute("successMessage", "Record updated successfully");
    }

    @Test
    @DisplayName("update() should return form view when validation errors exist")
    void testUpdateValidationError() {
        // Arrange
        Long id = 1L;
        when(bindingResult.hasErrors()).thenReturn(true);

        // Act
        String viewName = controller.update(id, testDTO, bindingResult, redirectAttributes);

        // Assert
        assertThat(viewName).isEqualTo("cuzk/unit-types/form");
        verify(service, never()).update(anyLong(), any());
        verify(redirectAttributes, never()).addFlashAttribute(anyString(), anyString());
    }

    @Test
    @DisplayName("update() should handle exception and redirect to edit form")
    void testUpdateException() {
        // Arrange
        Long id = 1L;
        when(bindingResult.hasErrors()).thenReturn(false);
        when(service.update(id, testDTO)).thenThrow(new RuntimeException("Update failed"));

        // Act
        String viewName = controller.update(id, testDTO, bindingResult, redirectAttributes);

        // Assert
        assertThat(viewName).isEqualTo("redirect:/web/cuzk/unit-types/" + id + "/edit");
        verify(service).update(id, testDTO);
        verify(redirectAttributes).addFlashAttribute("errorMessage", "Update failed");
    }

    @Test
    @DisplayName("delete() should delete record and redirect with success message")
    void testDeleteSuccess() {
        // Arrange
        Long id = 1L;
        doNothing().when(service).delete(id);

        // Act
        String viewName = controller.delete(id, redirectAttributes);

        // Assert
        assertThat(viewName).isEqualTo("redirect:/web/cuzk/unit-types");
        verify(service).delete(id);
        verify(redirectAttributes).addFlashAttribute("successMessage", "Record deleted successfully");
    }

    @Test
    @DisplayName("delete() should handle exception and redirect with error message")
    void testDeleteException() {
        // Arrange
        Long id = 1L;
        doThrow(new RuntimeException("Delete failed")).when(service).delete(id);

        // Act
        String viewName = controller.delete(id, redirectAttributes);

        // Assert
        assertThat(viewName).isEqualTo("redirect:/web/cuzk/unit-types");
        verify(service).delete(id);
        verify(redirectAttributes).addFlashAttribute("errorMessage", "Delete failed");
    }
}

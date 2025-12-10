package cz.intelis.legislativeenums.cuzk.soilecologicalunit;

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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("SoilEcologicalUnitWebController Unit Tests")
class SoilEcologicalUnitWebControllerTest {

    @Mock
    private SoilEcologicalUnitService service;

    @Mock
    private Model model;

    @Mock
    private BindingResult bindingResult;

    @Mock
    private RedirectAttributes redirectAttributes;

    @InjectMocks
    private SoilEcologicalUnitWebController controller;

    private SoilEcologicalUnitDTO testDTO;

    @BeforeEach
    void setUp() {
        testDTO = new SoilEcologicalUnitDTO();
        testDTO.setId(1L);
        testDTO.setCode("BPEJ001");
        testDTO.setNameCs("Testovací BPEJ");
        testDTO.setNameEn("Test BPEJ");
        testDTO.setDescriptionCs("Popis česky");
        testDTO.setDescriptionEn("Description in English");
        testDTO.setPrice(new BigDecimal("10000.00"));
        testDTO.setDetailedDescription("Detailed description");
        testDTO.setValidFrom(LocalDate.of(2024, 1, 1));
        testDTO.setValidTo(LocalDate.of(2025, 12, 31));
        testDTO.setSortOrder(1);
    }

    @Test
    @DisplayName("list() should add currently valid items to model and return list view")
    void testList() {
        // Given
        List<SoilEcologicalUnitDTO> items = Arrays.asList(testDTO);
        when(service.findAllCurrentlyValid()).thenReturn(items);

        // When
        String viewName = controller.list(false, model);

        // Then
        assertEquals("cuzk/soil-ecological-units/list", viewName);
        verify(service).findAllCurrentlyValid();
        verify(model).addAttribute("items", items);
    }

    @Test
    @DisplayName("list() should add all items to model when showAll is true")
    void testListShowAll() {
        // Given
        List<SoilEcologicalUnitDTO> items = Arrays.asList(testDTO);
        when(service.findAll()).thenReturn(items);

        // When
        String viewName = controller.list(true, model);

        // Then
        assertEquals("cuzk/soil-ecological-units/list", viewName);
        verify(service).findAll();
        verify(model).addAttribute("items", items);
    }

    @Test
    @DisplayName("createForm() should add empty DTO to model and return form view")
    void testCreateForm() {
        // When
        String viewName = controller.createForm(model);

        // Then
        assertEquals("cuzk/soil-ecological-units/form", viewName);
        verify(model).addAttribute(eq("item"), any(SoilEcologicalUnitDTO.class));
    }

    @Test
    @DisplayName("create() should create record and redirect to list on success")
    void testCreateSuccess() {
        // Given
        when(bindingResult.hasErrors()).thenReturn(false);
        when(service.create(any(SoilEcologicalUnitDTO.class))).thenReturn(testDTO);

        // When
        String viewName = controller.create(testDTO, bindingResult, redirectAttributes);

        // Then
        assertEquals("redirect:/web/cuzk/soil-ecological-units", viewName);
        verify(service).create(testDTO);
        verify(redirectAttributes).addFlashAttribute("successMessage", "Record created successfully");
        verify(redirectAttributes, never()).addFlashAttribute(eq("errorMessage"), anyString());
    }

    @Test
    @DisplayName("create() should return form view when validation errors exist")
    void testCreateValidationError() {
        // Given
        when(bindingResult.hasErrors()).thenReturn(true);

        // When
        String viewName = controller.create(testDTO, bindingResult, redirectAttributes);

        // Then
        assertEquals("cuzk/soil-ecological-units/form", viewName);
        verify(service, never()).create(any());
        verify(redirectAttributes, never()).addFlashAttribute(anyString(), anyString());
    }

    @Test
    @DisplayName("create() should redirect to create form when exception occurs")
    void testCreateException() {
        // Given
        when(bindingResult.hasErrors()).thenReturn(false);
        when(service.create(any(SoilEcologicalUnitDTO.class)))
                .thenThrow(new RuntimeException("Database error"));

        // When
        String viewName = controller.create(testDTO, bindingResult, redirectAttributes);

        // Then
        assertEquals("redirect:/web/cuzk/soil-ecological-units/create", viewName);
        verify(service).create(testDTO);
        verify(redirectAttributes).addFlashAttribute("errorMessage", "Database error");
        verify(redirectAttributes, never()).addFlashAttribute(eq("successMessage"), anyString());
    }

    @Test
    @DisplayName("editForm() should add item to model and return form view")
    void testEditForm() {
        // Given
        Long id = 1L;
        when(service.findById(id)).thenReturn(testDTO);

        // When
        String viewName = controller.editForm(id, model);

        // Then
        assertEquals("cuzk/soil-ecological-units/form", viewName);
        verify(service).findById(id);
        verify(model).addAttribute("item", testDTO);
    }

    @Test
    @DisplayName("update() should update record and redirect to list on success")
    void testUpdateSuccess() {
        // Given
        Long id = 1L;
        when(bindingResult.hasErrors()).thenReturn(false);
        when(service.update(eq(id), any(SoilEcologicalUnitDTO.class))).thenReturn(testDTO);

        // When
        String viewName = controller.update(id, testDTO, bindingResult, redirectAttributes);

        // Then
        assertEquals("redirect:/web/cuzk/soil-ecological-units", viewName);
        verify(service).update(id, testDTO);
        verify(redirectAttributes).addFlashAttribute("successMessage", "Record updated successfully");
        verify(redirectAttributes, never()).addFlashAttribute(eq("errorMessage"), anyString());
    }

    @Test
    @DisplayName("update() should return form view when validation errors exist")
    void testUpdateValidationError() {
        // Given
        Long id = 1L;
        when(bindingResult.hasErrors()).thenReturn(true);

        // When
        String viewName = controller.update(id, testDTO, bindingResult, redirectAttributes);

        // Then
        assertEquals("cuzk/soil-ecological-units/form", viewName);
        verify(service, never()).update(anyLong(), any());
        verify(redirectAttributes, never()).addFlashAttribute(anyString(), anyString());
    }

    @Test
    @DisplayName("update() should redirect to edit form when exception occurs")
    void testUpdateException() {
        // Given
        Long id = 1L;
        when(bindingResult.hasErrors()).thenReturn(false);
        when(service.update(eq(id), any(SoilEcologicalUnitDTO.class)))
                .thenThrow(new RuntimeException("Update failed"));

        // When
        String viewName = controller.update(id, testDTO, bindingResult, redirectAttributes);

        // Then
        assertEquals("redirect:/web/cuzk/soil-ecological-units/" + id + "/edit", viewName);
        verify(service).update(id, testDTO);
        verify(redirectAttributes).addFlashAttribute("errorMessage", "Update failed");
        verify(redirectAttributes, never()).addFlashAttribute(eq("successMessage"), anyString());
    }

    @Test
    @DisplayName("delete() should delete record and redirect to list on success")
    void testDeleteSuccess() {
        // Given
        Long id = 1L;
        doNothing().when(service).delete(id);

        // When
        String viewName = controller.delete(id, redirectAttributes);

        // Then
        assertEquals("redirect:/web/cuzk/soil-ecological-units", viewName);
        verify(service).delete(id);
        verify(redirectAttributes).addFlashAttribute("successMessage", "Record deleted successfully");
        verify(redirectAttributes, never()).addFlashAttribute(eq("errorMessage"), anyString());
    }

    @Test
    @DisplayName("delete() should add error message and redirect to list when exception occurs")
    void testDeleteException() {
        // Given
        Long id = 1L;
        doThrow(new RuntimeException("Cannot delete")).when(service).delete(id);

        // When
        String viewName = controller.delete(id, redirectAttributes);

        // Then
        assertEquals("redirect:/web/cuzk/soil-ecological-units", viewName);
        verify(service).delete(id);
        verify(redirectAttributes).addFlashAttribute("errorMessage", "Cannot delete");
        verify(redirectAttributes, never()).addFlashAttribute(eq("successMessage"), anyString());
    }
}

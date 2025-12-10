package cz.intelis.legislativeenums.cuzk.buildingtype;

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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("BuildingTypeWebController Unit Tests")
class BuildingTypeWebControllerTest {

    @Mock
    private BuildingTypeService service;

    @Mock
    private Model model;

    @Mock
    private BindingResult bindingResult;

    @Mock
    private RedirectAttributes redirectAttributes;

    @InjectMocks
    private BuildingTypeWebController controller;

    private BuildingTypeDTO testDTO;

    @BeforeEach
    void setUp() {
        testDTO = new BuildingTypeDTO();
        testDTO.setId(1L);
        testDTO.setCode("1");
        testDTO.setNameCs("budova s číslem popisným");
        testDTO.setNameEn("building with descriptive number");
        testDTO.setSortOrder(1);
    }

    @Test
    @DisplayName("Should display list of currently valid building types")
    void shouldDisplayListOfBuildingTypes() {
        List<BuildingTypeDTO> items = Arrays.asList(testDTO);
        when(service.findAllCurrentlyValid()).thenReturn(items);

        String viewName = controller.list(false, model);

        assertThat(viewName).isEqualTo("cuzk/building-types/list");
        verify(model).addAttribute("buildingTypes", items);
    }

    @Test
    @DisplayName("Should display list of all building types when showAll is true")
    void shouldDisplayListOfAllBuildingTypes() {
        List<BuildingTypeDTO> items = Arrays.asList(testDTO);
        when(service.findAll()).thenReturn(items);

        String viewName = controller.list(true, model);

        assertThat(viewName).isEqualTo("cuzk/building-types/list");
        verify(model).addAttribute("buildingTypes", items);
    }

    @Test
    @DisplayName("Should display create form")
    void shouldDisplayCreateForm() {
        String viewName = controller.createForm(model);

        assertThat(viewName).isEqualTo("cuzk/building-types/form");
        verify(model).addAttribute(eq("buildingType"), any(BuildingTypeDTO.class));
    }

    @Test
    @DisplayName("Should create building type successfully")
    void shouldCreateSuccessfully() {
        when(bindingResult.hasErrors()).thenReturn(false);
        when(service.create(any(BuildingTypeDTO.class))).thenReturn(testDTO);

        String viewName = controller.create(testDTO, bindingResult, redirectAttributes);

        assertThat(viewName).isEqualTo("redirect:/web/cuzk/building-types");
        verify(service).create(testDTO);
        verify(redirectAttributes).addFlashAttribute(eq("successMessage"), anyString());
    }

    @Test
    @DisplayName("Should return to form when validation errors on create")
    void shouldReturnToFormWhenValidationErrorsOnCreate() {
        when(bindingResult.hasErrors()).thenReturn(true);

        String viewName = controller.create(testDTO, bindingResult, redirectAttributes);

        assertThat(viewName).isEqualTo("cuzk/building-types/form");
        verify(service, never()).create(any());
    }

    @Test
    @DisplayName("Should handle exception on create")
    void shouldHandleExceptionOnCreate() {
        when(bindingResult.hasErrors()).thenReturn(false);
        when(service.create(any(BuildingTypeDTO.class))).thenThrow(new RuntimeException("Cannot create"));

        String viewName = controller.create(testDTO, bindingResult, redirectAttributes);

        assertThat(viewName).isEqualTo("redirect:/web/cuzk/building-types/create");
        verify(redirectAttributes).addFlashAttribute(eq("errorMessage"), anyString());
    }

    @Test
    @DisplayName("Should display edit form")
    void shouldDisplayEditForm() {
        when(service.findById(1L)).thenReturn(testDTO);

        String viewName = controller.editForm(1L, model);

        assertThat(viewName).isEqualTo("cuzk/building-types/form");
        verify(model).addAttribute("buildingType", testDTO);
    }

    @Test
    @DisplayName("Should update successfully")
    void shouldUpdateSuccessfully() {
        when(bindingResult.hasErrors()).thenReturn(false);
        when(service.update(eq(1L), any(BuildingTypeDTO.class))).thenReturn(testDTO);

        String viewName = controller.update(1L, testDTO, bindingResult, redirectAttributes);

        assertThat(viewName).isEqualTo("redirect:/web/cuzk/building-types");
        verify(service).update(1L, testDTO);
        verify(redirectAttributes).addFlashAttribute(eq("successMessage"), anyString());
    }

    @Test
    @DisplayName("Should return to form when validation errors on update")
    void shouldReturnToFormWhenValidationErrorsOnUpdate() {
        when(bindingResult.hasErrors()).thenReturn(true);

        String viewName = controller.update(1L, testDTO, bindingResult, redirectAttributes);

        assertThat(viewName).isEqualTo("cuzk/building-types/form");
        verify(service, never()).update(anyLong(), any());
    }

    @Test
    @DisplayName("Should handle exception on update")
    void shouldHandleExceptionOnUpdate() {
        when(bindingResult.hasErrors()).thenReturn(false);
        when(service.update(eq(1L), any(BuildingTypeDTO.class))).thenThrow(new RuntimeException("Cannot update"));

        String viewName = controller.update(1L, testDTO, bindingResult, redirectAttributes);

        assertThat(viewName).isEqualTo("redirect:/web/cuzk/building-types/1/edit");
        verify(redirectAttributes).addFlashAttribute(eq("errorMessage"), anyString());
    }

    @Test
    @DisplayName("Should delete successfully")
    void shouldDeleteSuccessfully() {
        doNothing().when(service).delete(1L);

        String viewName = controller.delete(1L, redirectAttributes);

        assertThat(viewName).isEqualTo("redirect:/web/cuzk/building-types");
        verify(service).delete(1L);
        verify(redirectAttributes).addFlashAttribute(eq("successMessage"), anyString());
    }

    @Test
    @DisplayName("Should handle exception on delete")
    void shouldHandleExceptionOnDelete() {
        doThrow(new RuntimeException("Cannot delete")).when(service).delete(1L);

        String viewName = controller.delete(1L, redirectAttributes);

        assertThat(viewName).isEqualTo("redirect:/web/cuzk/building-types");
        verify(redirectAttributes).addFlashAttribute(eq("errorMessage"), anyString());
    }
}

package cz.intelis.legislativeenums.cuzk.landuse;

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
@DisplayName("LandUseWebController Unit Tests")
class LandUseWebControllerTest {

    @Mock
    private LandUseService service;

    @Mock
    private Model model;

    @Mock
    private BindingResult bindingResult;

    @Mock
    private RedirectAttributes redirectAttributes;

    @InjectMocks
    private LandUseWebController controller;

    private LandUseDTO testDTO;

    @BeforeEach
    void setUp() {
        testDTO = new LandUseDTO();
        testDTO.setId(1L);
        testDTO.setCode("1");
        testDTO.setNameCs("zahrady a sady");
        testDTO.setNameEn("gardens and orchards");
        testDTO.setSortOrder(1);
    }

    @Test
    @DisplayName("Should display list of currently valid land uses")
    void shouldDisplayListOfLandUses() {
        List<LandUseDTO> items = Arrays.asList(testDTO);
        when(service.findAllCurrentlyValid()).thenReturn(items);

        String viewName = controller.list(false, model);

        assertThat(viewName).isEqualTo("cuzk/land-uses/list");
        verify(model).addAttribute("landUses", items);
    }

    @Test
    @DisplayName("Should display list of all land uses when showAll is true")
    void shouldDisplayListOfAllLandUses() {
        List<LandUseDTO> items = Arrays.asList(testDTO);
        when(service.findAll()).thenReturn(items);

        String viewName = controller.list(true, model);

        assertThat(viewName).isEqualTo("cuzk/land-uses/list");
        verify(model).addAttribute("landUses", items);
    }

    @Test
    @DisplayName("Should display create form")
    void shouldDisplayCreateForm() {
        String viewName = controller.createForm(model);

        assertThat(viewName).isEqualTo("cuzk/land-uses/form");
        verify(model).addAttribute(eq("landUse"), any(LandUseDTO.class));
    }

    @Test
    @DisplayName("Should create land use successfully")
    void shouldCreateSuccessfully() {
        when(bindingResult.hasErrors()).thenReturn(false);
        when(service.create(any(LandUseDTO.class))).thenReturn(testDTO);

        String viewName = controller.create(testDTO, bindingResult, redirectAttributes);

        assertThat(viewName).isEqualTo("redirect:/web/cuzk/land-uses");
        verify(service).create(testDTO);
        verify(redirectAttributes).addFlashAttribute(eq("successMessage"), anyString());
    }

    @Test
    @DisplayName("Should return to form when validation errors on create")
    void shouldReturnToFormWhenValidationErrorsOnCreate() {
        when(bindingResult.hasErrors()).thenReturn(true);

        String viewName = controller.create(testDTO, bindingResult, redirectAttributes);

        assertThat(viewName).isEqualTo("cuzk/land-uses/form");
        verify(service, never()).create(any());
    }

    @Test
    @DisplayName("Should handle exception on create")
    void shouldHandleExceptionOnCreate() {
        when(bindingResult.hasErrors()).thenReturn(false);
        when(service.create(any(LandUseDTO.class))).thenThrow(new RuntimeException("Cannot create"));

        String viewName = controller.create(testDTO, bindingResult, redirectAttributes);

        assertThat(viewName).isEqualTo("redirect:/web/cuzk/land-uses/create");
        verify(redirectAttributes).addFlashAttribute(eq("errorMessage"), anyString());
    }

    @Test
    @DisplayName("Should display edit form")
    void shouldDisplayEditForm() {
        when(service.findById(1L)).thenReturn(testDTO);

        String viewName = controller.editForm(1L, model);

        assertThat(viewName).isEqualTo("cuzk/land-uses/form");
        verify(model).addAttribute("landUse", testDTO);
    }

    @Test
    @DisplayName("Should update successfully")
    void shouldUpdateSuccessfully() {
        when(bindingResult.hasErrors()).thenReturn(false);
        when(service.update(eq(1L), any(LandUseDTO.class))).thenReturn(testDTO);

        String viewName = controller.update(1L, testDTO, bindingResult, redirectAttributes);

        assertThat(viewName).isEqualTo("redirect:/web/cuzk/land-uses");
        verify(service).update(1L, testDTO);
        verify(redirectAttributes).addFlashAttribute(eq("successMessage"), anyString());
    }

    @Test
    @DisplayName("Should return to form when validation errors on update")
    void shouldReturnToFormWhenValidationErrorsOnUpdate() {
        when(bindingResult.hasErrors()).thenReturn(true);

        String viewName = controller.update(1L, testDTO, bindingResult, redirectAttributes);

        assertThat(viewName).isEqualTo("cuzk/land-uses/form");
        verify(service, never()).update(anyLong(), any());
    }

    @Test
    @DisplayName("Should handle exception on update")
    void shouldHandleExceptionOnUpdate() {
        when(bindingResult.hasErrors()).thenReturn(false);
        when(service.update(eq(1L), any(LandUseDTO.class))).thenThrow(new RuntimeException("Cannot update"));

        String viewName = controller.update(1L, testDTO, bindingResult, redirectAttributes);

        assertThat(viewName).isEqualTo("redirect:/web/cuzk/land-uses/1/edit");
        verify(redirectAttributes).addFlashAttribute(eq("errorMessage"), anyString());
    }

    @Test
    @DisplayName("Should delete successfully")
    void shouldDeleteSuccessfully() {
        doNothing().when(service).delete(1L);

        String viewName = controller.delete(1L, redirectAttributes);

        assertThat(viewName).isEqualTo("redirect:/web/cuzk/land-uses");
        verify(service).delete(1L);
        verify(redirectAttributes).addFlashAttribute(eq("successMessage"), anyString());
    }

    @Test
    @DisplayName("Should handle exception on delete")
    void shouldHandleExceptionOnDelete() {
        doThrow(new RuntimeException("Cannot delete")).when(service).delete(1L);

        String viewName = controller.delete(1L, redirectAttributes);

        assertThat(viewName).isEqualTo("redirect:/web/cuzk/land-uses");
        verify(redirectAttributes).addFlashAttribute(eq("errorMessage"), anyString());
    }
}

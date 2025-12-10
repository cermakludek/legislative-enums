package cz.intelis.legislativeenums.cuzk.landtype;

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
@DisplayName("LandTypeWebController Unit Tests")
class LandTypeWebControllerTest {

    @Mock
    private LandTypeService service;

    @Mock
    private Model model;

    @Mock
    private BindingResult bindingResult;

    @Mock
    private RedirectAttributes redirectAttributes;

    @InjectMocks
    private LandTypeWebController controller;

    private LandTypeDTO testDTO;

    @BeforeEach
    void setUp() {
        testDTO = new LandTypeDTO();
        testDTO.setId(1L);
        testDTO.setCode("2");
        testDTO.setNameCs("orná půda");
        testDTO.setNameEn("arable land");
        testDTO.setSortOrder(1);
    }

    @Test
    @DisplayName("Should display list of currently valid land types")
    void shouldDisplayListOfLandTypes() {
        List<LandTypeDTO> items = Arrays.asList(testDTO);
        when(service.findAllCurrentlyValid()).thenReturn(items);

        String viewName = controller.list(false, model);

        assertThat(viewName).isEqualTo("cuzk/land-types/list");
        verify(model).addAttribute("landTypes", items);
    }

    @Test
    @DisplayName("Should display list of all land types when showAll is true")
    void shouldDisplayListOfAllLandTypes() {
        List<LandTypeDTO> items = Arrays.asList(testDTO);
        when(service.findAll()).thenReturn(items);

        String viewName = controller.list(true, model);

        assertThat(viewName).isEqualTo("cuzk/land-types/list");
        verify(model).addAttribute("landTypes", items);
    }

    @Test
    @DisplayName("Should display create form")
    void shouldDisplayCreateForm() {
        String viewName = controller.createForm(model);

        assertThat(viewName).isEqualTo("cuzk/land-types/form");
        verify(model).addAttribute(eq("landType"), any(LandTypeDTO.class));
    }

    @Test
    @DisplayName("Should create land type successfully")
    void shouldCreateSuccessfully() {
        when(bindingResult.hasErrors()).thenReturn(false);
        when(service.create(any(LandTypeDTO.class))).thenReturn(testDTO);

        String viewName = controller.create(testDTO, bindingResult, redirectAttributes);

        assertThat(viewName).isEqualTo("redirect:/web/cuzk/land-types");
        verify(service).create(testDTO);
        verify(redirectAttributes).addFlashAttribute(eq("successMessage"), anyString());
    }

    @Test
    @DisplayName("Should return to form when validation errors on create")
    void shouldReturnToFormWhenValidationErrorsOnCreate() {
        when(bindingResult.hasErrors()).thenReturn(true);

        String viewName = controller.create(testDTO, bindingResult, redirectAttributes);

        assertThat(viewName).isEqualTo("cuzk/land-types/form");
        verify(service, never()).create(any());
    }

    @Test
    @DisplayName("Should handle exception on create")
    void shouldHandleExceptionOnCreate() {
        when(bindingResult.hasErrors()).thenReturn(false);
        when(service.create(any(LandTypeDTO.class))).thenThrow(new RuntimeException("Cannot create"));

        String viewName = controller.create(testDTO, bindingResult, redirectAttributes);

        assertThat(viewName).isEqualTo("redirect:/web/cuzk/land-types/create");
        verify(redirectAttributes).addFlashAttribute(eq("errorMessage"), anyString());
    }

    @Test
    @DisplayName("Should display edit form")
    void shouldDisplayEditForm() {
        when(service.findById(1L)).thenReturn(testDTO);

        String viewName = controller.editForm(1L, model);

        assertThat(viewName).isEqualTo("cuzk/land-types/form");
        verify(model).addAttribute("landType", testDTO);
    }

    @Test
    @DisplayName("Should update successfully")
    void shouldUpdateSuccessfully() {
        when(bindingResult.hasErrors()).thenReturn(false);
        when(service.update(eq(1L), any(LandTypeDTO.class))).thenReturn(testDTO);

        String viewName = controller.update(1L, testDTO, bindingResult, redirectAttributes);

        assertThat(viewName).isEqualTo("redirect:/web/cuzk/land-types");
        verify(service).update(1L, testDTO);
        verify(redirectAttributes).addFlashAttribute(eq("successMessage"), anyString());
    }

    @Test
    @DisplayName("Should return to form when validation errors on update")
    void shouldReturnToFormWhenValidationErrorsOnUpdate() {
        when(bindingResult.hasErrors()).thenReturn(true);

        String viewName = controller.update(1L, testDTO, bindingResult, redirectAttributes);

        assertThat(viewName).isEqualTo("cuzk/land-types/form");
        verify(service, never()).update(anyLong(), any());
    }

    @Test
    @DisplayName("Should handle exception on update")
    void shouldHandleExceptionOnUpdate() {
        when(bindingResult.hasErrors()).thenReturn(false);
        when(service.update(eq(1L), any(LandTypeDTO.class))).thenThrow(new RuntimeException("Cannot update"));

        String viewName = controller.update(1L, testDTO, bindingResult, redirectAttributes);

        assertThat(viewName).isEqualTo("redirect:/web/cuzk/land-types/1/edit");
        verify(redirectAttributes).addFlashAttribute(eq("errorMessage"), anyString());
    }

    @Test
    @DisplayName("Should delete successfully")
    void shouldDeleteSuccessfully() {
        doNothing().when(service).delete(1L);

        String viewName = controller.delete(1L, redirectAttributes);

        assertThat(viewName).isEqualTo("redirect:/web/cuzk/land-types");
        verify(service).delete(1L);
        verify(redirectAttributes).addFlashAttribute(eq("successMessage"), anyString());
    }

    @Test
    @DisplayName("Should handle exception on delete")
    void shouldHandleExceptionOnDelete() {
        doThrow(new RuntimeException("Cannot delete")).when(service).delete(1L);

        String viewName = controller.delete(1L, redirectAttributes);

        assertThat(viewName).isEqualTo("redirect:/web/cuzk/land-types");
        verify(redirectAttributes).addFlashAttribute(eq("errorMessage"), anyString());
    }
}

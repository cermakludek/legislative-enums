package cz.intelis.legislativeenums.cuzk.areadetermination;

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
@DisplayName("AreaDeterminationWebController Unit Tests")
class AreaDeterminationWebControllerTest {

    @Mock
    private AreaDeterminationService service;

    @Mock
    private Model model;

    @Mock
    private BindingResult bindingResult;

    @Mock
    private RedirectAttributes redirectAttributes;

    @InjectMocks
    private AreaDeterminationWebController controller;

    private AreaDeterminationDTO testDTO;

    @BeforeEach
    void setUp() {
        testDTO = new AreaDeterminationDTO();
        testDTO.setId(1L);
        testDTO.setCode("1");
        testDTO.setNameCs("ze souřadnic v S-JTSK");
        testDTO.setNameEn("from coordinates in S-JTSK");
        testDTO.setSortOrder(1);
    }

    @Test
    @DisplayName("Should display list of currently valid area determinations")
    void shouldDisplayListOfAreaDeterminations() {
        List<AreaDeterminationDTO> items = Arrays.asList(testDTO);
        when(service.findAllCurrentlyValid()).thenReturn(items);

        String viewName = controller.list(false, model);

        assertThat(viewName).isEqualTo("cuzk/area-determinations/list");
        verify(model).addAttribute("areaDeterminations", items);
    }

    @Test
    @DisplayName("Should display list of all area determinations when showAll is true")
    void shouldDisplayListOfAllAreaDeterminations() {
        List<AreaDeterminationDTO> items = Arrays.asList(testDTO);
        when(service.findAll()).thenReturn(items);

        String viewName = controller.list(true, model);

        assertThat(viewName).isEqualTo("cuzk/area-determinations/list");
        verify(model).addAttribute("areaDeterminations", items);
    }

    @Test
    @DisplayName("Should display create form")
    void shouldDisplayCreateForm() {
        String viewName = controller.createForm(model);

        assertThat(viewName).isEqualTo("cuzk/area-determinations/form");
        verify(model).addAttribute(eq("areaDetermination"), any(AreaDeterminationDTO.class));
    }

    @Test
    @DisplayName("Should create area determination successfully")
    void shouldCreateSuccessfully() {
        when(bindingResult.hasErrors()).thenReturn(false);
        when(service.create(any(AreaDeterminationDTO.class))).thenReturn(testDTO);

        String viewName = controller.create(testDTO, bindingResult, redirectAttributes);

        assertThat(viewName).isEqualTo("redirect:/web/cuzk/area-determinations");
        verify(service).create(testDTO);
        verify(redirectAttributes).addFlashAttribute(eq("successMessage"), anyString());
    }

    @Test
    @DisplayName("Should return to form when validation errors on create")
    void shouldReturnToFormWhenValidationErrorsOnCreate() {
        when(bindingResult.hasErrors()).thenReturn(true);

        String viewName = controller.create(testDTO, bindingResult, redirectAttributes);

        assertThat(viewName).isEqualTo("cuzk/area-determinations/form");
        verify(service, never()).create(any());
    }

    @Test
    @DisplayName("Should handle exception on create")
    void shouldHandleExceptionOnCreate() {
        when(bindingResult.hasErrors()).thenReturn(false);
        when(service.create(any(AreaDeterminationDTO.class))).thenThrow(new RuntimeException("Cannot create"));

        String viewName = controller.create(testDTO, bindingResult, redirectAttributes);

        assertThat(viewName).isEqualTo("redirect:/web/cuzk/area-determinations/create");
        verify(redirectAttributes).addFlashAttribute(eq("errorMessage"), anyString());
    }

    @Test
    @DisplayName("Should display edit form")
    void shouldDisplayEditForm() {
        when(service.findById(1L)).thenReturn(testDTO);

        String viewName = controller.editForm(1L, model);

        assertThat(viewName).isEqualTo("cuzk/area-determinations/form");
        verify(model).addAttribute("areaDetermination", testDTO);
    }

    @Test
    @DisplayName("Should update successfully")
    void shouldUpdateSuccessfully() {
        when(bindingResult.hasErrors()).thenReturn(false);
        when(service.update(eq(1L), any(AreaDeterminationDTO.class))).thenReturn(testDTO);

        String viewName = controller.update(1L, testDTO, bindingResult, redirectAttributes);

        assertThat(viewName).isEqualTo("redirect:/web/cuzk/area-determinations");
        verify(service).update(1L, testDTO);
        verify(redirectAttributes).addFlashAttribute(eq("successMessage"), anyString());
    }

    @Test
    @DisplayName("Should return to form when validation errors on update")
    void shouldReturnToFormWhenValidationErrorsOnUpdate() {
        when(bindingResult.hasErrors()).thenReturn(true);

        String viewName = controller.update(1L, testDTO, bindingResult, redirectAttributes);

        assertThat(viewName).isEqualTo("cuzk/area-determinations/form");
        verify(service, never()).update(anyLong(), any());
    }

    @Test
    @DisplayName("Should handle exception on update")
    void shouldHandleExceptionOnUpdate() {
        when(bindingResult.hasErrors()).thenReturn(false);
        when(service.update(eq(1L), any(AreaDeterminationDTO.class))).thenThrow(new RuntimeException("Cannot update"));

        String viewName = controller.update(1L, testDTO, bindingResult, redirectAttributes);

        assertThat(viewName).isEqualTo("redirect:/web/cuzk/area-determinations/1/edit");
        verify(redirectAttributes).addFlashAttribute(eq("errorMessage"), anyString());
    }

    @Test
    @DisplayName("Should delete successfully")
    void shouldDeleteSuccessfully() {
        doNothing().when(service).delete(1L);

        String viewName = controller.delete(1L, redirectAttributes);

        assertThat(viewName).isEqualTo("redirect:/web/cuzk/area-determinations");
        verify(service).delete(1L);
        verify(redirectAttributes).addFlashAttribute(eq("successMessage"), anyString());
    }

    @Test
    @DisplayName("Should handle exception on delete")
    void shouldHandleExceptionOnDelete() {
        doThrow(new RuntimeException("Cannot delete")).when(service).delete(1L);

        String viewName = controller.delete(1L, redirectAttributes);

        assertThat(viewName).isEqualTo("redirect:/web/cuzk/area-determinations");
        verify(redirectAttributes).addFlashAttribute(eq("errorMessage"), anyString());
    }
}

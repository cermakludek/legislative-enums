package cz.intelis.legislativeenums.flag;

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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("FlagWebController Unit Tests")
class FlagWebControllerTest {

    @Mock
    private FlagService flagService;

    @Mock
    private Model model;

    @Mock
    private BindingResult bindingResult;

    @Mock
    private RedirectAttributes redirectAttributes;

    @InjectMocks
    private FlagWebController controller;

    private FlagDTO testDTO;

    @BeforeEach
    void setUp() {
        testDTO = new FlagDTO();
        testDTO.setId(1L);
        testDTO.setCode("IMPORTANT");
        testDTO.setNameCs("Důležité");
        testDTO.setNameEn("Important");
        testDTO.setDescriptionCs("Důležitý číselník");
        testDTO.setDescriptionEn("Important codelist");
        testDTO.setColor("#FF0000");
        testDTO.setIconClass("fas fa-star");
        testDTO.setActive(true);
        testDTO.setSortOrder(1);
    }

    @Test
    @DisplayName("Should display list of currently valid flags")
    void shouldDisplayListOfFlags() {
        // Given
        List<FlagDTO> flags = Arrays.asList(testDTO);
        when(flagService.findAllCurrentlyValid()).thenReturn(flags);

        // When
        String viewName = controller.list(false, model);

        // Then
        assertThat(viewName).isEqualTo("flags/list");
        verify(model).addAttribute("flags", flags);
        verify(flagService).findAllCurrentlyValid();
    }

    @Test
    @DisplayName("Should display list of all flags when showAll is true")
    void shouldDisplayListOfAllFlags() {
        // Given
        List<FlagDTO> flags = Arrays.asList(testDTO);
        when(flagService.findAll()).thenReturn(flags);

        // When
        String viewName = controller.list(true, model);

        // Then
        assertThat(viewName).isEqualTo("flags/list");
        verify(model).addAttribute("flags", flags);
        verify(flagService).findAll();
    }

    @Test
    @DisplayName("Should display create form")
    void shouldDisplayCreateForm() {
        // When
        String viewName = controller.createForm(model);

        // Then
        assertThat(viewName).isEqualTo("flags/form");
        verify(model).addAttribute(eq("flag"), any(FlagDTO.class));
    }

    @Test
    @DisplayName("Should create flag successfully")
    void shouldCreateFlagSuccessfully() {
        // Given
        when(bindingResult.hasErrors()).thenReturn(false);
        when(flagService.create(any(FlagDTO.class))).thenReturn(testDTO);

        // When
        String viewName = controller.create(testDTO, bindingResult, redirectAttributes);

        // Then
        assertThat(viewName).isEqualTo("redirect:/web/flags");
        verify(flagService).create(testDTO);
        verify(redirectAttributes).addFlashAttribute(eq("successMessage"), anyString());
    }

    @Test
    @DisplayName("Should return to form when validation errors on create")
    void shouldReturnToFormWhenValidationErrorsOnCreate() {
        // Given
        when(bindingResult.hasErrors()).thenReturn(true);

        // When
        String viewName = controller.create(testDTO, bindingResult, redirectAttributes);

        // Then
        assertThat(viewName).isEqualTo("flags/form");
        verify(flagService, never()).create(any());
    }

    @Test
    @DisplayName("Should handle exception on create")
    void shouldHandleExceptionOnCreate() {
        // Given
        when(bindingResult.hasErrors()).thenReturn(false);
        when(flagService.create(any(FlagDTO.class)))
            .thenThrow(new RuntimeException("Database error"));

        // When
        String viewName = controller.create(testDTO, bindingResult, redirectAttributes);

        // Then
        assertThat(viewName).isEqualTo("redirect:/web/flags/create");
        verify(redirectAttributes).addFlashAttribute(eq("errorMessage"), anyString());
    }

    @Test
    @DisplayName("Should display edit form")
    void shouldDisplayEditForm() {
        // Given
        when(flagService.findById(1L)).thenReturn(testDTO);

        // When
        String viewName = controller.editForm(1L, model);

        // Then
        assertThat(viewName).isEqualTo("flags/form");
        verify(model).addAttribute("flag", testDTO);
    }

    @Test
    @DisplayName("Should update flag successfully")
    void shouldUpdateFlagSuccessfully() {
        // Given
        when(bindingResult.hasErrors()).thenReturn(false);
        when(flagService.update(eq(1L), any(FlagDTO.class))).thenReturn(testDTO);

        // When
        String viewName = controller.update(1L, testDTO, bindingResult, redirectAttributes);

        // Then
        assertThat(viewName).isEqualTo("redirect:/web/flags");
        verify(flagService).update(1L, testDTO);
        verify(redirectAttributes).addFlashAttribute(eq("successMessage"), anyString());
    }

    @Test
    @DisplayName("Should return to form when validation errors on update")
    void shouldReturnToFormWhenValidationErrorsOnUpdate() {
        // Given
        when(bindingResult.hasErrors()).thenReturn(true);

        // When
        String viewName = controller.update(1L, testDTO, bindingResult, redirectAttributes);

        // Then
        assertThat(viewName).isEqualTo("flags/form");
        verify(flagService, never()).update(anyLong(), any());
    }

    @Test
    @DisplayName("Should handle exception on update")
    void shouldHandleExceptionOnUpdate() {
        // Given
        when(bindingResult.hasErrors()).thenReturn(false);
        when(flagService.update(eq(1L), any(FlagDTO.class)))
            .thenThrow(new RuntimeException("Database error"));

        // When
        String viewName = controller.update(1L, testDTO, bindingResult, redirectAttributes);

        // Then
        assertThat(viewName).isEqualTo("redirect:/web/flags/1/edit");
        verify(redirectAttributes).addFlashAttribute(eq("errorMessage"), anyString());
    }

    @Test
    @DisplayName("Should delete flag successfully")
    void shouldDeleteFlagSuccessfully() {
        // Given
        doNothing().when(flagService).delete(1L);

        // When
        String viewName = controller.delete(1L, redirectAttributes);

        // Then
        assertThat(viewName).isEqualTo("redirect:/web/flags");
        verify(flagService).delete(1L);
        verify(redirectAttributes).addFlashAttribute(eq("successMessage"), anyString());
    }

    @Test
    @DisplayName("Should handle exception on delete")
    void shouldHandleExceptionOnDelete() {
        // Given
        doThrow(new RuntimeException("Cannot delete")).when(flagService).delete(1L);

        // When
        String viewName = controller.delete(1L, redirectAttributes);

        // Then
        assertThat(viewName).isEqualTo("redirect:/web/flags");
        verify(redirectAttributes).addFlashAttribute(eq("errorMessage"), anyString());
    }
}

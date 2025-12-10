package cz.intelis.legislativeenums.voltagelevel;

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
@DisplayName("VoltageLevelWebController Unit Tests")
class VoltageLevelWebControllerTest {

    @Mock
    private VoltageLevelService voltageLevelService;

    @Mock
    private Model model;

    @Mock
    private BindingResult bindingResult;

    @Mock
    private RedirectAttributes redirectAttributes;

    @InjectMocks
    private VoltageLevelWebController controller;

    private VoltageLevelDTO testDTO;

    @BeforeEach
    void setUp() {
        testDTO = new VoltageLevelDTO();
        testDTO.setId(1L);
        testDTO.setCode("NN");
        testDTO.setNameCs("Nízké napětí");
        testDTO.setNameEn("Low Voltage");
        testDTO.setVoltageRangeCs("do 1 kV");
        testDTO.setVoltageRangeEn("up to 1 kV");
        testDTO.setSortOrder(1);
    }

    @Test
    @DisplayName("Should display list of currently valid voltage levels")
    void shouldDisplayListOfVoltageLevels() {
        // Given
        List<VoltageLevelDTO> levels = Arrays.asList(testDTO);
        when(voltageLevelService.findAllCurrentlyValid()).thenReturn(levels);

        // When
        String viewName = controller.list(false, model);

        // Then
        assertThat(viewName).isEqualTo("voltage-levels/list");
        verify(model).addAttribute("voltageLevels", levels);
        verify(voltageLevelService).findAllCurrentlyValid();
    }

    @Test
    @DisplayName("Should display list of all voltage levels when showAll is true")
    void shouldDisplayListOfAllVoltageLevels() {
        // Given
        List<VoltageLevelDTO> levels = Arrays.asList(testDTO);
        when(voltageLevelService.findAll()).thenReturn(levels);

        // When
        String viewName = controller.list(true, model);

        // Then
        assertThat(viewName).isEqualTo("voltage-levels/list");
        verify(model).addAttribute("voltageLevels", levels);
        verify(voltageLevelService).findAll();
    }

    @Test
    @DisplayName("Should display create form")
    void shouldDisplayCreateForm() {
        // When
        String viewName = controller.createForm(model);

        // Then
        assertThat(viewName).isEqualTo("voltage-levels/form");
        verify(model).addAttribute(eq("voltageLevel"), any(VoltageLevelDTO.class));
    }

    @Test
    @DisplayName("Should create voltage level successfully")
    void shouldCreateVoltageLevelSuccessfully() {
        // Given
        when(bindingResult.hasErrors()).thenReturn(false);
        when(voltageLevelService.create(any(VoltageLevelDTO.class))).thenReturn(testDTO);

        // When
        String viewName = controller.create(testDTO, bindingResult, redirectAttributes);

        // Then
        assertThat(viewName).isEqualTo("redirect:/web/voltage-levels");
        verify(voltageLevelService).create(testDTO);
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
        assertThat(viewName).isEqualTo("voltage-levels/form");
        verify(voltageLevelService, never()).create(any());
    }

    @Test
    @DisplayName("Should handle exception on create")
    void shouldHandleExceptionOnCreate() {
        // Given
        when(bindingResult.hasErrors()).thenReturn(false);
        when(voltageLevelService.create(any(VoltageLevelDTO.class)))
            .thenThrow(new RuntimeException("Database error"));

        // When
        String viewName = controller.create(testDTO, bindingResult, redirectAttributes);

        // Then
        assertThat(viewName).isEqualTo("redirect:/web/voltage-levels/create");
        verify(redirectAttributes).addFlashAttribute(eq("errorMessage"), anyString());
    }

    @Test
    @DisplayName("Should display edit form")
    void shouldDisplayEditForm() {
        // Given
        when(voltageLevelService.findById(1L)).thenReturn(testDTO);

        // When
        String viewName = controller.editForm(1L, model);

        // Then
        assertThat(viewName).isEqualTo("voltage-levels/form");
        verify(model).addAttribute("voltageLevel", testDTO);
    }

    @Test
    @DisplayName("Should update voltage level successfully")
    void shouldUpdateVoltageLevelSuccessfully() {
        // Given
        when(bindingResult.hasErrors()).thenReturn(false);
        when(voltageLevelService.update(eq(1L), any(VoltageLevelDTO.class))).thenReturn(testDTO);

        // When
        String viewName = controller.update(1L, testDTO, bindingResult, redirectAttributes);

        // Then
        assertThat(viewName).isEqualTo("redirect:/web/voltage-levels");
        verify(voltageLevelService).update(1L, testDTO);
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
        assertThat(viewName).isEqualTo("voltage-levels/form");
        verify(voltageLevelService, never()).update(anyLong(), any());
    }

    @Test
    @DisplayName("Should handle exception on update")
    void shouldHandleExceptionOnUpdate() {
        // Given
        when(bindingResult.hasErrors()).thenReturn(false);
        when(voltageLevelService.update(eq(1L), any(VoltageLevelDTO.class)))
            .thenThrow(new RuntimeException("Database error"));

        // When
        String viewName = controller.update(1L, testDTO, bindingResult, redirectAttributes);

        // Then
        assertThat(viewName).isEqualTo("redirect:/web/voltage-levels/1/edit");
        verify(redirectAttributes).addFlashAttribute(eq("errorMessage"), anyString());
    }

    @Test
    @DisplayName("Should delete voltage level successfully")
    void shouldDeleteVoltageLevelSuccessfully() {
        // Given
        doNothing().when(voltageLevelService).delete(1L);

        // When
        String viewName = controller.delete(1L, redirectAttributes);

        // Then
        assertThat(viewName).isEqualTo("redirect:/web/voltage-levels");
        verify(voltageLevelService).delete(1L);
        verify(redirectAttributes).addFlashAttribute(eq("successMessage"), anyString());
    }

    @Test
    @DisplayName("Should handle exception on delete")
    void shouldHandleExceptionOnDelete() {
        // Given
        doThrow(new RuntimeException("Cannot delete")).when(voltageLevelService).delete(1L);

        // When
        String viewName = controller.delete(1L, redirectAttributes);

        // Then
        assertThat(viewName).isEqualTo("redirect:/web/voltage-levels");
        verify(redirectAttributes).addFlashAttribute(eq("errorMessage"), anyString());
    }
}

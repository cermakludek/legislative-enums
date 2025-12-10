package cz.intelis.legislativeenums.networktype;

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
@DisplayName("NetworkTypeWebController Unit Tests")
class NetworkTypeWebControllerTest {

    @Mock
    private NetworkTypeService networkTypeService;

    @Mock
    private Model model;

    @Mock
    private BindingResult bindingResult;

    @Mock
    private RedirectAttributes redirectAttributes;

    @InjectMocks
    private NetworkTypeWebController controller;

    private NetworkTypeDTO testDTO;

    @BeforeEach
    void setUp() {
        testDTO = new NetworkTypeDTO();
        testDTO.setId(1L);
        testDTO.setCode("DS");
        testDTO.setNameCs("Distribuční síť");
        testDTO.setNameEn("Distribution Network");
        testDTO.setDescriptionCs("Síť pro distribuci elektrické energie");
        testDTO.setDescriptionEn("Network for electricity distribution");
        testDTO.setSortOrder(1);
    }

    @Test
    @DisplayName("Should display list of currently valid network types")
    void shouldDisplayListOfNetworkTypes() {
        // Given
        List<NetworkTypeDTO> types = Arrays.asList(testDTO);
        when(networkTypeService.findAllCurrentlyValid()).thenReturn(types);

        // When
        String viewName = controller.list(false, model);

        // Then
        assertThat(viewName).isEqualTo("network-types/list");
        verify(model).addAttribute("networkTypes", types);
        verify(networkTypeService).findAllCurrentlyValid();
    }

    @Test
    @DisplayName("Should display list of all network types when showAll is true")
    void shouldDisplayListOfAllNetworkTypes() {
        // Given
        List<NetworkTypeDTO> types = Arrays.asList(testDTO);
        when(networkTypeService.findAll()).thenReturn(types);

        // When
        String viewName = controller.list(true, model);

        // Then
        assertThat(viewName).isEqualTo("network-types/list");
        verify(model).addAttribute("networkTypes", types);
        verify(networkTypeService).findAll();
    }

    @Test
    @DisplayName("Should display create form")
    void shouldDisplayCreateForm() {
        // When
        String viewName = controller.createForm(model);

        // Then
        assertThat(viewName).isEqualTo("network-types/form");
        verify(model).addAttribute(eq("networkType"), any(NetworkTypeDTO.class));
    }

    @Test
    @DisplayName("Should create network type successfully")
    void shouldCreateNetworkTypeSuccessfully() {
        // Given
        when(bindingResult.hasErrors()).thenReturn(false);
        when(networkTypeService.create(any(NetworkTypeDTO.class))).thenReturn(testDTO);

        // When
        String viewName = controller.create(testDTO, bindingResult, redirectAttributes);

        // Then
        assertThat(viewName).isEqualTo("redirect:/web/network-types");
        verify(networkTypeService).create(testDTO);
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
        assertThat(viewName).isEqualTo("network-types/form");
        verify(networkTypeService, never()).create(any());
    }

    @Test
    @DisplayName("Should handle exception on create")
    void shouldHandleExceptionOnCreate() {
        // Given
        when(bindingResult.hasErrors()).thenReturn(false);
        when(networkTypeService.create(any(NetworkTypeDTO.class)))
            .thenThrow(new RuntimeException("Database error"));

        // When
        String viewName = controller.create(testDTO, bindingResult, redirectAttributes);

        // Then
        assertThat(viewName).isEqualTo("redirect:/web/network-types/create");
        verify(redirectAttributes).addFlashAttribute(eq("errorMessage"), anyString());
    }

    @Test
    @DisplayName("Should display edit form")
    void shouldDisplayEditForm() {
        // Given
        when(networkTypeService.findById(1L)).thenReturn(testDTO);

        // When
        String viewName = controller.editForm(1L, model);

        // Then
        assertThat(viewName).isEqualTo("network-types/form");
        verify(model).addAttribute("networkType", testDTO);
    }

    @Test
    @DisplayName("Should update network type successfully")
    void shouldUpdateNetworkTypeSuccessfully() {
        // Given
        when(bindingResult.hasErrors()).thenReturn(false);
        when(networkTypeService.update(eq(1L), any(NetworkTypeDTO.class))).thenReturn(testDTO);

        // When
        String viewName = controller.update(1L, testDTO, bindingResult, redirectAttributes);

        // Then
        assertThat(viewName).isEqualTo("redirect:/web/network-types");
        verify(networkTypeService).update(1L, testDTO);
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
        assertThat(viewName).isEqualTo("network-types/form");
        verify(networkTypeService, never()).update(anyLong(), any());
    }

    @Test
    @DisplayName("Should handle exception on update")
    void shouldHandleExceptionOnUpdate() {
        // Given
        when(bindingResult.hasErrors()).thenReturn(false);
        when(networkTypeService.update(eq(1L), any(NetworkTypeDTO.class)))
            .thenThrow(new RuntimeException("Database error"));

        // When
        String viewName = controller.update(1L, testDTO, bindingResult, redirectAttributes);

        // Then
        assertThat(viewName).isEqualTo("redirect:/web/network-types/1/edit");
        verify(redirectAttributes).addFlashAttribute(eq("errorMessage"), anyString());
    }

    @Test
    @DisplayName("Should delete network type successfully")
    void shouldDeleteNetworkTypeSuccessfully() {
        // Given
        doNothing().when(networkTypeService).delete(1L);

        // When
        String viewName = controller.delete(1L, redirectAttributes);

        // Then
        assertThat(viewName).isEqualTo("redirect:/web/network-types");
        verify(networkTypeService).delete(1L);
        verify(redirectAttributes).addFlashAttribute(eq("successMessage"), anyString());
    }

    @Test
    @DisplayName("Should handle exception on delete")
    void shouldHandleExceptionOnDelete() {
        // Given
        doThrow(new RuntimeException("Cannot delete")).when(networkTypeService).delete(1L);

        // When
        String viewName = controller.delete(1L, redirectAttributes);

        // Then
        assertThat(viewName).isEqualTo("redirect:/web/network-types");
        verify(redirectAttributes).addFlashAttribute(eq("errorMessage"), anyString());
    }
}

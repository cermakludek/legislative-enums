package cz.intelis.legislativeenums.user;

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

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserWebController Unit Tests")
class UserWebControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private Model model;

    @Mock
    private BindingResult bindingResult;

    @Mock
    private RedirectAttributes redirectAttributes;

    @InjectMocks
    private UserWebController controller;

    private UserDTO testDTO;

    @BeforeEach
    void setUp() {
        testDTO = new UserDTO();
        testDTO.setId(1L);
        testDTO.setUsername("testuser");
        testDTO.setEmail("test@example.com");
        testDTO.setRole("READER");
        testDTO.setUsagePlan("PER_MONTH");
        testDTO.setTrialEndDate(LocalDate.now().plusDays(30));
        testDTO.setEnabled(true);
    }

    @Test
    @DisplayName("Should display list of users")
    void shouldDisplayListOfUsers() {
        // Given
        List<UserDTO> users = Arrays.asList(testDTO);
        when(userService.findAll()).thenReturn(users);

        // When
        String viewName = controller.list(model);

        // Then
        assertThat(viewName).isEqualTo("users/list");
        verify(model).addAttribute("users", users);
        verify(model).addAttribute("roles", User.UserRole.values());
        verify(model).addAttribute("usagePlans", User.UsagePlan.values());
        verify(userService).findAll();
    }

    @Test
    @DisplayName("Should display edit form")
    void shouldDisplayEditForm() {
        // Given
        when(userService.findById(1L)).thenReturn(testDTO);

        // When
        String viewName = controller.editForm(1L, model);

        // Then
        assertThat(viewName).isEqualTo("users/form");
        verify(model).addAttribute("user", testDTO);
        verify(model).addAttribute("roles", User.UserRole.values());
        verify(model).addAttribute("usagePlans", User.UsagePlan.values());
    }

    @Test
    @DisplayName("Should update user successfully")
    void shouldUpdateUserSuccessfully() {
        // Given
        when(bindingResult.hasErrors()).thenReturn(false);
        when(userService.update(eq(1L), any(UserDTO.class))).thenReturn(testDTO);

        // When
        String viewName = controller.update(1L, testDTO, bindingResult, model, redirectAttributes);

        // Then
        assertThat(viewName).isEqualTo("redirect:/web/users");
        verify(userService).update(1L, testDTO);
        verify(redirectAttributes).addFlashAttribute(eq("successMessage"), anyString());
    }

    @Test
    @DisplayName("Should return to form when validation errors on update")
    void shouldReturnToFormWhenValidationErrorsOnUpdate() {
        // Given
        when(bindingResult.hasErrors()).thenReturn(true);

        // When
        String viewName = controller.update(1L, testDTO, bindingResult, model, redirectAttributes);

        // Then
        assertThat(viewName).isEqualTo("users/form");
        verify(model).addAttribute("roles", User.UserRole.values());
        verify(model).addAttribute("usagePlans", User.UsagePlan.values());
        verify(userService, never()).update(anyLong(), any());
    }

    @Test
    @DisplayName("Should handle exception on update")
    void shouldHandleExceptionOnUpdate() {
        // Given
        when(bindingResult.hasErrors()).thenReturn(false);
        when(userService.update(eq(1L), any(UserDTO.class)))
            .thenThrow(new RuntimeException("Database error"));

        // When
        String viewName = controller.update(1L, testDTO, bindingResult, model, redirectAttributes);

        // Then
        assertThat(viewName).isEqualTo("redirect:/web/users/1/edit");
        verify(redirectAttributes).addFlashAttribute(eq("errorMessage"), anyString());
    }

    @Test
    @DisplayName("Should delete user successfully")
    void shouldDeleteUserSuccessfully() {
        // Given
        doNothing().when(userService).delete(1L);

        // When
        String viewName = controller.delete(1L, redirectAttributes);

        // Then
        assertThat(viewName).isEqualTo("redirect:/web/users");
        verify(userService).delete(1L);
        verify(redirectAttributes).addFlashAttribute(eq("successMessage"), anyString());
    }

    @Test
    @DisplayName("Should handle exception on delete")
    void shouldHandleExceptionOnDelete() {
        // Given
        doThrow(new RuntimeException("Cannot delete")).when(userService).delete(1L);

        // When
        String viewName = controller.delete(1L, redirectAttributes);

        // Then
        assertThat(viewName).isEqualTo("redirect:/web/users");
        verify(redirectAttributes).addFlashAttribute(eq("errorMessage"), anyString());
    }
}

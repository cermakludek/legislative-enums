package cz.intelis.legislativeenums.registry;

import cz.intelis.legislativeenums.apikey.ApiKey;
import cz.intelis.legislativeenums.apikey.ApiKeyRepository;
import cz.intelis.legislativeenums.flag.FlagDTO;
import cz.intelis.legislativeenums.flag.FlagService;
import cz.intelis.legislativeenums.user.User;
import cz.intelis.legislativeenums.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CodelistRegistryWebController Unit Tests")
class CodelistRegistryWebControllerTest {

    @Mock
    private CodelistRegistryService codelistRegistryService;

    @Mock
    private FlagService flagService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ApiKeyRepository apiKeyRepository;

    @Mock
    private Model model;

    @Mock
    private BindingResult bindingResult;

    @Mock
    private RedirectAttributes redirectAttributes;

    @Mock
    private UserDetails userDetails;

    @InjectMocks
    private CodelistRegistryWebController controller;

    private CodelistRegistryDTO testDTO;
    private FlagDTO testFlagDTO;

    @BeforeEach
    void setUp() {
        testFlagDTO = new FlagDTO();
        testFlagDTO.setId(1L);
        testFlagDTO.setCode("IMPORTANT");
        testFlagDTO.setNameCs("Důležité");
        testFlagDTO.setNameEn("Important");

        testDTO = new CodelistRegistryDTO();
        testDTO.setId(1L);
        testDTO.setCode("VOLTAGE_LEVELS");
        testDTO.setNameCs("Úrovně napětí");
        testDTO.setNameEn("Voltage Levels");
        testDTO.setDescriptionCs("Číselník úrovní napětí");
        testDTO.setDescriptionEn("Voltage levels codelist");
        testDTO.setWebUrl("/web/voltage-levels");
        testDTO.setApiUrl("/api/voltage-levels");
        testDTO.setIconClass("fas fa-bolt");
        testDTO.setSortOrder(1);
        testDTO.setFlagIds(new HashSet<>(Arrays.asList(1L)));
    }

    @Test
    @DisplayName("Should display dashboard filtered by flag")
    void shouldDisplayDashboardFilteredByFlag() {
        // Given
        List<CodelistRegistryDTO> codelists = Arrays.asList(testDTO);
        List<FlagDTO> flags = Arrays.asList(testFlagDTO);
        when(codelistRegistryService.findByFlag(1L)).thenReturn(codelists);
        when(flagService.findAllActive()).thenReturn(flags);

        // When
        String viewName = controller.dashboard(1L, model, null);

        // Then
        assertThat(viewName).isEqualTo("codelists/dashboard");
        verify(model).addAttribute("codelists", codelists);
        verify(model).addAttribute("selectedFlagId", 1L);
        verify(model).addAttribute("flags", flags);
        verify(codelistRegistryService).findByFlag(1L);
    }

    @Test
    @DisplayName("Should display dashboard with user API key")
    void shouldDisplayDashboardWithUserApiKey() {
        // Given
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");

        ApiKey apiKey = new ApiKey();
        apiKey.setApiKey("test-api-key-12345");

        List<CodelistRegistryDTO> codelists = Arrays.asList(testDTO);
        List<FlagDTO> flags = Arrays.asList(testFlagDTO);

        when(userDetails.getUsername()).thenReturn("testuser");
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(apiKeyRepository.findByUserId(1L)).thenReturn(Arrays.asList(apiKey));
        when(codelistRegistryService.findAllCurrentlyValid()).thenReturn(codelists);
        when(flagService.findAllActive()).thenReturn(flags);

        // When
        String viewName = controller.dashboard(null, model, userDetails);

        // Then
        assertThat(viewName).isEqualTo("codelists/dashboard");
        verify(model).addAttribute("userApiKey", "test-api-key-12345");
        verify(userRepository).findByUsername("testuser");
        verify(apiKeyRepository).findByUserId(1L);
    }

    @Test
    @DisplayName("Should display admin list of currently valid codelists")
    void shouldDisplayAdminListOfAllCodelists() {
        // Given
        List<CodelistRegistryDTO> codelists = Arrays.asList(testDTO);
        when(codelistRegistryService.findAllCurrentlyValid()).thenReturn(codelists);

        // When
        String viewName = controller.list(false, model);

        // Then
        assertThat(viewName).isEqualTo("codelists/list");
        verify(model).addAttribute("codelists", codelists);
        verify(codelistRegistryService).findAllCurrentlyValid();
    }

    @Test
    @DisplayName("Should display admin list of all codelists when showAll is true")
    void shouldDisplayAdminListOfAllCodelistsShowAll() {
        // Given
        List<CodelistRegistryDTO> codelists = Arrays.asList(testDTO);
        when(codelistRegistryService.findAll()).thenReturn(codelists);

        // When
        String viewName = controller.list(true, model);

        // Then
        assertThat(viewName).isEqualTo("codelists/list");
        verify(model).addAttribute("codelists", codelists);
        verify(codelistRegistryService).findAll();
    }

    @Test
    @DisplayName("Should display create form")
    void shouldDisplayCreateForm() {
        // Given
        List<FlagDTO> flags = Arrays.asList(testFlagDTO);
        when(flagService.findAllActive()).thenReturn(flags);

        // When
        String viewName = controller.createForm(model);

        // Then
        assertThat(viewName).isEqualTo("codelists/form");
        verify(model).addAttribute(eq("codelist"), any(CodelistRegistryDTO.class));
        verify(model).addAttribute("allFlags", flags);
    }

    @Test
    @DisplayName("Should create codelist registry entry successfully")
    void shouldCreateCodelistRegistryEntrySuccessfully() {
        // Given
        when(bindingResult.hasErrors()).thenReturn(false);
        when(codelistRegistryService.create(any(CodelistRegistryDTO.class))).thenReturn(testDTO);

        // When
        String viewName = controller.create(testDTO, bindingResult, model, redirectAttributes);

        // Then
        assertThat(viewName).isEqualTo("redirect:/web/codelists/admin");
        verify(codelistRegistryService).create(testDTO);
        verify(redirectAttributes).addFlashAttribute(eq("successMessage"), anyString());
    }

    @Test
    @DisplayName("Should return to form when validation errors on create")
    void shouldReturnToFormWhenValidationErrorsOnCreate() {
        // Given
        List<FlagDTO> flags = Arrays.asList(testFlagDTO);
        when(bindingResult.hasErrors()).thenReturn(true);
        when(flagService.findAllActive()).thenReturn(flags);

        // When
        String viewName = controller.create(testDTO, bindingResult, model, redirectAttributes);

        // Then
        assertThat(viewName).isEqualTo("codelists/form");
        verify(model).addAttribute("allFlags", flags);
        verify(codelistRegistryService, never()).create(any());
    }

    @Test
    @DisplayName("Should handle exception on create")
    void shouldHandleExceptionOnCreate() {
        // Given
        when(bindingResult.hasErrors()).thenReturn(false);
        when(codelistRegistryService.create(any(CodelistRegistryDTO.class)))
            .thenThrow(new RuntimeException("Database error"));

        // When
        String viewName = controller.create(testDTO, bindingResult, model, redirectAttributes);

        // Then
        assertThat(viewName).isEqualTo("redirect:/web/codelists/create");
        verify(redirectAttributes).addFlashAttribute(eq("errorMessage"), anyString());
    }

    @Test
    @DisplayName("Should display edit form")
    void shouldDisplayEditForm() {
        // Given
        List<FlagDTO> flags = Arrays.asList(testFlagDTO);
        when(codelistRegistryService.findById(1L)).thenReturn(testDTO);
        when(flagService.findAllActive()).thenReturn(flags);

        // When
        String viewName = controller.editForm(1L, model);

        // Then
        assertThat(viewName).isEqualTo("codelists/form");
        verify(model).addAttribute("codelist", testDTO);
        verify(model).addAttribute("allFlags", flags);
    }

    @Test
    @DisplayName("Should update codelist registry entry successfully")
    void shouldUpdateCodelistRegistryEntrySuccessfully() {
        // Given
        when(bindingResult.hasErrors()).thenReturn(false);
        when(codelistRegistryService.update(eq(1L), any(CodelistRegistryDTO.class))).thenReturn(testDTO);

        // When
        String viewName = controller.update(1L, testDTO, bindingResult, model, redirectAttributes);

        // Then
        assertThat(viewName).isEqualTo("redirect:/web/codelists/admin");
        verify(codelistRegistryService).update(1L, testDTO);
        verify(redirectAttributes).addFlashAttribute(eq("successMessage"), anyString());
    }

    @Test
    @DisplayName("Should return to form when validation errors on update")
    void shouldReturnToFormWhenValidationErrorsOnUpdate() {
        // Given
        List<FlagDTO> flags = Arrays.asList(testFlagDTO);
        when(bindingResult.hasErrors()).thenReturn(true);
        when(flagService.findAllActive()).thenReturn(flags);

        // When
        String viewName = controller.update(1L, testDTO, bindingResult, model, redirectAttributes);

        // Then
        assertThat(viewName).isEqualTo("codelists/form");
        verify(model).addAttribute("allFlags", flags);
        verify(codelistRegistryService, never()).update(anyLong(), any());
    }

    @Test
    @DisplayName("Should handle exception on update")
    void shouldHandleExceptionOnUpdate() {
        // Given
        when(bindingResult.hasErrors()).thenReturn(false);
        when(codelistRegistryService.update(eq(1L), any(CodelistRegistryDTO.class)))
            .thenThrow(new RuntimeException("Database error"));

        // When
        String viewName = controller.update(1L, testDTO, bindingResult, model, redirectAttributes);

        // Then
        assertThat(viewName).isEqualTo("redirect:/web/codelists/1/edit");
        verify(redirectAttributes).addFlashAttribute(eq("errorMessage"), anyString());
    }

    @Test
    @DisplayName("Should delete codelist registry entry successfully")
    void shouldDeleteCodelistRegistryEntrySuccessfully() {
        // Given
        doNothing().when(codelistRegistryService).delete(1L);

        // When
        String viewName = controller.delete(1L, redirectAttributes);

        // Then
        assertThat(viewName).isEqualTo("redirect:/web/codelists/admin");
        verify(codelistRegistryService).delete(1L);
        verify(redirectAttributes).addFlashAttribute(eq("successMessage"), anyString());
    }

    @Test
    @DisplayName("Should handle exception on delete")
    void shouldHandleExceptionOnDelete() {
        // Given
        doThrow(new RuntimeException("Cannot delete")).when(codelistRegistryService).delete(1L);

        // When
        String viewName = controller.delete(1L, redirectAttributes);

        // Then
        assertThat(viewName).isEqualTo("redirect:/web/codelists/admin");
        verify(redirectAttributes).addFlashAttribute(eq("errorMessage"), anyString());
    }
}

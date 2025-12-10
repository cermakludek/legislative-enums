package cz.intelis.legislativeenums.apikey;

import cz.intelis.legislativeenums.user.User;
import cz.intelis.legislativeenums.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ApiKeyService Unit Tests")
class ApiKeyServiceTest {

    @Mock
    private ApiKeyRepository apiKeyRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ApiKeyService apiKeyService;

    private User testUser;
    private ApiKey testApiKey;
    private ApiKeyDTO testApiKeyDTO;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");

        testApiKey = new ApiKey();
        testApiKey.setId(1L);
        testApiKey.setApiKey("test-api-key-123");
        testApiKey.setName("Test Key");
        testApiKey.setEnabled(true);
        testApiKey.setUser(testUser);

        testApiKeyDTO = new ApiKeyDTO();
        testApiKeyDTO.setId(1L);
        testApiKeyDTO.setName("Test Key");
        testApiKeyDTO.setEnabled(true);
    }

    @Test
    @DisplayName("Should find all API keys")
    void shouldFindAllApiKeys() {
        // Given
        ApiKey apiKey2 = new ApiKey();
        apiKey2.setId(2L);
        apiKey2.setApiKey("test-api-key-456");
        apiKey2.setName("Key 2");
        apiKey2.setEnabled(true);
        apiKey2.setUser(testUser);

        when(apiKeyRepository.findAll()).thenReturn(Arrays.asList(testApiKey, apiKey2));

        // When
        List<ApiKeyDTO> result = apiKeyService.findAll();

        // Then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getName()).isEqualTo("Test Key");
        assertThat(result.get(1).getName()).isEqualTo("Key 2");
        verify(apiKeyRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should find API keys by user ID")
    void shouldFindApiKeysByUserId() {
        // Given
        when(apiKeyRepository.findByUserId(1L)).thenReturn(Arrays.asList(testApiKey));

        // When
        List<ApiKeyDTO> result = apiKeyService.findByUserId(1L);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Test Key");
        verify(apiKeyRepository, times(1)).findByUserId(1L);
    }

    @Test
    @DisplayName("Should find API key by ID")
    void shouldFindApiKeyById() {
        // Given
        when(apiKeyRepository.findById(1L)).thenReturn(Optional.of(testApiKey));

        // When
        ApiKeyDTO result = apiKeyService.findById(1L);

        // Then
        assertThat(result.getName()).isEqualTo("Test Key");
        verify(apiKeyRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should throw exception when API key not found by ID")
    void shouldThrowExceptionWhenApiKeyNotFoundById() {
        // Given
        when(apiKeyRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> apiKeyService.findById(999L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Not found");
    }

    @Test
    @DisplayName("Should find valid API key by key value")
    void shouldFindValidApiKeyByKeyValue() {
        // Given
        when(apiKeyRepository.findValidApiKey("test-api-key-123")).thenReturn(Optional.of(testApiKey));

        // When
        ApiKey result = apiKeyService.findValidByApiKey("test-api-key-123");

        // Then
        assertThat(result.getApiKey()).isEqualTo("test-api-key-123");
        verify(apiKeyRepository, times(1)).findValidApiKey("test-api-key-123");
    }

    @Test
    @DisplayName("Should throw exception when invalid API key")
    void shouldThrowExceptionWhenInvalidApiKey() {
        // Given
        when(apiKeyRepository.findValidApiKey("invalid-key")).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> apiKeyService.findValidByApiKey("invalid-key"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Invalid API key");
    }

    @Test
    @DisplayName("Should create new API key")
    void shouldCreateNewApiKey() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(apiKeyRepository.save(any(ApiKey.class))).thenAnswer(inv -> {
            ApiKey k = inv.getArgument(0);
            k.setId(1L);
            return k;
        });

        // When
        ApiKeyDTO result = apiKeyService.create(1L, testApiKeyDTO);

        // Then
        assertThat(result.getName()).isEqualTo("Test Key");
        ArgumentCaptor<ApiKey> captor = ArgumentCaptor.forClass(ApiKey.class);
        verify(apiKeyRepository).save(captor.capture());
        assertThat(captor.getValue().getApiKey()).isNotNull();
        assertThat(captor.getValue().getUser()).isEqualTo(testUser);
    }

    @Test
    @DisplayName("Should throw exception when creating API key for non-existent user")
    void shouldThrowExceptionWhenCreatingApiKeyForNonExistentUser() {
        // Given
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> apiKeyService.create(999L, testApiKeyDTO))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("User not found");
    }

    @Test
    @DisplayName("Should update API key")
    void shouldUpdateApiKey() {
        // Given
        when(apiKeyRepository.findById(1L)).thenReturn(Optional.of(testApiKey));
        when(apiKeyRepository.save(any(ApiKey.class))).thenReturn(testApiKey);

        testApiKeyDTO.setName("Updated Key");
        testApiKeyDTO.setEnabled(false);

        // When
        ApiKeyDTO result = apiKeyService.update(1L, testApiKeyDTO);

        // Then
        verify(apiKeyRepository, times(1)).save(any(ApiKey.class));
    }

    @Test
    @DisplayName("Should delete API key")
    void shouldDeleteApiKey() {
        // When
        apiKeyService.delete(1L);

        // Then
        verify(apiKeyRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Should regenerate API key")
    void shouldRegenerateApiKey() {
        // Given
        String originalKey = testApiKey.getApiKey();
        when(apiKeyRepository.findById(1L)).thenReturn(Optional.of(testApiKey));
        when(apiKeyRepository.save(any(ApiKey.class))).thenAnswer(inv -> inv.getArgument(0));

        // When
        ApiKeyDTO result = apiKeyService.regenerate(1L);

        // Then
        ArgumentCaptor<ApiKey> captor = ArgumentCaptor.forClass(ApiKey.class);
        verify(apiKeyRepository).save(captor.capture());
        assertThat(captor.getValue().getApiKey()).isNotEqualTo(originalKey);
    }

    @Test
    @DisplayName("Should regenerate API key for user")
    void shouldRegenerateApiKeyForUser() {
        // Given
        String originalKey = testApiKey.getApiKey();
        when(apiKeyRepository.findByUserId(1L)).thenReturn(Arrays.asList(testApiKey));
        when(apiKeyRepository.save(any(ApiKey.class))).thenAnswer(inv -> inv.getArgument(0));

        // When
        String newKey = apiKeyService.regenerateForUser(1L);

        // Then
        assertThat(newKey).isNotEqualTo(originalKey);
        verify(apiKeyRepository, times(1)).save(any(ApiKey.class));
    }

    @Test
    @DisplayName("Should create new API key when user has none")
    void shouldCreateNewApiKeyWhenUserHasNone() {
        // Given
        when(apiKeyRepository.findByUserId(1L)).thenReturn(Collections.emptyList());
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(apiKeyRepository.save(any(ApiKey.class))).thenAnswer(inv -> {
            ApiKey k = inv.getArgument(0);
            return k;
        });

        // When
        String newKey = apiKeyService.regenerateForUser(1L);

        // Then
        assertThat(newKey).isNotNull();
        ArgumentCaptor<ApiKey> captor = ArgumentCaptor.forClass(ApiKey.class);
        verify(apiKeyRepository).save(captor.capture());
        assertThat(captor.getValue().getName()).isEqualTo("Default API Key");
        assertThat(captor.getValue().getUser()).isEqualTo(testUser);
    }

    @Test
    @DisplayName("Should set expiration date when creating API key")
    void shouldSetExpirationDateWhenCreatingApiKey() {
        // Given
        LocalDateTime expiresAt = LocalDateTime.now().plusDays(30);
        testApiKeyDTO.setExpiresAt(expiresAt);

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(apiKeyRepository.save(any(ApiKey.class))).thenAnswer(inv -> {
            ApiKey k = inv.getArgument(0);
            k.setId(1L);
            return k;
        });

        // When
        apiKeyService.create(1L, testApiKeyDTO);

        // Then
        ArgumentCaptor<ApiKey> captor = ArgumentCaptor.forClass(ApiKey.class);
        verify(apiKeyRepository).save(captor.capture());
        assertThat(captor.getValue().getExpiresAt()).isEqualTo(expiresAt);
    }
}

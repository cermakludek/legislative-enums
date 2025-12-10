package cz.intelis.legislativeenums.apikey;

import cz.intelis.legislativeenums.monetization.*;
import cz.intelis.legislativeenums.user.*;
import lombok.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ApiKeyService {
    private final ApiKeyRepository apiKeyRepository;
    private final UserRepository userRepository;

    /**
     * Retrieves all API keys.
     *
     * @return list of all API keys as DTOs
     */
    public List<ApiKeyDTO> findAll() {
        return apiKeyRepository.findAll().stream().map(ApiKeyDTO::fromEntity).collect(Collectors.toList());
    }

    /**
     * Retrieves all API keys for a specific user.
     *
     * @param userId the user ID
     * @return list of API keys for the user as DTOs
     */
    public List<ApiKeyDTO> findByUserId(Long userId) {
        return apiKeyRepository.findByUserId(userId).stream().map(ApiKeyDTO::fromEntity).collect(Collectors.toList());
    }

    /**
     * Finds an API key by its ID.
     *
     * @param id the API key ID
     * @return the API key as DTO
     * @throws RuntimeException if API key not found
     */
    public ApiKeyDTO findById(Long id) {
        return ApiKeyDTO.fromEntity(apiKeyRepository.findById(id).orElseThrow(() -> new RuntimeException("Not found")));
    }

    /**
     * Finds a valid API key by its key value.
     * Validates that the key is enabled and not expired.
     *
     * @param apiKeyValue the API key value to search for
     * @return the valid API key entity
     * @throws RuntimeException if API key not found or invalid
     */
    @Transactional(readOnly = true)
    public ApiKey findValidByApiKey(String apiKeyValue) {
        return apiKeyRepository.findValidApiKey(apiKeyValue).orElseThrow(() -> new RuntimeException("Invalid API key"));
    }

    /**
     * Creates a new API key for a user.
     * Automatically generates a unique UUID for the key value.
     *
     * @param userId the ID of the user who will own the key
     * @param dto the API key data
     * @return the created API key as DTO
     * @throws RuntimeException if user not found
     */
    @Transactional
    public ApiKeyDTO create(Long userId, ApiKeyDTO dto) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        ApiKey k = new ApiKey();
        k.setApiKey(UUID.randomUUID().toString());
        k.setName(dto.getName());
        k.setEnabled(dto.getEnabled() != null ? dto.getEnabled() : true);
        k.setExpiresAt(dto.getExpiresAt());
        k.setUser(user);
        return ApiKeyDTO.fromEntity(apiKeyRepository.save(k));
    }

    /**
     * Updates an existing API key.
     * Cannot update the key value itself, use regenerate for that.
     *
     * @param id the ID of the API key to update
     * @param dto the new API key data
     * @return the updated API key as DTO
     * @throws RuntimeException if API key not found
     */
    @Transactional
    public ApiKeyDTO update(Long id, ApiKeyDTO dto) {
        ApiKey k = apiKeyRepository.findById(id).orElseThrow(() -> new RuntimeException("Not found"));
        k.setName(dto.getName());
        k.setEnabled(dto.getEnabled());
        k.setExpiresAt(dto.getExpiresAt());
        return ApiKeyDTO.fromEntity(apiKeyRepository.save(k));
    }

    /**
     * Deletes an API key by its ID.
     *
     * @param id the ID of the API key to delete
     */
    @Transactional
    public void delete(Long id) {
        apiKeyRepository.deleteById(id);
    }

    /**
     * Regenerates the API key value for an existing API key.
     *
     * @param id the ID of the API key to regenerate
     * @return the updated API key DTO with the new key value
     */
    @Transactional
    public ApiKeyDTO regenerate(Long id) {
        ApiKey k = apiKeyRepository.findById(id).orElseThrow(() -> new RuntimeException("API key not found"));
        k.setApiKey(UUID.randomUUID().toString());
        return ApiKeyDTO.fromEntity(apiKeyRepository.save(k));
    }

    /**
     * Regenerates the API key for a user by user ID.
     * If the user has multiple keys, regenerates the first one.
     *
     * @param userId the ID of the user
     * @return the regenerated API key value
     */
    @Transactional
    public String regenerateForUser(Long userId) {
        List<ApiKey> keys = apiKeyRepository.findByUserId(userId);
        if (keys.isEmpty()) {
            // Create new API key if none exists
            User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
            ApiKey k = new ApiKey();
            k.setApiKey(UUID.randomUUID().toString());
            k.setName("Default API Key");
            k.setEnabled(true);
            k.setUser(user);
            return apiKeyRepository.save(k).getApiKey();
        }
        // Regenerate the first key
        ApiKey k = keys.get(0);
        k.setApiKey(UUID.randomUUID().toString());
        return apiKeyRepository.save(k).getApiKey();
    }
}

package cz.intelis.legislativeenums.apikey;

import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiKeyDTO {
    private Long id;
    @NotBlank
    private String name;
    private String apiKey;
    private Boolean enabled;
    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;
    private Long userId;
    private String username;

    public static ApiKeyDTO fromEntity(ApiKey k) {
        if (k == null) return null;
        ApiKeyDTO dto = new ApiKeyDTO();
        dto.setId(k.getId());
        dto.setName(k.getName());
        dto.setApiKey(k.getApiKey());
        dto.setEnabled(k.getEnabled());
        dto.setCreatedAt(k.getCreatedAt());
        dto.setExpiresAt(k.getExpiresAt());
        dto.setUserId(k.getUser() != null ? k.getUser().getId() : null);
        dto.setUsername(k.getUser() != null ? k.getUser().getUsername() : null);
        return dto;
    }
}

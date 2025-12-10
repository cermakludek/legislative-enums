package cz.intelis.legislativeenums.registry;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import cz.intelis.legislativeenums.flag.FlagDTO;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * DTO for CodelistRegistry entity.
 *
 * @author Legislative Codelists Team
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JacksonXmlRootElement(localName = "codelist")
public class CodelistRegistryDTO {

    private Long id;

    @NotBlank(message = "Code is required")
    @Size(max = 50)
    private String code;

    @NotBlank(message = "Czech name is required")
    @Size(max = 100)
    private String nameCs;

    @NotBlank(message = "English name is required")
    @Size(max = 100)
    private String nameEn;

    private String descriptionCs;
    private String descriptionEn;

    @NotBlank(message = "Web URL is required")
    @Size(max = 100)
    private String webUrl;

    @NotBlank(message = "API URL is required")
    @Size(max = 100)
    private String apiUrl;

    @Size(max = 50)
    private String iconClass;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate validFrom;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate validTo;
    private Integer sortOrder;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @JacksonXmlElementWrapper(localName = "flags")
    @JacksonXmlProperty(localName = "flag")
    private Set<FlagDTO> flags = new HashSet<>();

    @JacksonXmlElementWrapper(localName = "flagIds")
    @JacksonXmlProperty(localName = "flagId")
    private Set<Long> flagIds = new HashSet<>();

    public static CodelistRegistryDTO fromEntity(CodelistRegistry entity) {
        if (entity == null) return null;
        CodelistRegistryDTO dto = new CodelistRegistryDTO();
        dto.setId(entity.getId());
        dto.setCode(entity.getCode());
        dto.setNameCs(entity.getNameCs());
        dto.setNameEn(entity.getNameEn());
        dto.setDescriptionCs(entity.getDescriptionCs());
        dto.setDescriptionEn(entity.getDescriptionEn());
        dto.setWebUrl(entity.getWebUrl());
        dto.setApiUrl(entity.getApiUrl());
        dto.setIconClass(entity.getIconClass());
        dto.setValidFrom(entity.getValidFrom());
        dto.setValidTo(entity.getValidTo());
        dto.setSortOrder(entity.getSortOrder());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        if (entity.getFlags() != null) {
            dto.setFlags(entity.getFlags().stream()
                .map(FlagDTO::fromEntity)
                .collect(Collectors.toSet()));
            dto.setFlagIds(entity.getFlags().stream()
                .map(f -> f.getId())
                .collect(Collectors.toSet()));
        }
        return dto;
    }
}

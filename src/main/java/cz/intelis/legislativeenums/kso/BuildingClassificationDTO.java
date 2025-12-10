package cz.intelis.legislativeenums.kso;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * DTO for BuildingClassification entity.
 * Supports hierarchical structure with children for tree representation.
 *
 * @author Legislative Codelists Team
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JacksonXmlRootElement(localName = "buildingClassification")
public class BuildingClassificationDTO {

    private Long id;

    @NotBlank(message = "Code is required")
    @Size(max = 15)
    private String code;

    @NotBlank(message = "Czech name is required")
    @Size(max = 200)
    private String nameCs;

    @NotBlank(message = "English name is required")
    @Size(max = 200)
    private String nameEn;

    private String descriptionCs;
    private String descriptionEn;

    @NotNull(message = "Level is required")
    private Integer level;

    private Long parentId;
    private String parentCode;
    private String parentName;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate validFrom;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate validTo;
    private Integer sortOrder;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * Children items for hierarchical tree representation.
     * Only populated when fetching tree structure.
     */
    @JacksonXmlElementWrapper(localName = "children")
    @JacksonXmlProperty(localName = "child")
    private List<BuildingClassificationDTO> children = new ArrayList<>();

    /**
     * Converts entity to DTO (flat, without children).
     */
    public static BuildingClassificationDTO fromEntity(BuildingClassification entity) {
        if (entity == null) return null;
        BuildingClassificationDTO dto = new BuildingClassificationDTO();
        dto.setId(entity.getId());
        dto.setCode(entity.getCode());
        dto.setNameCs(entity.getNameCs());
        dto.setNameEn(entity.getNameEn());
        dto.setDescriptionCs(entity.getDescriptionCs());
        dto.setDescriptionEn(entity.getDescriptionEn());
        dto.setLevel(entity.getLevel());
        if (entity.getParent() != null) {
            dto.setParentId(entity.getParent().getId());
            dto.setParentCode(entity.getParent().getCode());
            dto.setParentName(entity.getParent().getNameCs());
        }
        dto.setValidFrom(entity.getValidFrom());
        dto.setValidTo(entity.getValidTo());
        dto.setSortOrder(entity.getSortOrder());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        return dto;
    }

    /**
     * Converts entity to DTO including children (for tree structure).
     */
    public static BuildingClassificationDTO fromEntityWithChildren(BuildingClassification entity) {
        if (entity == null) return null;
        BuildingClassificationDTO dto = fromEntity(entity);
        if (entity.getChildren() != null && !entity.getChildren().isEmpty()) {
            dto.setChildren(entity.getChildren().stream()
                    .map(BuildingClassificationDTO::fromEntityWithChildren)
                    .toList());
        }
        return dto;
    }
}

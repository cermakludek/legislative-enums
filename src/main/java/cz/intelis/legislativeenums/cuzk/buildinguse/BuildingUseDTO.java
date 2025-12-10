package cz.intelis.legislativeenums.cuzk.buildinguse;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * DTO for BuildingUse entity (Způsob využití stavby).
 *
 * @author Legislative Codelists Team
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JacksonXmlRootElement(localName = "buildingUse")
public class BuildingUseDTO {

    private Long id;

    @NotBlank(message = "Code is required")
    @Size(max = 10)
    private String code;

    @NotBlank(message = "Czech name is required")
    @Size(max = 150)
    private String nameCs;

    @NotBlank(message = "English name is required")
    @Size(max = 150)
    private String nameEn;

    @Size(max = 500)
    private String descriptionCs;

    @Size(max = 500)
    private String descriptionEn;

    @Size(max = 20)
    private String abbreviation;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate validFrom;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate validTo;

    private Integer sortOrder;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static BuildingUseDTO fromEntity(BuildingUse entity) {
        if (entity == null) return null;
        BuildingUseDTO dto = new BuildingUseDTO();
        dto.setId(entity.getId());
        dto.setCode(entity.getCode());
        dto.setNameCs(entity.getNameCs());
        dto.setNameEn(entity.getNameEn());
        dto.setDescriptionCs(entity.getDescriptionCs());
        dto.setDescriptionEn(entity.getDescriptionEn());
        dto.setAbbreviation(entity.getAbbreviation());
        dto.setValidFrom(entity.getValidFrom());
        dto.setValidTo(entity.getValidTo());
        dto.setSortOrder(entity.getSortOrder());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        return dto;
    }
}

package cz.intelis.legislativeenums.cuzk.buildingtypeuse;

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
 * DTO for BuildingTypeUse entity (Vazba typ stavby a využití stavby).
 *
 * @author Legislative Codelists Team
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JacksonXmlRootElement(localName = "buildingTypeUse")
public class BuildingTypeUseDTO {

    private Long id;

    @NotBlank(message = "Building type code is required")
    @Size(max = 10)
    private String buildingTypeCode;

    @NotBlank(message = "Building use code is required")
    @Size(max = 10)
    private String buildingUseCode;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate validFrom;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate validTo;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static BuildingTypeUseDTO fromEntity(BuildingTypeUse entity) {
        if (entity == null) return null;
        BuildingTypeUseDTO dto = new BuildingTypeUseDTO();
        dto.setId(entity.getId());
        dto.setBuildingTypeCode(entity.getBuildingTypeCode());
        dto.setBuildingUseCode(entity.getBuildingUseCode());
        dto.setValidFrom(entity.getValidFrom());
        dto.setValidTo(entity.getValidTo());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        return dto;
    }
}

package cz.intelis.legislativeenums.cuzk.landtypeuse;

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
 * DTO for LandTypeUse entity (Vazba druh pozemku a využití).
 *
 * @author Legislative Codelists Team
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JacksonXmlRootElement(localName = "landTypeUse")
public class LandTypeUseDTO {

    private Long id;

    @NotBlank(message = "Land type code is required")
    @Size(max = 10)
    private String landTypeCode;

    @NotBlank(message = "Land use code is required")
    @Size(max = 10)
    private String landUseCode;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate validFrom;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate validTo;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static LandTypeUseDTO fromEntity(LandTypeUse entity) {
        if (entity == null) return null;
        LandTypeUseDTO dto = new LandTypeUseDTO();
        dto.setId(entity.getId());
        dto.setLandTypeCode(entity.getLandTypeCode());
        dto.setLandUseCode(entity.getLandUseCode());
        dto.setValidFrom(entity.getValidFrom());
        dto.setValidTo(entity.getValidTo());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        return dto;
    }
}

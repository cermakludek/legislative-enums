package cz.intelis.legislativeenums.voltagelevel;

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
 * DTO for VoltageLevel entity.
 *
 * @author Legislative Codelists Team
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JacksonXmlRootElement(localName = "voltageLevel")
public class VoltageLevelDTO {

    private Long id;

    @NotBlank(message = "Code is required")
    @Size(max = 10)
    private String code;

    @NotBlank(message = "Czech name is required")
    @Size(max = 100)
    private String nameCs;

    @NotBlank(message = "English name is required")
    @Size(max = 100)
    private String nameEn;

    @NotBlank(message = "Czech voltage range is required")
    @Size(max = 100)
    private String voltageRangeCs;

    @NotBlank(message = "English voltage range is required")
    @Size(max = 100)
    private String voltageRangeEn;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate validFrom;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate validTo;
    private Integer sortOrder;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static VoltageLevelDTO fromEntity(VoltageLevel entity) {
        if (entity == null) return null;
        VoltageLevelDTO dto = new VoltageLevelDTO();
        dto.setId(entity.getId());
        dto.setCode(entity.getCode());
        dto.setNameCs(entity.getNameCs());
        dto.setNameEn(entity.getNameEn());
        dto.setVoltageRangeCs(entity.getVoltageRangeCs());
        dto.setVoltageRangeEn(entity.getVoltageRangeEn());
        dto.setValidFrom(entity.getValidFrom());
        dto.setValidTo(entity.getValidTo());
        dto.setSortOrder(entity.getSortOrder());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        return dto;
    }
}

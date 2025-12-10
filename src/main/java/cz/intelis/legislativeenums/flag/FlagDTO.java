package cz.intelis.legislativeenums.flag;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for Flag entity.
 *
 * @author Legislative Codelists Team
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JacksonXmlRootElement(localName = "flag")
public class FlagDTO {

    private Long id;

    @NotBlank(message = "Code is required")
    @Size(max = 30)
    private String code;

    @NotBlank(message = "Czech name is required")
    @Size(max = 50)
    private String nameCs;

    @NotBlank(message = "English name is required")
    @Size(max = 50)
    private String nameEn;

    @Size(max = 200)
    private String descriptionCs;

    @Size(max = 200)
    private String descriptionEn;

    @Size(max = 7)
    private String color;

    @Size(max = 50)
    private String iconClass;

    private Boolean active;
    private Integer sortOrder;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static FlagDTO fromEntity(Flag entity) {
        if (entity == null) return null;
        FlagDTO dto = new FlagDTO();
        dto.setId(entity.getId());
        dto.setCode(entity.getCode());
        dto.setNameCs(entity.getNameCs());
        dto.setNameEn(entity.getNameEn());
        dto.setDescriptionCs(entity.getDescriptionCs());
        dto.setDescriptionEn(entity.getDescriptionEn());
        dto.setColor(entity.getColor());
        dto.setIconClass(entity.getIconClass());
        dto.setActive(entity.getActive());
        dto.setSortOrder(entity.getSortOrder());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        return dto;
    }
}

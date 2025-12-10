package cz.intelis.legislativeenums.cuzk.areadetermination;

import cz.intelis.legislativeenums.common.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entity representing area determination method (Způsob určení výměry).
 * Based on ČÚZK codelist SC_ZP_URCENI_VYMERY.
 * Attributes: KOD, NAZEV, PLATNOST_OD, PLATNOST_DO
 *
 * @author Legislative Codelists Team
 */
@Entity
@Table(name = "cuzk_area_determinations", uniqueConstraints = {@UniqueConstraint(columnNames = "code")})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AreaDetermination extends BaseEntity {

    @NotBlank
    @Size(max = 10)
    @Column(nullable = false, unique = true, length = 10)
    private String code;

    @NotBlank
    @Size(max = 150)
    @Column(name = "name_cs", nullable = false, length = 150)
    private String nameCs;

    @NotBlank
    @Size(max = 150)
    @Column(name = "name_en", nullable = false, length = 150)
    private String nameEn;

    @Size(max = 500)
    @Column(name = "description_cs", length = 500)
    private String descriptionCs;

    @Size(max = 500)
    @Column(name = "description_en", length = 500)
    private String descriptionEn;

    public String getName(String lang) {
        return "cs".equalsIgnoreCase(lang) ? nameCs : nameEn;
    }

    public String getDescription(String lang) {
        return "cs".equalsIgnoreCase(lang) ? descriptionCs : descriptionEn;
    }
}

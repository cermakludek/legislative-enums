package cz.intelis.legislativeenums.cuzk.buildingtype;

import cz.intelis.legislativeenums.common.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entity representing building type classification (Typ stavby).
 * Based on ČÚZK codelist SC_T_BUDOV.
 * Attributes: KOD, NAZEV, PLATNOST_OD, PLATNOST_DO, ZADANI_CD, ZKRATKA
 *
 * @author Legislative Codelists Team
 */
@Entity
@Table(name = "cuzk_building_types", uniqueConstraints = {@UniqueConstraint(columnNames = "code")})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BuildingType extends BaseEntity {

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

    /** ZKRATKA - abbreviation */
    @Size(max = 20)
    @Column(name = "abbreviation", length = 20)
    private String abbreviation;

    /** ZADANI_CD - entry code flag (descriptive number entry required) */
    @Column(name = "entry_code")
    private Boolean entryCode;

    public String getName(String lang) {
        return "cs".equalsIgnoreCase(lang) ? nameCs : nameEn;
    }

    public String getDescription(String lang) {
        return "cs".equalsIgnoreCase(lang) ? descriptionCs : descriptionEn;
    }
}

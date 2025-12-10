package cz.intelis.legislativeenums.cuzk.propertyprotection;

import cz.intelis.legislativeenums.common.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entity representing property protection method (Způsob ochrany nemovitosti).
 * Based on ČÚZK codelist SC_ZP_OCHRANY_NEM.
 * Attributes: KOD, NAZEV, TYOCHN_KOD, PLATNOST_OD, PLATNOST_DO, POZEMEK, BUDOVA, JEDNOTKA, PRAVO_STAVBY
 *
 * @author Legislative Codelists Team
 */
@Entity
@Table(name = "cuzk_property_protections", uniqueConstraints = {@UniqueConstraint(columnNames = "code")})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PropertyProtection extends BaseEntity {

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

    /** TYOCHN_KOD - protection type code */
    @Size(max = 10)
    @Column(name = "protection_type_code", length = 10)
    private String protectionTypeCode;

    /** POZEMEK - applies to land */
    @Column(name = "applies_to_land")
    private Boolean appliesToLand;

    /** BUDOVA - applies to building */
    @Column(name = "applies_to_building")
    private Boolean appliesToBuilding;

    /** JEDNOTKA - applies to unit */
    @Column(name = "applies_to_unit")
    private Boolean appliesToUnit;

    /** PRAVO_STAVBY - applies to building right */
    @Column(name = "applies_to_building_right")
    private Boolean appliesToBuildingRight;

    public String getName(String lang) {
        return "cs".equalsIgnoreCase(lang) ? nameCs : nameEn;
    }

    public String getDescription(String lang) {
        return "cs".equalsIgnoreCase(lang) ? descriptionCs : descriptionEn;
    }
}

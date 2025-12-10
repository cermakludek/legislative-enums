package cz.intelis.legislativeenums.cuzk.landtype;

import cz.intelis.legislativeenums.common.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entity representing land type classification (Druh pozemku).
 * Based on ČÚZK codelist SC_D_POZEMKU.
 * Attributes: KOD, NAZEV, ZEMEDELSKA_KULTURA, PLATNOST_OD, PLATNOST_DO, ZKRATKA, TYPPPD_KOD, STAVEBNI_PARCELA, POVINNA_OCHRANA_POZ, POVINNY_ZPUSOB_VYUZ
 *
 * @author Legislative Codelists Team
 */
@Entity
@Table(name = "cuzk_land_types", uniqueConstraints = {@UniqueConstraint(columnNames = "code")})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LandType extends BaseEntity {

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

    /** ZEMEDELSKA_KULTURA - agricultural land flag */
    @Column(name = "agricultural_land")
    private Boolean agriculturalLand;

    /** TYPPPD_KOD - land parcel type code */
    @Size(max = 10)
    @Column(name = "land_parcel_type_code", length = 10)
    private String landParcelTypeCode;

    /** STAVEBNI_PARCELA - building parcel flag */
    @Column(name = "building_parcel")
    private Boolean buildingParcel;

    /** POVINNA_OCHRANA_POZ - mandatory land protection flag */
    @Column(name = "mandatory_land_protection")
    private Boolean mandatoryLandProtection;

    /** POVINNY_ZPUSOB_VYUZ - mandatory land use flag */
    @Column(name = "mandatory_land_use")
    private Boolean mandatoryLandUse;

    public String getName(String lang) {
        return "cs".equalsIgnoreCase(lang) ? nameCs : nameEn;
    }

    public String getDescription(String lang) {
        return "cs".equalsIgnoreCase(lang) ? descriptionCs : descriptionEn;
    }
}

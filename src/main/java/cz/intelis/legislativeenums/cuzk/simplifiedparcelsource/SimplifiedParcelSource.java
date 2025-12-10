package cz.intelis.legislativeenums.cuzk.simplifiedparcelsource;

import cz.intelis.legislativeenums.common.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entity representing simplified evidence parcel source (Zdroje parcel zjednodušené evidence).
 * Based on ČÚZK codelist SC_ZDROJE_PARCEL_ZE.
 * Attributes: KOD, NAZEV, PLATNOST_OD, PLATNOST_DO, ZKRATKA
 *
 * @author Legislative Codelists Team
 */
@Entity
@Table(name = "cuzk_simplified_parcel_sources", uniqueConstraints = {@UniqueConstraint(columnNames = "code")})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SimplifiedParcelSource extends BaseEntity {

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

    public String getName(String lang) {
        return "cs".equalsIgnoreCase(lang) ? nameCs : nameEn;
    }

    public String getDescription(String lang) {
        return "cs".equalsIgnoreCase(lang) ? descriptionCs : descriptionEn;
    }
}

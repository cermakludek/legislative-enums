package cz.intelis.legislativeenums.cuzk.soilecologicalunit;

import cz.intelis.legislativeenums.common.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Entity representing soil ecological unit (Bonitované půdně ekologické jednotky - BPEJ).
 * Based on ČÚZK codelist SC_BPEJ.
 * Attributes: KOD, CENA, POPIS, PLATNOST_OD, PLATNOST_DO
 *
 * @author Legislative Codelists Team
 */
@Entity
@Table(name = "cuzk_soil_ecological_units", uniqueConstraints = {@UniqueConstraint(columnNames = "code")})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SoilEcologicalUnit extends BaseEntity {

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

    /** CENA - price per square meter */
    @Column(name = "price", precision = 10, scale = 2)
    private BigDecimal price;

    /** POPIS - detailed description */
    @Size(max = 1000)
    @Column(name = "detailed_description", length = 1000)
    private String detailedDescription;

    public String getName(String lang) {
        return "cs".equalsIgnoreCase(lang) ? nameCs : nameEn;
    }

    public String getDescription(String lang) {
        return "cs".equalsIgnoreCase(lang) ? descriptionCs : descriptionEn;
    }
}

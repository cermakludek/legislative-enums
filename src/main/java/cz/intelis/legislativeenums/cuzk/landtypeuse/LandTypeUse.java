package cz.intelis.legislativeenums.cuzk.landtypeuse;

import cz.intelis.legislativeenums.common.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entity representing the relationship between land type and land use (Vazba druh pozemku a využití).
 * Based on ČÚZK codelist SC_POZEMEK_VYUZITI.
 * Attributes: DRUPOZ_KOD, ZPVYPO_KOD
 *
 * @author Legislative Codelists Team
 */
@Entity
@Table(name = "cuzk_land_type_uses", uniqueConstraints = {@UniqueConstraint(columnNames = {"land_type_code", "land_use_code"})})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LandTypeUse extends BaseEntity {

    /** DRUPOZ_KOD - land type code */
    @NotBlank
    @Size(max = 10)
    @Column(name = "land_type_code", nullable = false, length = 10)
    private String landTypeCode;

    /** ZPVYPO_KOD - land use code */
    @NotBlank
    @Size(max = 10)
    @Column(name = "land_use_code", nullable = false, length = 10)
    private String landUseCode;
}

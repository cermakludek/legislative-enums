package cz.intelis.legislativeenums.cuzk.buildingtypeuse;

import cz.intelis.legislativeenums.common.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entity representing the relationship between building type and building use (Vazba typ stavby a využití stavby).
 * Based on ČÚZK codelist SC_TYPB_ZPVYB.
 * Attributes: ZPVYBU_KOD, TYPBUD_KOD
 *
 * @author Legislative Codelists Team
 */
@Entity
@Table(name = "cuzk_building_type_uses", uniqueConstraints = {@UniqueConstraint(columnNames = {"building_type_code", "building_use_code"})})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BuildingTypeUse extends BaseEntity {

    /** TYPBUD_KOD - building type code */
    @NotBlank
    @Size(max = 10)
    @Column(name = "building_type_code", nullable = false, length = 10)
    private String buildingTypeCode;

    /** ZPVYBU_KOD - building use code */
    @NotBlank
    @Size(max = 10)
    @Column(name = "building_use_code", nullable = false, length = 10)
    private String buildingUseCode;
}

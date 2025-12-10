package cz.intelis.legislativeenums.voltagelevel;

import cz.intelis.legislativeenums.common.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entity representing voltage level classification.
 * Codelist for "Rozdělení napětí dle velikosti" (Voltage classification by magnitude).
 *
 * @author Legislative Codelists Team
 */
@Entity
@Table(name = "voltage_levels", uniqueConstraints = {@UniqueConstraint(columnNames = "code")})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VoltageLevel extends BaseEntity {

    @NotBlank
    @Size(max = 10)
    @Column(nullable = false, unique = true, length = 10)
    private String code;

    @NotBlank
    @Size(max = 100)
    @Column(name = "name_cs", nullable = false, length = 100)
    private String nameCs;

    @NotBlank
    @Size(max = 100)
    @Column(name = "name_en", nullable = false, length = 100)
    private String nameEn;

    @NotBlank
    @Size(max = 100)
    @Column(name = "voltage_range_cs", nullable = false, length = 100)
    private String voltageRangeCs;

    @NotBlank
    @Size(max = 100)
    @Column(name = "voltage_range_en", nullable = false, length = 100)
    private String voltageRangeEn;

    public String getName(String lang) {
        return "cs".equalsIgnoreCase(lang) ? nameCs : nameEn;
    }

    public String getVoltageRange(String lang) {
        return "cs".equalsIgnoreCase(lang) ? voltageRangeCs : voltageRangeEn;
    }
}

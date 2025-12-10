package cz.intelis.legislativeenums.networktype;

import cz.intelis.legislativeenums.common.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entity representing a network type classification.
 * (Rozdělení sítí z hlediska vedení)
 *
 * @author Legislative Codelists Team
 */
@Entity
@Table(name = "network_types", uniqueConstraints = {@UniqueConstraint(columnNames = "code")})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NetworkType extends BaseEntity {

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

    @Column(name = "description_cs", columnDefinition = "TEXT")
    private String descriptionCs;

    @Column(name = "description_en", columnDefinition = "TEXT")
    private String descriptionEn;

    public String getName(String lang) {
        return "cs".equalsIgnoreCase(lang) ? nameCs : nameEn;
    }

    public String getDescription(String lang) {
        return "cs".equalsIgnoreCase(lang) ? descriptionCs : descriptionEn;
    }
}

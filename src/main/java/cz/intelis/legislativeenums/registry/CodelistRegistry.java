package cz.intelis.legislativeenums.registry;

import cz.intelis.legislativeenums.common.BaseEntity;
import cz.intelis.legislativeenums.flag.Flag;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

/**
 * Entity representing a codelist registry entry.
 * Stores metadata about available codelists in the system.
 *
 * @author Legislative Codelists Team
 */
@Entity
@Table(name = "codelist_registry", uniqueConstraints = {@UniqueConstraint(columnNames = "code")})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true, exclude = "flags")
public class CodelistRegistry extends BaseEntity {

    @NotBlank
    @Size(max = 50)
    @Column(nullable = false, unique = true, length = 50)
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

    @NotBlank
    @Size(max = 100)
    @Column(name = "web_url", nullable = false, length = 100)
    private String webUrl;

    @NotBlank
    @Size(max = 100)
    @Column(name = "api_url", nullable = false, length = 100)
    private String apiUrl;

    @Size(max = 50)
    @Column(name = "icon_class", length = 50)
    private String iconClass;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "codelist_flags",
        joinColumns = @JoinColumn(name = "codelist_id"),
        inverseJoinColumns = @JoinColumn(name = "flag_id")
    )
    private Set<Flag> flags = new HashSet<>();

    public String getName(String lang) {
        return "cs".equalsIgnoreCase(lang) ? nameCs : nameEn;
    }

    public String getDescription(String lang) {
        return "cs".equalsIgnoreCase(lang) ? descriptionCs : descriptionEn;
    }
}

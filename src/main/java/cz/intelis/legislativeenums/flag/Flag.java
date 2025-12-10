package cz.intelis.legislativeenums.flag;

import cz.intelis.legislativeenums.common.AuditingEntityListener;
import cz.intelis.legislativeenums.registry.CodelistRegistry;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Entity representing a flag/tag that can be assigned to codelists.
 * Flags allow categorization and filtering of codelists.
 * Note: Flag does not extend BaseEntity because it uses active instead of validFrom/validTo.
 *
 * @author Legislative Codelists Team
 */
@Entity
@Table(name = "flags", uniqueConstraints = {@UniqueConstraint(columnNames = "code")})
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = "codelists")
public class Flag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 30)
    @Column(nullable = false, unique = true, length = 30)
    private String code;

    @NotBlank
    @Size(max = 50)
    @Column(name = "name_cs", nullable = false, length = 50)
    private String nameCs;

    @NotBlank
    @Size(max = 50)
    @Column(name = "name_en", nullable = false, length = 50)
    private String nameEn;

    @Size(max = 200)
    @Column(name = "description_cs", length = 200)
    private String descriptionCs;

    @Size(max = 200)
    @Column(name = "description_en", length = 200)
    private String descriptionEn;

    @Size(max = 7)
    @Column(length = 7)
    private String color;

    @Size(max = 50)
    @Column(name = "icon_class", length = 50)
    private String iconClass;

    @Column(nullable = false)
    private Boolean active = true;

    @Column(name = "sort_order")
    private Integer sortOrder;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "created_by", length = 100, updatable = false)
    private String createdBy;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "updated_by", length = 100)
    private String updatedBy;

    @ManyToMany(mappedBy = "flags")
    private Set<CodelistRegistry> codelists = new HashSet<>();

    public String getName(String lang) {
        return "cs".equalsIgnoreCase(lang) ? nameCs : nameEn;
    }

    public String getDescription(String lang) {
        return "cs".equalsIgnoreCase(lang) ? descriptionCs : descriptionEn;
    }
}

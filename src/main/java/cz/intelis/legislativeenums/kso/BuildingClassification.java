package cz.intelis.legislativeenums.kso;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entity representing a building object classification (Klasifikace stavebních objektů - KSO/JKSO).
 * This is a hierarchical codelist with 4 levels:
 * - Level 1: Podskupina (3 digits): e.g., 801 - Budovy občanské výstavby
 * - Level 2: Oddíl (3+1 digits): e.g., 801.1 - Budovy pro zdravotní péči
 * - Level 3: Pododdíl (3+2 digits): e.g., 801.11 - budovy nemocnic
 * - Level 4: Konstrukčně materiálová charakteristika (3+2+1 digits): e.g., 801.11.1 - zděná z cihel
 *
 * @author Legislative Codelists Team
 */
@Entity
@Table(name = "building_classifications", uniqueConstraints = {@UniqueConstraint(columnNames = "code")})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BuildingClassification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 15)
    @Column(nullable = false, unique = true, length = 15)
    private String code;

    @NotBlank
    @Size(max = 200)
    @Column(name = "name_cs", nullable = false, length = 200)
    private String nameCs;

    @NotBlank
    @Size(max = 200)
    @Column(name = "name_en", nullable = false, length = 200)
    private String nameEn;

    @Column(name = "description_cs", columnDefinition = "TEXT")
    private String descriptionCs;

    @Column(name = "description_en", columnDefinition = "TEXT")
    private String descriptionEn;

    /**
     * Hierarchy level:
     * 1 = Podskupina (e.g., 801)
     * 2 = Oddíl (e.g., 801.1)
     * 3 = Pododdíl (e.g., 801.11)
     * 4 = Konstrukčně materiálová charakteristika (e.g., 801.11.1)
     */
    @Column(nullable = false)
    private Integer level;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private BuildingClassification parent;

    @OneToMany(mappedBy = "parent", fetch = FetchType.LAZY)
    @OrderBy("code ASC")
    private List<BuildingClassification> children = new ArrayList<>();

    @Column(name = "valid_from")
    private LocalDate validFrom;

    @Column(name = "valid_to")
    private LocalDate validTo;

    @Column(name = "sort_order")
    private Integer sortOrder;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public String getName(String lang) {
        return "cs".equalsIgnoreCase(lang) ? nameCs : nameEn;
    }

    public String getDescription(String lang) {
        return "cs".equalsIgnoreCase(lang) ? descriptionCs : descriptionEn;
    }
}

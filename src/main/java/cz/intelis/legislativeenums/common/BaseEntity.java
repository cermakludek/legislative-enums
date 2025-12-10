package cz.intelis.legislativeenums.common;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Base entity class providing common fields for all codelist entities.
 * Contains validity period (validFrom, validTo) and audit fields (createdAt, createdBy, updatedAt, updatedBy).
 * Audit fields are populated automatically by {@link AuditingEntityListener}.
 *
 * @author Legislative Codelists Team
 */
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
public abstract class BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Start date of validity period (PLATNOST_OD).
     */
    @Column(name = "valid_from")
    private LocalDate validFrom;

    /**
     * End date of validity period (PLATNOST_DO).
     * Null means the record is currently valid.
     */
    @Column(name = "valid_to")
    private LocalDate validTo;

    /**
     * Sort order for display purposes.
     */
    @Column(name = "sort_order")
    private Integer sortOrder;

    /**
     * Timestamp when this record was created.
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Username of the user who created this record.
     */
    @Column(name = "created_by", length = 100, updatable = false)
    private String createdBy;

    /**
     * Timestamp when this record was last updated.
     */
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * Username of the user who last updated this record.
     */
    @Column(name = "updated_by", length = 100)
    private String updatedBy;

    /**
     * Checks if this record is currently valid based on validFrom and validTo dates.
     *
     * @return true if the record is valid as of today
     */
    public boolean isCurrentlyValid() {
        LocalDate today = LocalDate.now();
        boolean afterStart = validFrom == null || !today.isBefore(validFrom);
        boolean beforeEnd = validTo == null || !today.isAfter(validTo);
        return afterStart && beforeEnd;
    }
}

package cz.intelis.legislativeenums.audit;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Entity for storing audit log of all changes to codelist entities.
 * Records CREATE, UPDATE, and DELETE operations with old and new values.
 *
 * @author Legislative Codelists Team
 */
@Entity
@Table(name = "audit_log", indexes = {
        @Index(name = "idx_audit_log_entity_type", columnList = "entity_type"),
        @Index(name = "idx_audit_log_entity_code", columnList = "entity_code"),
        @Index(name = "idx_audit_log_changed_at", columnList = "changed_at"),
        @Index(name = "idx_audit_log_changed_by", columnList = "changed_by"),
        @Index(name = "idx_audit_log_change_type", columnList = "change_type")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Type of entity (e.g., "VoltageLevel", "NetworkType", "BuildingType").
     */
    @Column(name = "entity_type", nullable = false, length = 100)
    private String entityType;

    /**
     * ID of the changed entity.
     */
    @Column(name = "entity_id", nullable = false)
    private Long entityId;

    /**
     * Code of the changed entity (for easy identification).
     */
    @Column(name = "entity_code", length = 50)
    private String entityCode;

    /**
     * Type of change: CREATE, UPDATE, DELETE.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "change_type", nullable = false, length = 20)
    private ChangeType changeType;

    /**
     * Username who made the change.
     */
    @Column(name = "changed_by", nullable = false, length = 100)
    private String changedBy;

    /**
     * Timestamp when the change was made.
     */
    @Column(name = "changed_at", nullable = false)
    private LocalDateTime changedAt;

    /**
     * Old values as JSON (null for CREATE).
     */
    @Column(name = "old_values", columnDefinition = "TEXT")
    private String oldValues;

    /**
     * New values as JSON (null for DELETE).
     */
    @Column(name = "new_values", columnDefinition = "TEXT")
    private String newValues;

    /**
     * Types of changes that can be audited.
     */
    public enum ChangeType {
        CREATE,
        UPDATE,
        DELETE
    }
}

package cz.intelis.legislativeenums.audit;

import java.util.Map;

/**
 * Interface for entities that support audit logging.
 * Implementing entities should provide a method to extract auditable values.
 *
 * @author Legislative Codelists Team
 */
public interface Auditable {

    /**
     * Get the entity ID.
     */
    Long getId();

    /**
     * Get the entity code (unique identifier).
     */
    String getCode();

    /**
     * Convert entity to a map of auditable values.
     * Should include all fields that are relevant for auditing.
     */
    Map<String, Object> toAuditMap();

    /**
     * Get the simple class name for audit log entity type.
     */
    default String getAuditEntityType() {
        return this.getClass().getSimpleName();
    }
}

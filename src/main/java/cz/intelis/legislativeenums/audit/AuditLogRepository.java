package cz.intelis.legislativeenums.audit;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository for AuditLog entity with support for filtering and fulltext search.
 *
 * @author Legislative Codelists Team
 */
@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {

    /**
     * Find all audit logs ordered by change date descending.
     */
    Page<AuditLog> findAllByOrderByChangedAtDesc(Pageable pageable);

    /**
     * Find audit logs by entity type.
     */
    Page<AuditLog> findByEntityTypeOrderByChangedAtDesc(String entityType, Pageable pageable);

    /**
     * Find audit logs by change type.
     */
    Page<AuditLog> findByChangeTypeOrderByChangedAtDesc(AuditLog.ChangeType changeType, Pageable pageable);

    /**
     * Find audit logs by user who made the change.
     */
    Page<AuditLog> findByChangedByOrderByChangedAtDesc(String changedBy, Pageable pageable);

    /**
     * Find audit logs within a date range.
     */
    Page<AuditLog> findByChangedAtBetweenOrderByChangedAtDesc(
            LocalDateTime from, LocalDateTime to, Pageable pageable);

    /**
     * Fulltext search across entity type, entity code, changed by, old values, and new values.
     */
    @Query(value = "SELECT * FROM audit_log a WHERE " +
            "LOWER(a.entity_type) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(a.entity_code) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(a.changed_by) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(CAST(a.old_values AS VARCHAR)) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(CAST(a.new_values AS VARCHAR)) LIKE LOWER(CONCAT('%', :search, '%')) " +
            "ORDER BY a.changed_at DESC",
            countQuery = "SELECT COUNT(*) FROM audit_log a WHERE " +
            "LOWER(a.entity_type) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(a.entity_code) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(a.changed_by) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(CAST(a.old_values AS VARCHAR)) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(CAST(a.new_values AS VARCHAR)) LIKE LOWER(CONCAT('%', :search, '%'))",
            nativeQuery = true)
    Page<AuditLog> searchFulltext(@Param("search") String search, Pageable pageable);

    /**
     * Combined filter with optional parameters.
     */
    @Query(value = "SELECT * FROM audit_log a WHERE " +
            "(:entityType IS NULL OR a.entity_type = :entityType) AND " +
            "(CAST(:changeType AS VARCHAR) IS NULL OR a.change_type = :changeType) AND " +
            "(:changedBy IS NULL OR a.changed_by = :changedBy) AND " +
            "(:search IS NULL OR " +
            "   LOWER(a.entity_type) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "   LOWER(a.entity_code) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "   LOWER(a.changed_by) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "   LOWER(CAST(a.old_values AS VARCHAR)) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "   LOWER(CAST(a.new_values AS VARCHAR)) LIKE LOWER(CONCAT('%', :search, '%'))" +
            ") " +
            "ORDER BY a.changed_at DESC",
            countQuery = "SELECT COUNT(*) FROM audit_log a WHERE " +
            "(:entityType IS NULL OR a.entity_type = :entityType) AND " +
            "(CAST(:changeType AS VARCHAR) IS NULL OR a.change_type = :changeType) AND " +
            "(:changedBy IS NULL OR a.changed_by = :changedBy) AND " +
            "(:search IS NULL OR " +
            "   LOWER(a.entity_type) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "   LOWER(a.entity_code) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "   LOWER(a.changed_by) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "   LOWER(CAST(a.old_values AS VARCHAR)) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "   LOWER(CAST(a.new_values AS VARCHAR)) LIKE LOWER(CONCAT('%', :search, '%'))" +
            ")",
            nativeQuery = true)
    Page<AuditLog> findWithFilters(
            @Param("entityType") String entityType,
            @Param("changeType") String changeType,
            @Param("changedBy") String changedBy,
            @Param("search") String search,
            Pageable pageable);

    /**
     * Get distinct entity types for filter dropdown.
     */
    @Query("SELECT DISTINCT a.entityType FROM AuditLog a ORDER BY a.entityType")
    List<String> findDistinctEntityTypes();

    /**
     * Get distinct users who made changes for filter dropdown.
     */
    @Query("SELECT DISTINCT a.changedBy FROM AuditLog a ORDER BY a.changedBy")
    List<String> findDistinctChangedBy();

    /**
     * Count audit logs by entity type.
     */
    long countByEntityType(String entityType);

    /**
     * Count audit logs by change type.
     */
    long countByChangeType(AuditLog.ChangeType changeType);
}

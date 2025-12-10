package cz.intelis.legislativeenums.audit;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Service for recording and retrieving audit logs.
 * Provides methods for logging CREATE, UPDATE, and DELETE operations.
 *
 * @author Legislative Codelists Team
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;
    private final ObjectMapper objectMapper;

    /**
     * Record a CREATE operation.
     *
     * @param entityType Type of the entity (e.g., "VoltageLevel")
     * @param entityId   ID of the created entity
     * @param entityCode Code of the created entity
     * @param newValues  Map of new values
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void logCreate(String entityType, Long entityId, String entityCode, Map<String, Object> newValues) {
        AuditLog auditLog = AuditLog.builder()
                .entityType(entityType)
                .entityId(entityId)
                .entityCode(entityCode)
                .changeType(AuditLog.ChangeType.CREATE)
                .changedBy(getCurrentUsername())
                .changedAt(LocalDateTime.now())
                .oldValues(null)
                .newValues(toJson(newValues))
                .build();

        auditLogRepository.save(auditLog);
        log.debug("Audit log created: {} {} (ID: {}) by {}",
                AuditLog.ChangeType.CREATE, entityType, entityId, auditLog.getChangedBy());
    }

    /**
     * Record an UPDATE operation.
     *
     * @param entityType Type of the entity
     * @param entityId   ID of the updated entity
     * @param entityCode Code of the updated entity
     * @param oldValues  Map of old values
     * @param newValues  Map of new values
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void logUpdate(String entityType, Long entityId, String entityCode,
                          Map<String, Object> oldValues, Map<String, Object> newValues) {
        // Only log if there are actual changes
        if (oldValues.equals(newValues)) {
            log.debug("No changes detected for {} (ID: {}), skipping audit log", entityType, entityId);
            return;
        }

        AuditLog auditLog = AuditLog.builder()
                .entityType(entityType)
                .entityId(entityId)
                .entityCode(entityCode)
                .changeType(AuditLog.ChangeType.UPDATE)
                .changedBy(getCurrentUsername())
                .changedAt(LocalDateTime.now())
                .oldValues(toJson(oldValues))
                .newValues(toJson(newValues))
                .build();

        auditLogRepository.save(auditLog);
        log.debug("Audit log created: {} {} (ID: {}) by {}",
                AuditLog.ChangeType.UPDATE, entityType, entityId, auditLog.getChangedBy());
    }

    /**
     * Record a DELETE operation.
     *
     * @param entityType Type of the entity
     * @param entityId   ID of the deleted entity
     * @param entityCode Code of the deleted entity
     * @param oldValues  Map of values before deletion
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void logDelete(String entityType, Long entityId, String entityCode, Map<String, Object> oldValues) {
        AuditLog auditLog = AuditLog.builder()
                .entityType(entityType)
                .entityId(entityId)
                .entityCode(entityCode)
                .changeType(AuditLog.ChangeType.DELETE)
                .changedBy(getCurrentUsername())
                .changedAt(LocalDateTime.now())
                .oldValues(toJson(oldValues))
                .newValues(null)
                .build();

        auditLogRepository.save(auditLog);
        log.debug("Audit log created: {} {} (ID: {}) by {}",
                AuditLog.ChangeType.DELETE, entityType, entityId, auditLog.getChangedBy());
    }

    /**
     * Find all audit logs with pagination.
     */
    @Transactional(readOnly = true)
    public Page<AuditLog> findAll(Pageable pageable) {
        return auditLogRepository.findAllByOrderByChangedAtDesc(pageable);
    }

    /**
     * Find audit logs with filters and fulltext search.
     */
    @Transactional(readOnly = true)
    public Page<AuditLog> findWithFilters(String entityType, AuditLog.ChangeType changeType,
                                          String changedBy, String search, Pageable pageable) {
        // Convert empty strings to null for proper query handling
        String entityTypeParam = (entityType != null && !entityType.isEmpty()) ? entityType : null;
        String changedByParam = (changedBy != null && !changedBy.isEmpty()) ? changedBy : null;
        String searchParam = (search != null && !search.isEmpty()) ? search : null;
        String changeTypeParam = changeType != null ? changeType.name() : null;

        return auditLogRepository.findWithFilters(entityTypeParam, changeTypeParam, changedByParam, searchParam, pageable);
    }

    /**
     * Fulltext search in audit logs.
     */
    @Transactional(readOnly = true)
    public Page<AuditLog> search(String query, Pageable pageable) {
        return auditLogRepository.searchFulltext(query, pageable);
    }

    /**
     * Get distinct entity types for filter dropdown.
     */
    @Transactional(readOnly = true)
    public List<String> getDistinctEntityTypes() {
        return auditLogRepository.findDistinctEntityTypes();
    }

    /**
     * Get distinct users for filter dropdown.
     */
    @Transactional(readOnly = true)
    public List<String> getDistinctChangedBy() {
        return auditLogRepository.findDistinctChangedBy();
    }

    /**
     * Get audit log by ID.
     */
    @Transactional(readOnly = true)
    public AuditLog findById(Long id) {
        return auditLogRepository.findById(id).orElse(null);
    }

    /**
     * Convert map to JSON string.
     */
    private String toJson(Map<String, Object> values) {
        if (values == null || values.isEmpty()) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(values);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize audit values to JSON", e);
            return values.toString();
        }
    }

    /**
     * Get current authenticated username.
     */
    private String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()
                && !"anonymousUser".equals(authentication.getPrincipal())) {
            return authentication.getName();
        }
        return "system";
    }

    /**
     * Create a map of entity values for auditing.
     * Utility method to be used by services.
     */
    public static Map<String, Object> createValuesMap(Object... keyValuePairs) {
        Map<String, Object> map = new LinkedHashMap<>();
        for (int i = 0; i < keyValuePairs.length - 1; i += 2) {
            String key = String.valueOf(keyValuePairs[i]);
            Object value = keyValuePairs[i + 1];
            if (value != null) {
                map.put(key, value);
            }
        }
        return map;
    }
}

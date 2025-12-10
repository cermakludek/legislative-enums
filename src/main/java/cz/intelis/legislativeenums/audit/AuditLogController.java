package cz.intelis.legislativeenums.audit;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/**
 * Web controller for viewing audit logs.
 * Provides admin-only access to audit trail with filtering and sorting.
 *
 * @author Legislative Codelists Team
 */
@Controller
@RequestMapping("/web/audit")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AuditLogController {

    private final AuditLogService auditLogService;

    private static final int DEFAULT_PAGE_SIZE = 25;

    /**
     * Displays the audit log list with filtering and pagination.
     *
     * @param entityType filter by entity type (optional)
     * @param changeType filter by change type (optional)
     * @param changedBy filter by user who made the change (optional)
     * @param search fulltext search term (optional)
     * @param page page number (0-based)
     * @param size page size
     * @param sort sort field
     * @param direction sort direction (asc/desc)
     * @param model the Spring MVC model
     * @return the view name for the audit log list page
     */
    @GetMapping
    public String list(
            @RequestParam(required = false) String entityType,
            @RequestParam(required = false) String changeType,
            @RequestParam(required = false) String changedBy,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "25") int size,
            @RequestParam(defaultValue = "changedAt") String sort,
            @RequestParam(defaultValue = "desc") String direction,
            Model model) {

        // Build pageable WITHOUT sorting - sorting is done in native SQL query
        Pageable pageable = PageRequest.of(page, size);

        // Parse changeType enum if provided
        AuditLog.ChangeType changeTypeEnum = null;
        if (changeType != null && !changeType.isEmpty()) {
            try {
                changeTypeEnum = AuditLog.ChangeType.valueOf(changeType);
            } catch (IllegalArgumentException ignored) {
                // Invalid change type, ignore
            }
        }

        // Get filtered results
        Page<AuditLog> auditLogs = auditLogService.findWithFilters(
                entityType, changeTypeEnum, changedBy, search, pageable);

        // Add to model
        model.addAttribute("auditLogs", auditLogs);
        model.addAttribute("entityTypes", auditLogService.getDistinctEntityTypes());
        model.addAttribute("changeTypes", AuditLog.ChangeType.values());
        model.addAttribute("changedByList", auditLogService.getDistinctChangedBy());

        // Add current filter values
        model.addAttribute("currentEntityType", entityType);
        model.addAttribute("currentChangeType", changeType);
        model.addAttribute("currentChangedBy", changedBy);
        model.addAttribute("currentSearch", search);
        model.addAttribute("currentSort", sort);
        model.addAttribute("currentDirection", direction);

        return "audit/list";
    }

    /**
     * Displays the detail of a single audit log entry.
     *
     * @param id the audit log ID
     * @param model the Spring MVC model
     * @return the view name for the audit log detail page
     */
    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        AuditLog auditLog = auditLogService.findById(id);
        if (auditLog == null) {
            return "redirect:/web/audit";
        }
        model.addAttribute("auditLog", auditLog);
        return "audit/detail";
    }
}

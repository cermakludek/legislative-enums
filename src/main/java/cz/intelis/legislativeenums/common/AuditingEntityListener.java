package cz.intelis.legislativeenums.common;

import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;

/**
 * JPA Entity Listener for automatic population of audit fields.
 * Populates createdAt, createdBy on insert and updatedAt, updatedBy on update.
 *
 * @author Legislative Codelists Team
 */
public class AuditingEntityListener {

    /**
     * Called before entity is persisted.
     * Sets createdAt, createdBy, updatedAt, and updatedBy fields.
     *
     * @param entity the entity being persisted
     */
    @PrePersist
    public void prePersist(BaseEntity entity) {
        LocalDateTime now = LocalDateTime.now();
        String currentUser = getCurrentUsername();

        entity.setCreatedAt(now);
        entity.setCreatedBy(currentUser);
        entity.setUpdatedAt(now);
        entity.setUpdatedBy(currentUser);
    }

    /**
     * Called before entity is updated.
     * Sets updatedAt and updatedBy fields.
     *
     * @param entity the entity being updated
     */
    @PreUpdate
    public void preUpdate(BaseEntity entity) {
        entity.setUpdatedAt(LocalDateTime.now());
        entity.setUpdatedBy(getCurrentUsername());
    }

    /**
     * Gets the current authenticated username.
     *
     * @return the username or "system" if not authenticated
     */
    private String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()
                && !"anonymousUser".equals(authentication.getPrincipal())) {
            return authentication.getName();
        }
        return "system";
    }
}

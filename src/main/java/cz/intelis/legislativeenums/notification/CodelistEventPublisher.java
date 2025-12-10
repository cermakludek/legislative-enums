package cz.intelis.legislativeenums.notification;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * Helper component for publishing codelist change events.
 * Simplifies event publishing from service classes.
 */
@Component
@RequiredArgsConstructor
public class CodelistEventPublisher {

    private final ApplicationEventPublisher eventPublisher;

    /**
     * Publishes a codelist change event.
     *
     * @param codelistName  human-readable name of the codelist (e.g., "Network Types")
     * @param codelistCode  code identifier of the codelist (e.g., "NETWORK_TYPE")
     * @param changeType    type of change (INSERT, UPDATE, DELETE)
     * @param entityId      ID of the changed entity
     * @param entityCode    code of the changed entity
     * @param entityName    name of the changed entity
     */
    public void publishChange(String codelistName, String codelistCode,
                              ChangeType changeType, Long entityId,
                              String entityCode, String entityName) {
        String changedBy = getCurrentUsername();

        CodelistChangeEvent event = new CodelistChangeEvent(
                this, codelistName, codelistCode, changeType,
                entityId, entityCode, entityName, changedBy
        );

        eventPublisher.publishEvent(event);
    }

    /**
     * Convenience method for INSERT events.
     */
    public void publishInsert(String codelistName, String codelistCode,
                              Long entityId, String entityCode, String entityName) {
        publishChange(codelistName, codelistCode, ChangeType.INSERT,
                entityId, entityCode, entityName);
    }

    /**
     * Convenience method for UPDATE events.
     */
    public void publishUpdate(String codelistName, String codelistCode,
                              Long entityId, String entityCode, String entityName) {
        publishChange(codelistName, codelistCode, ChangeType.UPDATE,
                entityId, entityCode, entityName);
    }

    /**
     * Convenience method for DELETE events.
     */
    public void publishDelete(String codelistName, String codelistCode,
                              Long entityId, String entityCode, String entityName) {
        publishChange(codelistName, codelistCode, ChangeType.DELETE,
                entityId, entityCode, entityName);
    }

    private String getCurrentUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
            return auth.getName();
        }
        return "system";
    }
}

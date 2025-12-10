package cz.intelis.legislativeenums.notification;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.time.LocalDateTime;

/**
 * Spring Application Event fired when a codelist entity is changed.
 * Used for internal pub/sub notification system.
 */
@Getter
public class CodelistChangeEvent extends ApplicationEvent {

    private final String codelistName;
    private final String codelistCode;
    private final ChangeType changeType;
    private final Long entityId;
    private final String entityCode;
    private final String entityName;
    private final String changedBy;
    private final LocalDateTime occurredAt;

    public CodelistChangeEvent(Object source, String codelistName, String codelistCode,
                                ChangeType changeType, Long entityId, String entityCode,
                                String entityName, String changedBy) {
        super(source);
        this.codelistName = codelistName;
        this.codelistCode = codelistCode;
        this.changeType = changeType;
        this.entityId = entityId;
        this.entityCode = entityCode;
        this.entityName = entityName;
        this.changedBy = changedBy;
        this.occurredAt = LocalDateTime.now();
    }
}

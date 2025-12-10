package cz.intelis.legislativeenums.notification;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for notifications sent to clients via SSE.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDTO {

    private String id;
    private String codelistName;
    private String codelistCode;
    private ChangeType changeType;
    private Long entityId;
    private String entityCode;
    private String entityName;
    private String changedBy;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime timestamp;

    private String messageCs;
    private String messageEn;

    public static NotificationDTO fromEvent(CodelistChangeEvent event) {
        String actionCs = switch (event.getChangeType()) {
            case INSERT -> "přidán";
            case UPDATE -> "upraven";
            case DELETE -> "smazán";
        };

        String actionEn = switch (event.getChangeType()) {
            case INSERT -> "added";
            case UPDATE -> "updated";
            case DELETE -> "deleted";
        };

        return NotificationDTO.builder()
                .id(java.util.UUID.randomUUID().toString())
                .codelistName(event.getCodelistName())
                .codelistCode(event.getCodelistCode())
                .changeType(event.getChangeType())
                .entityId(event.getEntityId())
                .entityCode(event.getEntityCode())
                .entityName(event.getEntityName())
                .changedBy(event.getChangedBy())
                .timestamp(event.getOccurredAt())
                .messageCs(String.format("%s: %s (%s) byl %s",
                        event.getCodelistName(), event.getEntityName(), event.getEntityCode(), actionCs))
                .messageEn(String.format("%s: %s (%s) was %s",
                        event.getCodelistName(), event.getEntityName(), event.getEntityCode(), actionEn))
                .build();
    }
}

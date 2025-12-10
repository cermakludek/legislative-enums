package cz.intelis.legislativeenums.notification;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;

/**
 * REST controller for real-time notifications via Server-Sent Events (SSE).
 * Clients can subscribe to receive notifications about codelist changes.
 */
@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
@Tag(name = "Notifications", description = "Real-time notifications via Server-Sent Events")
public class NotificationController {

    private final NotificationService notificationService;

    /**
     * Subscribe to real-time notifications via SSE.
     * Connection stays open and receives events for all codelist changes.
     *
     * @return SSE emitter for receiving notifications
     */
    @GetMapping(value = "/subscribe", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @Operation(summary = "Subscribe to notifications",
               description = "Opens an SSE connection to receive real-time notifications about codelist changes")
    public SseEmitter subscribe() {
        return notificationService.createEmitter();
    }

    /**
     * Get current notification system status.
     *
     * @return status including connected clients count
     */
    @GetMapping("/status")
    @Operation(summary = "Get notification system status",
               description = "Returns the current status of the notification system")
    public ResponseEntity<Map<String, Object>> getStatus() {
        return ResponseEntity.ok(Map.of(
                "status", "active",
                "connectedClients", notificationService.getConnectedClientsCount()
        ));
    }
}

package cz.intelis.legislativeenums.notification;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Service for managing real-time notifications via Server-Sent Events (SSE).
 * Listens to CodelistChangeEvents and broadcasts notifications to all connected clients.
 */
@Service
@Slf4j
public class NotificationService {

    private final List<SseEmitter> emitters = new CopyOnWriteArrayList<>();

    /**
     * Creates a new SSE emitter for a client connection.
     * The emitter will receive all codelist change notifications.
     *
     * @return configured SseEmitter
     */
    public SseEmitter createEmitter() {
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE); // No timeout

        emitter.onCompletion(() -> {
            log.debug("SSE connection completed");
            emitters.remove(emitter);
        });

        emitter.onTimeout(() -> {
            log.debug("SSE connection timed out");
            emitter.complete();
            emitters.remove(emitter);
        });

        emitter.onError(e -> {
            log.debug("SSE connection error: {}", e.getMessage());
            emitters.remove(emitter);
        });

        emitters.add(emitter);
        log.info("New SSE client connected. Total clients: {}", emitters.size());

        // Send initial connection event
        try {
            emitter.send(SseEmitter.event()
                    .name("connected")
                    .data("{\"status\":\"connected\"}"));
        } catch (IOException e) {
            log.error("Failed to send initial SSE event", e);
            emitters.remove(emitter);
        }

        return emitter;
    }

    /**
     * Listens to CodelistChangeEvent and broadcasts notification to all connected clients.
     *
     * @param event the codelist change event
     */
    @EventListener
    @Async
    public void handleCodelistChange(CodelistChangeEvent event) {
        NotificationDTO notification = NotificationDTO.fromEvent(event);

        log.info("Broadcasting notification: {} - {} {} ({})",
                event.getCodelistName(), event.getChangeType(),
                event.getEntityCode(), event.getEntityName());

        List<SseEmitter> deadEmitters = new CopyOnWriteArrayList<>();

        for (SseEmitter emitter : emitters) {
            try {
                emitter.send(SseEmitter.event()
                        .name("codelist-change")
                        .data(notification));
            } catch (IOException e) {
                log.debug("Failed to send to emitter, marking for removal");
                deadEmitters.add(emitter);
            }
        }

        emitters.removeAll(deadEmitters);

        if (!deadEmitters.isEmpty()) {
            log.debug("Removed {} dead emitters. Active emitters: {}",
                    deadEmitters.size(), emitters.size());
        }
    }

    /**
     * Gets the count of currently connected SSE clients.
     *
     * @return number of connected clients
     */
    public int getConnectedClientsCount() {
        return emitters.size();
    }
}

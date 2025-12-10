package cz.intelis.legislativeenums.notification;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * Web controller for real-time notifications via Server-Sent Events (SSE).
 * Protected by session-based authentication for web users.
 */
@Controller
@RequestMapping("/web/notifications")
@RequiredArgsConstructor
public class NotificationWebController {

    private final NotificationService notificationService;

    /**
     * Subscribe to real-time notifications via SSE for web users.
     *
     * @return SSE emitter for receiving notifications
     */
    @GetMapping(value = "/subscribe", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @ResponseBody
    public SseEmitter subscribe() {
        return notificationService.createEmitter();
    }
}

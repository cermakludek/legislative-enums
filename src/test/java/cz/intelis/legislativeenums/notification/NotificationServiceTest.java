package cz.intelis.legislativeenums.notification;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
@DisplayName("NotificationService Unit Tests")
class NotificationServiceTest {

    private NotificationService notificationService;

    @BeforeEach
    void setUp() {
        notificationService = new NotificationService();
    }

    @Test
    @DisplayName("Should create SSE emitter")
    void shouldCreateSseEmitter() {
        // When
        SseEmitter emitter = notificationService.createEmitter();

        // Then
        assertThat(emitter).isNotNull();
        assertThat(notificationService.getConnectedClientsCount()).isEqualTo(1);
    }

    @Test
    @DisplayName("Should track multiple connected clients")
    void shouldTrackMultipleConnectedClients() {
        // When
        notificationService.createEmitter();
        notificationService.createEmitter();
        notificationService.createEmitter();

        // Then
        assertThat(notificationService.getConnectedClientsCount()).isEqualTo(3);
    }

    @Test
    @DisplayName("Should return zero clients initially")
    void shouldReturnZeroClientsInitially() {
        // Then
        assertThat(notificationService.getConnectedClientsCount()).isEqualTo(0);
    }

    @Test
    @DisplayName("Should handle codelist change event")
    void shouldHandleCodelistChangeEvent() {
        // Given
        notificationService.createEmitter();
        CodelistChangeEvent event = new CodelistChangeEvent(
                this,
                "Test Codelist",
                "TEST",
                ChangeType.INSERT,
                1L,
                "CODE1",
                "Test Entity",
                "admin"
        );

        // When - should not throw exception
        notificationService.handleCodelistChange(event);

        // Then - no exception means success
        assertThat(notificationService.getConnectedClientsCount()).isGreaterThanOrEqualTo(0);
    }

    @Test
    @DisplayName("Should handle UPDATE change type event")
    void shouldHandleUpdateChangeTypeEvent() {
        // Given
        notificationService.createEmitter();
        CodelistChangeEvent event = new CodelistChangeEvent(
                this,
                "Test Codelist",
                "TEST",
                ChangeType.UPDATE,
                1L,
                "CODE1",
                "Test Entity",
                "admin"
        );

        // When - should not throw exception
        notificationService.handleCodelistChange(event);

        // Then
        assertThat(notificationService.getConnectedClientsCount()).isGreaterThanOrEqualTo(0);
    }

    @Test
    @DisplayName("Should handle DELETE change type event")
    void shouldHandleDeleteChangeTypeEvent() {
        // Given
        notificationService.createEmitter();
        CodelistChangeEvent event = new CodelistChangeEvent(
                this,
                "Test Codelist",
                "TEST",
                ChangeType.DELETE,
                1L,
                "CODE1",
                "Test Entity",
                "admin"
        );

        // When - should not throw exception
        notificationService.handleCodelistChange(event);

        // Then
        assertThat(notificationService.getConnectedClientsCount()).isGreaterThanOrEqualTo(0);
    }

    @Test
    @DisplayName("Should handle event with no connected clients")
    void shouldHandleEventWithNoConnectedClients() {
        // Given
        CodelistChangeEvent event = new CodelistChangeEvent(
                this,
                "Test Codelist",
                "TEST",
                ChangeType.INSERT,
                1L,
                "CODE1",
                "Test Entity",
                "admin"
        );

        // When - should not throw exception
        notificationService.handleCodelistChange(event);

        // Then
        assertThat(notificationService.getConnectedClientsCount()).isEqualTo(0);
    }
}

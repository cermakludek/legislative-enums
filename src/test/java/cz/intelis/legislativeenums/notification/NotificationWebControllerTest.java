package cz.intelis.legislativeenums.notification;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("NotificationWebController Unit Tests")
class NotificationWebControllerTest {

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private NotificationWebController controller;

    private SseEmitter testEmitter;

    @BeforeEach
    void setUp() {
        testEmitter = new SseEmitter(Long.MAX_VALUE);
    }

    @Test
    @DisplayName("subscribe() should create and return SSE emitter")
    void testSubscribe() {
        // Given
        when(notificationService.createEmitter()).thenReturn(testEmitter);

        // When
        SseEmitter result = controller.subscribe();

        // Then
        assertThat(result).isNotNull();
        assertThat(result).isSameAs(testEmitter);
        verify(notificationService).createEmitter();
    }

    @Test
    @DisplayName("subscribe() should return new emitter for each call")
    void testSubscribe_multipleClients() {
        // Given
        SseEmitter firstEmitter = new SseEmitter(Long.MAX_VALUE);
        SseEmitter secondEmitter = new SseEmitter(Long.MAX_VALUE);

        when(notificationService.createEmitter())
                .thenReturn(firstEmitter)
                .thenReturn(secondEmitter);

        // When
        SseEmitter result1 = controller.subscribe();
        SseEmitter result2 = controller.subscribe();

        // Then
        assertThat(result1).isNotNull();
        assertThat(result2).isNotNull();
        assertThat(result1).isNotSameAs(result2);
        verify(notificationService, times(2)).createEmitter();
    }

    @Test
    @DisplayName("subscribe() should delegate to notification service")
    void testSubscribe_delegatesToService() {
        // Given
        when(notificationService.createEmitter()).thenReturn(testEmitter);

        // When
        controller.subscribe();

        // Then
        verify(notificationService, times(1)).createEmitter();
        verifyNoMoreInteractions(notificationService);
    }
}

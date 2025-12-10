package cz.intelis.legislativeenums.monetization;

import cz.intelis.legislativeenums.apikey.ApiKey;
import cz.intelis.legislativeenums.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("MonetizationService Unit Tests")
class MonetizationServiceTest {

    @Mock
    private ApiUsageRepository apiUsageRepository;

    @InjectMocks
    private MonetizationService monetizationService;

    private ApiKey testApiKey;
    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");

        testApiKey = new ApiKey();
        testApiKey.setId(1L);
        testApiKey.setApiKey("test-api-key-123");
        testApiKey.setName("Test Key");
        testApiKey.setEnabled(true);
        testApiKey.setUser(testUser);

        // Clear bucket cache before each test
        monetizationService.clearBucketCache();
    }

    @Test
    @DisplayName("Should allow request within rate limit")
    void shouldAllowRequestWithinRateLimit() {
        // When
        boolean result = monetizationService.checkRateLimit(testApiKey);

        // Then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("Should deny request when rate limit exceeded")
    void shouldDenyRequestWhenRateLimitExceeded() {
        // Given - consume all tokens (default limit is 1000)
        for (int i = 0; i < 1000; i++) {
            monetizationService.checkRateLimit(testApiKey);
        }

        // When
        boolean result = monetizationService.checkRateLimit(testApiKey);

        // Then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("Should record API usage")
    void shouldRecordApiUsage() {
        // Given
        String endpoint = "/api/v1/groups";
        String ipAddress = "127.0.0.1";
        String userAgent = "Mozilla/5.0";
        Integer status = 200;
        Long responseTime = 100L;
        String responseFormat = "JSON";

        when(apiUsageRepository.save(any(ApiUsage.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        // When
        monetizationService.recordUsage(
            testApiKey, endpoint, ipAddress, userAgent, status, responseTime, responseFormat
        );

        // Then
        verify(apiUsageRepository, times(1)).save(any(ApiUsage.class));
    }

    @Test
    @DisplayName("Should record API usage with XML format")
    void shouldRecordApiUsageWithXmlFormat() {
        // Given
        String endpoint = "/api/v1/groups";
        String ipAddress = "127.0.0.1";
        String userAgent = "Mozilla/5.0";
        Integer status = 200;
        Long responseTime = 100L;
        String responseFormat = "XML";

        when(apiUsageRepository.save(any(ApiUsage.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        // When
        monetizationService.recordUsage(
            testApiKey, endpoint, ipAddress, userAgent, status, responseTime, responseFormat
        );

        // Then
        verify(apiUsageRepository, times(1)).save(any(ApiUsage.class));
    }

    @Test
    @DisplayName("Should get usage count for period")
    void shouldGetUsageCount() {
        // Given
        when(apiUsageRepository.countRequestsSince(eq(1L), any(LocalDateTime.class)))
            .thenReturn(50L);

        // When
        Long count = monetizationService.getUsageCount(1L, 24);

        // Then
        assertThat(count).isEqualTo(50L);
        verify(apiUsageRepository, times(1))
            .countRequestsSince(eq(1L), any(LocalDateTime.class));
    }

    @Test
    @DisplayName("Should clear bucket cache")
    void shouldClearBucketCache() {
        // Given - consume some tokens
        for (int i = 0; i < 500; i++) {
            monetizationService.checkRateLimit(testApiKey);
        }

        // When
        monetizationService.clearBucketCache();

        // Then - should be able to use full limit again
        for (int i = 0; i < 1000; i++) {
            boolean result = monetizationService.checkRateLimit(testApiKey);
            assertThat(result).isTrue();
        }
    }

    @Test
    @DisplayName("Should create separate buckets for different API keys")
    void shouldCreateSeparateBucketsForDifferentKeys() {
        // Given
        ApiKey apiKey2 = new ApiKey();
        apiKey2.setId(2L);
        apiKey2.setApiKey("test-api-key-456");
        apiKey2.setName("Test Key 2");
        apiKey2.setEnabled(true);
        apiKey2.setUser(testUser);

        // When - consume all tokens from first key
        for (int i = 0; i < 1000; i++) {
            monetizationService.checkRateLimit(testApiKey);
        }

        // Then - second key should still work
        boolean result = monetizationService.checkRateLimit(apiKey2);
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("Should return zero usage when no records exist")
    void shouldReturnZeroUsageWhenNoRecords() {
        // Given
        when(apiUsageRepository.countRequestsSince(eq(1L), any(LocalDateTime.class)))
            .thenReturn(0L);

        // When
        Long count = monetizationService.getUsageCount(1L, 24);

        // Then
        assertThat(count).isEqualTo(0L);
    }

    @Test
    @DisplayName("Should handle different time periods for usage count")
    void shouldHandleDifferentTimePeriodsForUsageCount() {
        // Given
        when(apiUsageRepository.countRequestsSince(eq(1L), any(LocalDateTime.class)))
            .thenReturn(100L);

        // When
        Long count1Hour = monetizationService.getUsageCount(1L, 1);
        Long count24Hours = monetizationService.getUsageCount(1L, 24);
        Long count168Hours = monetizationService.getUsageCount(1L, 168);

        // Then
        assertThat(count1Hour).isEqualTo(100L);
        assertThat(count24Hours).isEqualTo(100L);
        assertThat(count168Hours).isEqualTo(100L);
        verify(apiUsageRepository, times(3)).countRequestsSince(eq(1L), any(LocalDateTime.class));
    }
}

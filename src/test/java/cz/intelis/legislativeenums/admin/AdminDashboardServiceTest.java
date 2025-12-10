package cz.intelis.legislativeenums.admin;

import cz.intelis.legislativeenums.apikey.ApiKeyRepository;
import cz.intelis.legislativeenums.monetization.ApiUsageRepository;
import cz.intelis.legislativeenums.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AdminDashboardService Unit Tests")
class AdminDashboardServiceTest {

    @Mock
    private ApiUsageRepository apiUsageRepository;

    @Mock
    private ApiKeyRepository apiKeyRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AdminDashboardService adminDashboardService;

    private List<EndpointStats> testEndpointStats;
    private List<UserApiStats> testUserStats;
    private List<DailyRequestCount> testDailyCounts;
    private List<RecentRequest> testRecentRequests;

    @BeforeEach
    void setUp() {
        testEndpointStats = Arrays.asList(
                new EndpointStats("/api/v1/groups", 100L, 50.5),
                new EndpointStats("/api/v1/users", 80L, 45.2)
        );

        testUserStats = Arrays.asList(
                new UserApiStats("admin", "admin@test.com", 150L),
                new UserApiStats("user1", "user1@test.com", 100L)
        );

        testDailyCounts = Arrays.asList(
                new DailyRequestCount(LocalDate.now(), 50L),
                new DailyRequestCount(LocalDate.now().minusDays(1), 30L)
        );

        testRecentRequests = Arrays.asList(
                new RecentRequest(LocalDateTime.now(), "/api/v1/groups", "admin", 200, 45L, "127.0.0.1"),
                new RecentRequest(LocalDateTime.now().minusMinutes(5), "/api/v1/users", "user1", 200, 52L, "192.168.1.1")
        );
    }

    @Test
    @DisplayName("Should return complete dashboard stats")
    void shouldReturnCompleteDashboardStats() {
        // Given
        when(userRepository.count()).thenReturn(10L);
        when(apiKeyRepository.count()).thenReturn(15L);
        when(apiKeyRepository.countByEnabledTrue()).thenReturn(12L);
        when(apiUsageRepository.countTotalRequestsSince(any(LocalDateTime.class))).thenReturn(100L);
        when(apiUsageRepository.getTopEndpoints(any(LocalDateTime.class), eq(10))).thenReturn(testEndpointStats);
        when(apiUsageRepository.getTopUsers(any(LocalDateTime.class), eq(10))).thenReturn(testUserStats);
        when(apiUsageRepository.getDailyRequestCounts(any(LocalDateTime.class))).thenReturn(testDailyCounts);
        when(apiUsageRepository.getRecentRequests(100)).thenReturn(testRecentRequests);
        when(apiUsageRepository.countByResponseFormat("JSON")).thenReturn(80L);
        when(apiUsageRepository.countByResponseFormat("XML")).thenReturn(20L);

        // When
        DashboardStats stats = adminDashboardService.getDashboardStats();

        // Then
        assertThat(stats).isNotNull();
        assertThat(stats.getTotalUsers()).isEqualTo(10L);
        assertThat(stats.getTotalApiKeys()).isEqualTo(15L);
        assertThat(stats.getActiveApiKeys()).isEqualTo(12L);
        assertThat(stats.getTopEndpoints()).hasSize(2);
        assertThat(stats.getTopUsers()).hasSize(2);
        assertThat(stats.getRecentRequests()).hasSize(2);
        assertThat(stats.getJsonRequestCount()).isEqualTo(80L);
        assertThat(stats.getXmlRequestCount()).isEqualTo(20L);
    }

    @Test
    @DisplayName("Should return top endpoints")
    void shouldReturnTopEndpoints() {
        // Given
        LocalDateTime since = LocalDateTime.now().minusDays(30);
        when(apiUsageRepository.getTopEndpoints(any(LocalDateTime.class), eq(10))).thenReturn(testEndpointStats);

        // When
        List<EndpointStats> result = adminDashboardService.getTopEndpoints(since, 10);

        // Then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getEndpoint()).isEqualTo("/api/v1/groups");
        assertThat(result.get(0).getRequestCount()).isEqualTo(100L);
        verify(apiUsageRepository, times(1)).getTopEndpoints(any(LocalDateTime.class), eq(10));
    }

    @Test
    @DisplayName("Should return top users")
    void shouldReturnTopUsers() {
        // Given
        LocalDateTime since = LocalDateTime.now().minusDays(30);
        when(apiUsageRepository.getTopUsers(any(LocalDateTime.class), eq(10))).thenReturn(testUserStats);

        // When
        List<UserApiStats> result = adminDashboardService.getTopUsers(since, 10);

        // Then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getUsername()).isEqualTo("admin");
        assertThat(result.get(0).getRequestCount()).isEqualTo(150L);
        verify(apiUsageRepository, times(1)).getTopUsers(any(LocalDateTime.class), eq(10));
    }

    @Test
    @DisplayName("Should return daily stats with zero-filled days")
    void shouldReturnDailyStatsWithZeroFilledDays() {
        // Given
        when(apiUsageRepository.getDailyRequestCounts(any(LocalDateTime.class))).thenReturn(testDailyCounts);

        // When
        Map<LocalDate, Long> result = adminDashboardService.getDailyStats(30);

        // Then
        assertThat(result).hasSize(30);
        assertThat(result.get(LocalDate.now())).isEqualTo(50L);
        assertThat(result.get(LocalDate.now().minusDays(1))).isEqualTo(30L);
        // Days without data should be zero
        assertThat(result.get(LocalDate.now().minusDays(5))).isEqualTo(0L);
    }

    @Test
    @DisplayName("Should return empty daily stats when no data")
    void shouldReturnEmptyDailyStatsWhenNoData() {
        // Given
        when(apiUsageRepository.getDailyRequestCounts(any(LocalDateTime.class))).thenReturn(Collections.emptyList());

        // When
        Map<LocalDate, Long> result = adminDashboardService.getDailyStats(7);

        // Then
        assertThat(result).hasSize(7);
        result.values().forEach(count -> assertThat(count).isEqualTo(0L));
    }

    @Test
    @DisplayName("Should return recent requests")
    void shouldReturnRecentRequests() {
        // Given
        when(apiUsageRepository.getRecentRequests(20)).thenReturn(testRecentRequests);

        // When
        List<RecentRequest> result = adminDashboardService.getRecentRequests(20);

        // Then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getEndpoint()).isEqualTo("/api/v1/groups");
        assertThat(result.get(0).getResponseStatus()).isEqualTo(200);
        verify(apiUsageRepository, times(1)).getRecentRequests(20);
    }

    @Test
    @DisplayName("Should handle empty recent requests")
    void shouldHandleEmptyRecentRequests() {
        // Given
        when(apiUsageRepository.getRecentRequests(20)).thenReturn(Collections.emptyList());

        // When
        List<RecentRequest> result = adminDashboardService.getRecentRequests(20);

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Should calculate requests for different time periods")
    void shouldCalculateRequestsForDifferentTimePeriods() {
        // Given
        when(userRepository.count()).thenReturn(5L);
        when(apiKeyRepository.count()).thenReturn(5L);
        when(apiKeyRepository.countByEnabledTrue()).thenReturn(5L);
        when(apiUsageRepository.countTotalRequestsSince(any(LocalDateTime.class)))
                .thenReturn(10L)   // today
                .thenReturn(50L)   // week
                .thenReturn(200L)  // month
                .thenReturn(1000L); // all time
        when(apiUsageRepository.getTopEndpoints(any(LocalDateTime.class), eq(10))).thenReturn(Collections.emptyList());
        when(apiUsageRepository.getTopUsers(any(LocalDateTime.class), eq(10))).thenReturn(Collections.emptyList());
        when(apiUsageRepository.getDailyRequestCounts(any(LocalDateTime.class))).thenReturn(Collections.emptyList());
        when(apiUsageRepository.getRecentRequests(100)).thenReturn(Collections.emptyList());
        when(apiUsageRepository.countByResponseFormat(any())).thenReturn(0L);

        // When
        DashboardStats stats = adminDashboardService.getDashboardStats();

        // Then
        assertThat(stats.getTotalRequestsToday()).isEqualTo(10L);
        assertThat(stats.getTotalRequestsWeek()).isEqualTo(50L);
        assertThat(stats.getTotalRequestsMonth()).isEqualTo(200L);
        assertThat(stats.getTotalRequestsAllTime()).isEqualTo(1000L);
    }

    @Test
    @DisplayName("Should return zero counts when no data exists")
    void shouldReturnZeroCountsWhenNoDataExists() {
        // Given
        when(userRepository.count()).thenReturn(0L);
        when(apiKeyRepository.count()).thenReturn(0L);
        when(apiKeyRepository.countByEnabledTrue()).thenReturn(0L);
        when(apiUsageRepository.countTotalRequestsSince(any(LocalDateTime.class))).thenReturn(0L);
        when(apiUsageRepository.getTopEndpoints(any(LocalDateTime.class), eq(10))).thenReturn(Collections.emptyList());
        when(apiUsageRepository.getTopUsers(any(LocalDateTime.class), eq(10))).thenReturn(Collections.emptyList());
        when(apiUsageRepository.getDailyRequestCounts(any(LocalDateTime.class))).thenReturn(Collections.emptyList());
        when(apiUsageRepository.getRecentRequests(100)).thenReturn(Collections.emptyList());
        when(apiUsageRepository.countByResponseFormat("JSON")).thenReturn(0L);
        when(apiUsageRepository.countByResponseFormat("XML")).thenReturn(0L);

        // When
        DashboardStats stats = adminDashboardService.getDashboardStats();

        // Then
        assertThat(stats.getTotalUsers()).isEqualTo(0L);
        assertThat(stats.getTotalApiKeys()).isEqualTo(0L);
        assertThat(stats.getActiveApiKeys()).isEqualTo(0L);
        assertThat(stats.getTotalRequestsToday()).isEqualTo(0L);
        assertThat(stats.getJsonRequestCount()).isEqualTo(0L);
        assertThat(stats.getXmlRequestCount()).isEqualTo(0L);
    }

    @Test
    @DisplayName("Should verify all repository calls are made")
    void shouldVerifyAllRepositoryCallsAreMade() {
        // Given
        when(userRepository.count()).thenReturn(1L);
        when(apiKeyRepository.count()).thenReturn(1L);
        when(apiKeyRepository.countByEnabledTrue()).thenReturn(1L);
        when(apiUsageRepository.countTotalRequestsSince(any(LocalDateTime.class))).thenReturn(1L);
        when(apiUsageRepository.getTopEndpoints(any(LocalDateTime.class), eq(10))).thenReturn(Collections.emptyList());
        when(apiUsageRepository.getTopUsers(any(LocalDateTime.class), eq(10))).thenReturn(Collections.emptyList());
        when(apiUsageRepository.getDailyRequestCounts(any(LocalDateTime.class))).thenReturn(Collections.emptyList());
        when(apiUsageRepository.getRecentRequests(100)).thenReturn(Collections.emptyList());
        when(apiUsageRepository.countByResponseFormat(any())).thenReturn(0L);

        // When
        adminDashboardService.getDashboardStats();

        // Then
        verify(userRepository, times(1)).count();
        verify(apiKeyRepository, times(1)).count();
        verify(apiKeyRepository, times(1)).countByEnabledTrue();
        verify(apiUsageRepository, times(4)).countTotalRequestsSince(any(LocalDateTime.class)); // today, week, month, all-time
        verify(apiUsageRepository, times(1)).getTopEndpoints(any(LocalDateTime.class), eq(10));
        verify(apiUsageRepository, times(1)).getTopUsers(any(LocalDateTime.class), eq(10));
        verify(apiUsageRepository, times(1)).getDailyRequestCounts(any(LocalDateTime.class));
        verify(apiUsageRepository, times(1)).getRecentRequests(100);
        verify(apiUsageRepository, times(1)).countByResponseFormat("JSON");
        verify(apiUsageRepository, times(1)).countByResponseFormat("XML");
    }
}

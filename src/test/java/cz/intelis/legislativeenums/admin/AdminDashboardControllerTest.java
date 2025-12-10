package cz.intelis.legislativeenums.admin;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.Model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AdminDashboardController Unit Tests")
class AdminDashboardControllerTest {

    @Mock
    private AdminDashboardService dashboardService;

    @Mock
    private Model model;

    @InjectMocks
    private AdminDashboardController controller;

    private DashboardStats testStats;

    @BeforeEach
    void setUp() {
        Map<LocalDate, Long> dailyStats = new LinkedHashMap<>();
        dailyStats.put(LocalDate.now(), 50L);
        dailyStats.put(LocalDate.now().minusDays(1), 30L);

        testStats = DashboardStats.builder()
                .totalUsers(10L)
                .totalApiKeys(15L)
                .activeApiKeys(12L)
                .totalRequestsToday(100L)
                .totalRequestsWeek(500L)
                .totalRequestsMonth(2000L)
                .totalRequestsAllTime(10000L)
                .topEndpoints(Arrays.asList(
                        new EndpointStats("/api/v1/groups", 100L, 50.5),
                        new EndpointStats("/api/v1/users", 80L, 45.2)
                ))
                .topUsers(Arrays.asList(
                        new UserApiStats("admin", "admin@test.com", 150L),
                        new UserApiStats("user1", "user1@test.com", 100L)
                ))
                .dailyStats(dailyStats)
                .recentRequests(Arrays.asList(
                        new RecentRequest(LocalDateTime.now(), "/api/v1/groups", "admin", 200, 45L, "127.0.0.1"),
                        new RecentRequest(LocalDateTime.now().minusMinutes(5), "/api/v1/users", "user1", 404, 52L, "192.168.1.1")
                ))
                .jsonRequestCount(80L)
                .xmlRequestCount(20L)
                .build();
    }

    @Test
    @DisplayName("Should return dashboard view name")
    void shouldReturnDashboardViewName() {
        // Given
        when(dashboardService.getDashboardStats()).thenReturn(testStats);

        // When
        String viewName = controller.dashboard(model);

        // Then
        assertThat(viewName).isEqualTo("admin/dashboard");
    }

    @Test
    @DisplayName("Should add stats to model")
    void shouldAddStatsToModel() {
        // Given
        when(dashboardService.getDashboardStats()).thenReturn(testStats);

        // When
        controller.dashboard(model);

        // Then
        verify(model, times(1)).addAttribute("stats", testStats);
    }

    @Test
    @DisplayName("Should call dashboard service")
    void shouldCallDashboardService() {
        // Given
        when(dashboardService.getDashboardStats()).thenReturn(testStats);

        // When
        controller.dashboard(model);

        // Then
        verify(dashboardService, times(1)).getDashboardStats();
    }

    @Test
    @DisplayName("Should handle empty dashboard stats")
    void shouldHandleEmptyDashboardStats() {
        // Given
        DashboardStats emptyStats = DashboardStats.builder()
                .totalUsers(0L)
                .totalApiKeys(0L)
                .activeApiKeys(0L)
                .totalRequestsToday(0L)
                .totalRequestsWeek(0L)
                .totalRequestsMonth(0L)
                .totalRequestsAllTime(0L)
                .topEndpoints(java.util.Collections.emptyList())
                .topUsers(java.util.Collections.emptyList())
                .dailyStats(new LinkedHashMap<>())
                .recentRequests(java.util.Collections.emptyList())
                .jsonRequestCount(0L)
                .xmlRequestCount(0L)
                .build();

        when(dashboardService.getDashboardStats()).thenReturn(emptyStats);

        // When
        String viewName = controller.dashboard(model);

        // Then
        assertThat(viewName).isEqualTo("admin/dashboard");
        verify(model, times(1)).addAttribute("stats", emptyStats);
    }

    @Test
    @DisplayName("Should pass correct stats with all properties")
    void shouldPassCorrectStatsWithAllProperties() {
        // Given
        when(dashboardService.getDashboardStats()).thenReturn(testStats);

        // When
        controller.dashboard(model);

        // Then
        verify(model).addAttribute(eq("stats"), argThat(stats -> {
            DashboardStats ds = (DashboardStats) stats;
            return ds.getTotalUsers() == 10L
                    && ds.getTotalApiKeys() == 15L
                    && ds.getActiveApiKeys() == 12L
                    && ds.getTotalRequestsToday() == 100L
                    && ds.getJsonRequestCount() == 80L
                    && ds.getXmlRequestCount() == 20L
                    && ds.getTopEndpoints().size() == 2
                    && ds.getTopUsers().size() == 2
                    && ds.getRecentRequests().size() == 2;
        }));
    }

    @Test
    @DisplayName("Should handle stats with null collections gracefully")
    void shouldHandleStatsWithNullCollections() {
        // Given
        DashboardStats statsWithNulls = DashboardStats.builder()
                .totalUsers(5L)
                .totalApiKeys(5L)
                .activeApiKeys(3L)
                .totalRequestsToday(10L)
                .totalRequestsWeek(50L)
                .totalRequestsMonth(200L)
                .totalRequestsAllTime(1000L)
                .topEndpoints(null)
                .topUsers(null)
                .dailyStats(null)
                .recentRequests(null)
                .jsonRequestCount(0L)
                .xmlRequestCount(0L)
                .build();

        when(dashboardService.getDashboardStats()).thenReturn(statsWithNulls);

        // When
        String viewName = controller.dashboard(model);

        // Then
        assertThat(viewName).isEqualTo("admin/dashboard");
        verify(model, times(1)).addAttribute("stats", statsWithNulls);
    }
}

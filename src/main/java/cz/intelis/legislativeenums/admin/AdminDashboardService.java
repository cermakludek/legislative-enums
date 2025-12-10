package cz.intelis.legislativeenums.admin;

import cz.intelis.legislativeenums.apikey.ApiKeyRepository;
import cz.intelis.legislativeenums.monetization.ApiUsageRepository;
import cz.intelis.legislativeenums.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Service for providing administrative dashboard statistics and analytics.
 * Aggregates API usage data, user statistics, and system metrics.
 *
 * @author Legislative Codelists Team
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminDashboardService {

    private final ApiUsageRepository apiUsageRepository;
    private final ApiKeyRepository apiKeyRepository;
    private final UserRepository userRepository;

    /**
     * Retrieves comprehensive dashboard statistics including user counts,
     * API key statistics, request metrics, and usage trends.
     *
     * @return DashboardStats containing all dashboard metrics
     */
    public DashboardStats getDashboardStats() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime todayStart = LocalDate.now().atStartOfDay();
        LocalDateTime weekStart = LocalDate.now().minusDays(7).atStartOfDay();
        LocalDateTime monthStart = LocalDate.now().minusDays(30).atStartOfDay();

        return DashboardStats.builder()
                .totalUsers(userRepository.count())
                .totalApiKeys(apiKeyRepository.count())
                .activeApiKeys(apiKeyRepository.countByEnabledTrue())
                .totalRequestsToday(countTotalRequestsSince(todayStart))
                .totalRequestsWeek(countTotalRequestsSince(weekStart))
                .totalRequestsMonth(countTotalRequestsSince(monthStart))
                .totalRequestsAllTime(countTotalRequestsSince(LocalDateTime.of(2000, 1, 1, 0, 0)))
                .topEndpoints(getTopEndpoints(monthStart, 10))
                .topUsers(getTopUsers(monthStart, 10))
                .dailyStats(getDailyStats(30))
                .recentRequests(getRecentRequests(100))
                .jsonRequestCount(apiUsageRepository.countByResponseFormat("JSON"))
                .xmlRequestCount(apiUsageRepository.countByResponseFormat("XML"))
                .build();
    }

    private Long countTotalRequestsSince(LocalDateTime since) {
        return apiUsageRepository.countTotalRequestsSince(since);
    }

    /**
     * Retrieves the most frequently called API endpoints.
     *
     * @param since the start date for the statistics period
     * @param limit maximum number of endpoints to return
     * @return list of endpoint statistics sorted by request count
     */
    public List<EndpointStats> getTopEndpoints(LocalDateTime since, int limit) {
        return apiUsageRepository.getTopEndpoints(since, limit);
    }

    /**
     * Retrieves the most active API users by request count.
     *
     * @param since the start date for the statistics period
     * @param limit maximum number of users to return
     * @return list of user API statistics sorted by request count
     */
    public List<UserApiStats> getTopUsers(LocalDateTime since, int limit) {
        return apiUsageRepository.getTopUsers(since, limit);
    }

    /**
     * Retrieves daily request counts for the specified number of days.
     * Returns a map with dates as keys and request counts as values.
     *
     * @param days number of days to include in statistics
     * @return map of dates to request counts
     */
    public Map<LocalDate, Long> getDailyStats(int days) {
        LocalDateTime startDate = LocalDate.now().minusDays(days - 1).atStartOfDay();
        List<DailyRequestCount> dailyCounts = apiUsageRepository.getDailyRequestCounts(startDate);

        Map<LocalDate, Long> result = new LinkedHashMap<>();
        LocalDate current = LocalDate.now().minusDays(days - 1);
        LocalDate today = LocalDate.now();

        while (!current.isAfter(today)) {
            result.put(current, 0L);
            current = current.plusDays(1);
        }

        for (DailyRequestCount dc : dailyCounts) {
            result.put(dc.getDate(), dc.getCount());
        }

        return result;
    }

    /**
     * Retrieves the most recent API requests.
     *
     * @param limit maximum number of requests to return
     * @return list of recent request details
     */
    public List<RecentRequest> getRecentRequests(int limit) {
        return apiUsageRepository.getRecentRequests(limit);
    }
}

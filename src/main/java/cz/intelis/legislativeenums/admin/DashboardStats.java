package cz.intelis.legislativeenums.admin;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Data
@Builder
public class DashboardStats {
    private Long totalUsers;
    private Long totalApiKeys;
    private Long activeApiKeys;
    private Long totalRequestsToday;
    private Long totalRequestsWeek;
    private Long totalRequestsMonth;
    private Long totalRequestsAllTime;
    private List<EndpointStats> topEndpoints;
    private List<UserApiStats> topUsers;
    private Map<LocalDate, Long> dailyStats;
    private List<RecentRequest> recentRequests;

    // Response format statistics
    private Long jsonRequestCount;
    private Long xmlRequestCount;
}

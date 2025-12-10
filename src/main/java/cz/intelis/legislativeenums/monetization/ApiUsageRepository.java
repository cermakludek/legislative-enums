package cz.intelis.legislativeenums.monetization;

import cz.intelis.legislativeenums.admin.DailyRequestCount;
import cz.intelis.legislativeenums.admin.EndpointStats;
import cz.intelis.legislativeenums.admin.RecentRequest;
import cz.intelis.legislativeenums.admin.UserApiStats;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ApiUsageRepository extends JpaRepository<ApiUsage, Long> {
    List<ApiUsage> findByApiKeyId(Long apiKeyId);

    @Query("SELECT COALESCE(SUM(u.requestCount), 0) FROM ApiUsage u WHERE u.apiKey.id = :apiKeyId AND u.timestamp >= :since")
    Long countRequestsSince(@Param("apiKeyId") Long apiKeyId, @Param("since") LocalDateTime since);

    @Query("SELECT u FROM ApiUsage u WHERE u.apiKey.id = :apiKeyId AND u.timestamp >= :since ORDER BY u.timestamp DESC")
    List<ApiUsage> findRecentUsage(@Param("apiKeyId") Long apiKeyId, @Param("since") LocalDateTime since);

    @Query("SELECT COALESCE(SUM(u.requestCount), 0) FROM ApiUsage u WHERE u.apiKey.id = :apiKeyId AND u.timestamp >= :startDate AND u.timestamp < :endDate")
    Long countRequestsBetween(@Param("apiKeyId") Long apiKeyId, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT COALESCE(SUM(u.requestCount), 0) FROM ApiUsage u WHERE u.timestamp >= :since")
    Long countTotalRequestsSince(@Param("since") LocalDateTime since);

    @Query("SELECT new cz.intelis.legislativeenums.admin.EndpointStats(u.endpoint, SUM(u.requestCount), AVG(u.responseTimeMs)) " +
           "FROM ApiUsage u WHERE u.timestamp >= :since " +
           "GROUP BY u.endpoint ORDER BY SUM(u.requestCount) DESC LIMIT :limit")
    List<EndpointStats> getTopEndpoints(@Param("since") LocalDateTime since, @Param("limit") int limit);

    @Query("SELECT new cz.intelis.legislativeenums.admin.UserApiStats(u.apiKey.user.username, u.apiKey.user.email, SUM(u.requestCount)) " +
           "FROM ApiUsage u WHERE u.timestamp >= :since " +
           "GROUP BY u.apiKey.user.id, u.apiKey.user.username, u.apiKey.user.email ORDER BY SUM(u.requestCount) DESC LIMIT :limit")
    List<UserApiStats> getTopUsers(@Param("since") LocalDateTime since, @Param("limit") int limit);

    @Query("SELECT new cz.intelis.legislativeenums.admin.DailyRequestCount(CAST(u.timestamp AS LocalDate), SUM(u.requestCount)) " +
           "FROM ApiUsage u WHERE u.timestamp >= :since " +
           "GROUP BY CAST(u.timestamp AS LocalDate) ORDER BY CAST(u.timestamp AS LocalDate)")
    List<DailyRequestCount> getDailyRequestCounts(@Param("since") LocalDateTime since);

    @Query("SELECT new cz.intelis.legislativeenums.admin.RecentRequest(u.timestamp, u.endpoint, u.apiKey.user.username, u.responseStatus, u.responseTimeMs, u.ipAddress) " +
           "FROM ApiUsage u ORDER BY u.timestamp DESC LIMIT :limit")
    List<RecentRequest> getRecentRequests(@Param("limit") int limit);

    @Query("SELECT COALESCE(SUM(u.requestCount), 0) FROM ApiUsage u WHERE u.responseFormat = :format")
    Long countByResponseFormat(@Param("format") String format);

    @Query("SELECT COALESCE(SUM(u.requestCount), 0) FROM ApiUsage u WHERE u.responseFormat IS NOT NULL")
    Long countWithKnownFormat();
}

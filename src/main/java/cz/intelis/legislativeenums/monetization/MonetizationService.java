package cz.intelis.legislativeenums.monetization;

import cz.intelis.legislativeenums.apikey.ApiKey;
import io.github.bucket4j.*;
import lombok.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class MonetizationService {
    private final ApiUsageRepository apiUsageRepository;
    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

    private static final long DEFAULT_REQUESTS_LIMIT = 1000;
    private static final int DEFAULT_PERIOD_HOURS = 24;

    /**
     * Checks if an API key has not exceeded its rate limit.
     * Uses token bucket algorithm to enforce rate limits per API key.
     *
     * @param apiKey the API key to check
     * @return true if request is allowed, false if rate limit exceeded
     */
    public boolean checkRateLimit(ApiKey apiKey) {
        Bucket bucket = buckets.computeIfAbsent(apiKey.getApiKey(), k -> createBucket());
        return bucket.tryConsume(1);
    }

    /**
     * Creates a new rate limit bucket with default limits.
     * Default: 1000 requests per 24 hours.
     *
     * @return configured Bucket instance
     */
    private Bucket createBucket() {
        Duration refillPeriod = Duration.ofHours(DEFAULT_PERIOD_HOURS);
        Bandwidth limit = Bandwidth.classic(DEFAULT_REQUESTS_LIMIT, Refill.intervally(DEFAULT_REQUESTS_LIMIT, refillPeriod));
        return Bucket.builder().addLimit(limit).build();
    }

    /**
     * Records API usage statistics for an API request.
     * Captures endpoint, IP address, user agent, response status, and timing.
     *
     * @param apiKey the API key used for the request
     * @param endpoint the endpoint that was called
     * @param ipAddress the client IP address
     * @param userAgent the client user agent
     * @param status the HTTP response status code
     * @param responseTime the response time in milliseconds
     * @param responseFormat the response format (JSON or XML)
     */
    @Transactional
    public void recordUsage(ApiKey apiKey, String endpoint, String ipAddress, String userAgent, Integer status, Long responseTime, String responseFormat) {
        ApiUsage usage = new ApiUsage();
        usage.setApiKey(apiKey);
        usage.setEndpoint(endpoint);
        usage.setRequestCount(1);
        usage.setIpAddress(ipAddress);
        usage.setUserAgent(userAgent);
        usage.setResponseStatus(status);
        usage.setResponseTimeMs(responseTime);
        usage.setResponseFormat(responseFormat);
        apiUsageRepository.save(usage);
    }

    /**
     * Gets the total usage count for an API key within a time window.
     *
     * @param apiKeyId the API key ID
     * @param hours the number of hours to look back
     * @return total number of requests in the time window
     */
    @Transactional(readOnly = true)
    public Long getUsageCount(Long apiKeyId, int hours) {
        return apiUsageRepository.countRequestsSince(apiKeyId, LocalDateTime.now().minusHours(hours));
    }

    /**
     * Clears the rate limit bucket cache.
     * Should be called when rate limits are updated or for maintenance.
     */
    public void clearBucketCache() { buckets.clear(); }
}

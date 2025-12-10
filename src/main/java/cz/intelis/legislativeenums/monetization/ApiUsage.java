package cz.intelis.legislativeenums.monetization;

import cz.intelis.legislativeenums.apikey.ApiKey;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

@Entity @Table(name = "api_usage", indexes = {
    @Index(name = "idx_api_key_timestamp", columnList = "api_key_id, timestamp"),
    @Index(name = "idx_timestamp", columnList = "timestamp")
}) @Data @NoArgsConstructor @AllArgsConstructor
public class ApiUsage {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "api_key_id", nullable = false)
    private ApiKey apiKey;
    @NotBlank @Column(nullable = false)
    private String endpoint;
    @Column(name = "request_count", nullable = false)
    private Integer requestCount = 1;
    @CreationTimestamp @Column(nullable = false, updatable = false)
    private LocalDateTime timestamp;
    @Column(name = "ip_address", length = 45)
    private String ipAddress;
    @Column(name = "user_agent")
    private String userAgent;
    @Column(name = "response_status")
    private Integer responseStatus;
    @Column(name = "response_time_ms")
    private Long responseTimeMs;
    @Column(name = "response_format", length = 10)
    private String responseFormat;
}

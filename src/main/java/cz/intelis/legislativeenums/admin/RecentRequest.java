package cz.intelis.legislativeenums.admin;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecentRequest {
    private LocalDateTime timestamp;
    private String endpoint;
    private String username;
    private Integer responseStatus;
    private Long responseTimeMs;
    private String ipAddress;
}

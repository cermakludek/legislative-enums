package cz.intelis.legislativeenums.admin;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EndpointStats {
    private String endpoint;
    private Long requestCount;
    private Double avgResponseTimeMs;
}

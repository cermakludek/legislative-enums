package cz.intelis.legislativeenums.admin;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserApiStats {
    private String username;
    private String email;
    private Long requestCount;
}

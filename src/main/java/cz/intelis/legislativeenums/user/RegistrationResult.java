package cz.intelis.legislativeenums.user;

import lombok.*;

/**
 * Result of successful user registration containing user info and generated API key.
 *
 * @author Legislative Codelists Team
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegistrationResult {
    private String username;
    private String email;
    private String apiKey;
    private String trialEndDate;
    private String usagePlan;
}

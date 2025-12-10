package cz.intelis.legislativeenums.user;

import jakarta.validation.constraints.*;
import lombok.*;

/**
 * DTO for user registration form.
 *
 * @author Legislative Codelists Team
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegistrationDTO {

    @NotBlank(message = "{validation.required}")
    @Size(min = 3, max = 50, message = "{validation.username.size}")
    private String username;

    @NotBlank(message = "{validation.required}")
    @Email(message = "{validation.email.invalid}")
    private String email;

    @Size(max = 100, message = "{validation.size.max}")
    private String firstName;

    @Size(max = 100, message = "{validation.size.max}")
    private String lastName;

    @NotBlank(message = "{validation.required}")
    @Size(min = 8, max = 100, message = "{validation.password.size}")
    private String password;

    @NotBlank(message = "{validation.required}")
    private String confirmPassword;

    @NotNull(message = "{validation.required}")
    private User.UsagePlan usagePlan;

    public boolean isPasswordMatching() {
        return password != null && password.equals(confirmPassword);
    }
}

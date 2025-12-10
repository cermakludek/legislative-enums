package cz.intelis.legislativeenums.user;

import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data @NoArgsConstructor @AllArgsConstructor
public class UserDTO {
    private Long id;
    @NotBlank @Size(min = 3, max = 50) private String username;
    @NotBlank @Email private String email;
    @Size(max = 100) private String firstName;
    @Size(max = 100) private String lastName;
    private String role;
    private String usagePlan;
    private LocalDate trialEndDate;
    private Boolean enabled;
    private LocalDateTime createdAt;
    private String newPassword;

    public static UserDTO fromEntity(User user) {
        if (user == null) return null;
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setRole(user.getRole().name());
        dto.setUsagePlan(user.getUsagePlan() != null ? user.getUsagePlan().name() : null);
        dto.setTrialEndDate(user.getTrialEndDate());
        dto.setEnabled(user.getEnabled());
        dto.setCreatedAt(user.getCreatedAt());
        return dto;
    }

    public User toEntity() {
        User user = new User();
        user.setId(this.id);
        user.setUsername(this.username);
        user.setEmail(this.email);
        user.setFirstName(this.firstName);
        user.setLastName(this.lastName);
        if (this.role != null) user.setRole(User.UserRole.valueOf(this.role));
        if (this.usagePlan != null) user.setUsagePlan(User.UsagePlan.valueOf(this.usagePlan));
        user.setTrialEndDate(this.trialEndDate);
        user.setEnabled(this.enabled != null ? this.enabled : true);
        return user;
    }

    /**
     * Returns display name: "firstName lastName" if available, otherwise username.
     */
    public String getDisplayName() {
        if (firstName != null && !firstName.isBlank() && lastName != null && !lastName.isBlank()) {
            return firstName + " " + lastName;
        } else if (firstName != null && !firstName.isBlank()) {
            return firstName;
        } else if (lastName != null && !lastName.isBlank()) {
            return lastName;
        }
        return username;
    }
}

package cz.intelis.legislativeenums.user;

import cz.intelis.legislativeenums.apikey.ApiKey;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Entity @Table(name = "users", uniqueConstraints = {@UniqueConstraint(columnNames = "username"), @UniqueConstraint(columnNames = "email")})
@Data @NoArgsConstructor @AllArgsConstructor
public class User {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank @Size(min = 3, max = 50) @Column(nullable = false, unique = true, length = 50)
    private String username;
    @NotBlank @Size(min = 60, max = 100) @Column(nullable = false, length = 100)
    private String password;
    @NotBlank @Email @Column(nullable = false, unique = true)
    private String email;
    @Size(max = 100) @Column(name = "first_name", length = 100)
    private String firstName;
    @Size(max = 100) @Column(name = "last_name", length = 100)
    private String lastName;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 20)
    private UserRole role = UserRole.READER;
    @Enumerated(EnumType.STRING) @Column(name = "usage_plan", length = 20)
    private UsagePlan usagePlan;
    @Column(name = "trial_end_date")
    private LocalDate trialEndDate;
    @Column(nullable = false)
    private Boolean enabled = true;
    @CreationTimestamp @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ApiKey> apiKeys = new ArrayList<>();

    public boolean isInTrialPeriod() {
        return trialEndDate != null && LocalDate.now().isBefore(trialEndDate);
    }

    public boolean isTrialExpired() {
        return trialEndDate != null && LocalDate.now().isAfter(trialEndDate);
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

    public enum UserRole { ADMIN, CONTRIBUTOR, READER }

    public enum UsagePlan {
        PER_MONTH("Měsíční", "Per Month"),
        PER_YEAR("Roční", "Per Year"),
        PER_REQUEST("Za požadavek", "Per Request");

        private final String displayNameCs;
        private final String displayNameEn;

        UsagePlan(String displayNameCs, String displayNameEn) {
            this.displayNameCs = displayNameCs;
            this.displayNameEn = displayNameEn;
        }

        public String getDisplayNameCs() { return displayNameCs; }
        public String getDisplayNameEn() { return displayNameEn; }
    }
}

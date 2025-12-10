package cz.intelis.legislativeenums.user;

import cz.intelis.legislativeenums.apikey.ApiKey;
import cz.intelis.legislativeenums.apikey.ApiKeyRepository;
import lombok.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service layer for managing User entities.
 * Provides user CRUD operations, authentication support, and registration functionality.
 *
 * @author Legislative Codelists Team
 */
@Service @RequiredArgsConstructor @Transactional(readOnly = true)
public class UserService {
    private final UserRepository userRepository;
    private final ApiKeyRepository apiKeyRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Retrieves all users.
     *
     * @return list of all users as DTOs
     */
    public List<UserDTO> findAll() {
        return userRepository.findAll().stream().map(UserDTO::fromEntity).collect(Collectors.toList());
    }

    /**
     * Finds a user by its ID.
     *
     * @param id the user ID
     * @return the user as DTO
     * @throws RuntimeException if user not found
     */
    public UserDTO findById(Long id) {
        return UserDTO.fromEntity(userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found")));
    }

    /**
     * Finds a user by username.
     *
     * @param username the username to search for
     * @return the user as DTO
     * @throws RuntimeException if user not found
     */
    public UserDTO findByUsername(String username) {
        return UserDTO.fromEntity(userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("User not found")));
    }

    /**
     * Creates a new user.
     *
     * @param dto the user data
     * @param password the user password (will be encoded)
     * @return the created user as DTO
     * @throws RuntimeException if username or email already exists
     */
    @Transactional
    public UserDTO create(UserDTO dto, String password) {
        if (userRepository.existsByUsername(dto.getUsername())) throw new RuntimeException("Username exists");
        if (userRepository.existsByEmail(dto.getEmail())) throw new RuntimeException("Email exists");
        User user = dto.toEntity();
        user.setPassword(passwordEncoder.encode(password));
        return UserDTO.fromEntity(userRepository.save(user));
    }

    /**
     * Updates an existing user.
     *
     * @param id the ID of the user to update
     * @param dto the new user data
     * @return the updated user as DTO
     * @throws RuntimeException if user not found or new username/email already exists
     */
    @Transactional
    public UserDTO update(Long id, UserDTO dto) {
        User u = userRepository.findById(id).orElseThrow(() -> new RuntimeException("Not found"));
        if (!u.getUsername().equals(dto.getUsername()) && userRepository.existsByUsername(dto.getUsername())) throw new RuntimeException("Username exists");
        if (!u.getEmail().equals(dto.getEmail()) && userRepository.existsByEmail(dto.getEmail())) throw new RuntimeException("Email exists");
        u.setUsername(dto.getUsername()); u.setEmail(dto.getEmail());
        u.setFirstName(dto.getFirstName()); u.setLastName(dto.getLastName());
        u.setRole(User.UserRole.valueOf(dto.getRole())); u.setEnabled(dto.getEnabled());
        if (dto.getUsagePlan() != null) u.setUsagePlan(User.UsagePlan.valueOf(dto.getUsagePlan()));
        if (dto.getNewPassword() != null && !dto.getNewPassword().isEmpty()) u.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        return UserDTO.fromEntity(userRepository.save(u));
    }

    /**
     * Deletes a user by its ID.
     *
     * @param id the ID of the user to delete
     */
    @Transactional
    public void delete(Long id) {
        userRepository.deleteById(id);
    }

    /**
     * Registers a new user with the selected usage plan.
     * All new registrations start with a 1-month trial period.
     * Generates an API key for the new user.
     *
     * @param dto the registration data
     * @return registration result with user info and API key
     * @throws RuntimeException if username or email already exists
     */
    @Transactional
    public RegistrationResult registerUser(RegistrationDTO dto) {
        if (userRepository.existsByUsername(dto.getUsername())) {
            throw new RuntimeException("Username already exists");
        }
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        // Create user
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setRole(User.UserRole.READER);
        user.setUsagePlan(dto.getUsagePlan());
        user.setTrialEndDate(LocalDate.now().plusMonths(1)); // 1 month trial
        user.setEnabled(true);
        user = userRepository.save(user);

        // Create API key for the user
        ApiKey apiKey = new ApiKey();
        apiKey.setName("Default API Key");
        apiKey.setApiKey(UUID.randomUUID().toString());
        apiKey.setUser(user);
        apiKey.setEnabled(true);
        apiKey = apiKeyRepository.save(apiKey);

        // Return result with API key
        RegistrationResult result = new RegistrationResult();
        result.setUsername(user.getUsername());
        result.setEmail(user.getEmail());
        result.setApiKey(apiKey.getApiKey());
        result.setTrialEndDate(user.getTrialEndDate().toString());
        result.setUsagePlan(user.getUsagePlan().getDisplayNameCs());
        return result;
    }
}

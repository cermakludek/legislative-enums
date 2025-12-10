package cz.intelis.legislativeenums.apikey;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.*;

@Repository
public interface ApiKeyRepository extends JpaRepository<ApiKey, Long> {
    Optional<ApiKey> findByApiKey(String apiKey);
    List<ApiKey> findByUserId(Long userId);
    @Query("SELECT k FROM ApiKey k JOIN FETCH k.user WHERE k.apiKey = :apiKey AND k.enabled = true AND (k.expiresAt IS NULL OR k.expiresAt > CURRENT_TIMESTAMP)")
    Optional<ApiKey> findValidApiKey(@Param("apiKey") String apiKey);
    boolean existsByApiKey(String apiKey);
    Long countByEnabledTrue();
}

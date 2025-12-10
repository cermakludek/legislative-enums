package cz.intelis.legislativeenums.voltagelevel;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for VoltageLevel entities.
 *
 * @author Legislative Codelists Team
 */
@Repository
public interface VoltageLevelRepository extends JpaRepository<VoltageLevel, Long> {

    Optional<VoltageLevel> findByCode(String code);

    boolean existsByCode(String code);

    @Query("SELECT v FROM VoltageLevel v ORDER BY v.sortOrder ASC, v.code ASC")
    List<VoltageLevel> findAllOrdered();

    @Query("SELECT v FROM VoltageLevel v WHERE " +
           "(v.validFrom IS NULL OR v.validFrom <= CURRENT_DATE) " +
           "AND (v.validTo IS NULL OR v.validTo >= CURRENT_DATE) " +
           "ORDER BY v.sortOrder ASC, v.code ASC")
    List<VoltageLevel> findAllCurrentlyValid();
}

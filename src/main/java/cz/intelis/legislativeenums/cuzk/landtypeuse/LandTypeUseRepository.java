package cz.intelis.legislativeenums.cuzk.landtypeuse;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for LandTypeUse entities (Vazba druh pozemku a využití).
 *
 * @author Legislative Codelists Team
 */
@Repository
public interface LandTypeUseRepository extends JpaRepository<LandTypeUse, Long> {

    Optional<LandTypeUse> findByLandTypeCodeAndLandUseCode(String landTypeCode, String landUseCode);

    boolean existsByLandTypeCodeAndLandUseCode(String landTypeCode, String landUseCode);

    List<LandTypeUse> findByLandTypeCode(String landTypeCode);

    List<LandTypeUse> findByLandUseCode(String landUseCode);

    @Query("SELECT l FROM LandTypeUse l ORDER BY l.landTypeCode ASC, l.landUseCode ASC")
    List<LandTypeUse> findAllOrdered();

    @Query("SELECT l FROM LandTypeUse l WHERE " +
           "(l.validFrom IS NULL OR l.validFrom <= CURRENT_DATE) " +
           "AND (l.validTo IS NULL OR l.validTo >= CURRENT_DATE) " +
           "ORDER BY l.landTypeCode ASC, l.landUseCode ASC")
    List<LandTypeUse> findAllCurrentlyValid();
}

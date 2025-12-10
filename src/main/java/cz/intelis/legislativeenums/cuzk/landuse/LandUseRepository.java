package cz.intelis.legislativeenums.cuzk.landuse;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for LandUse entities (Způsob využití pozemku).
 *
 * @author Legislative Codelists Team
 */
@Repository
public interface LandUseRepository extends JpaRepository<LandUse, Long> {

    Optional<LandUse> findByCode(String code);

    boolean existsByCode(String code);

    @Query("SELECT l FROM LandUse l ORDER BY l.sortOrder ASC, l.code ASC")
    List<LandUse> findAllOrdered();

    @Query("SELECT l FROM LandUse l WHERE " +
           "(l.validFrom IS NULL OR l.validFrom <= CURRENT_DATE) " +
           "AND (l.validTo IS NULL OR l.validTo >= CURRENT_DATE) " +
           "ORDER BY l.sortOrder ASC, l.code ASC")
    List<LandUse> findAllCurrentlyValid();
}

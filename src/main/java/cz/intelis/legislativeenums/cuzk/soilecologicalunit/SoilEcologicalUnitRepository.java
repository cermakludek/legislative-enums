package cz.intelis.legislativeenums.cuzk.soilecologicalunit;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for SoilEcologicalUnit entities (Bonitované půdně ekologické jednotky - BPEJ).
 *
 * @author Legislative Codelists Team
 */
@Repository
public interface SoilEcologicalUnitRepository extends JpaRepository<SoilEcologicalUnit, Long> {

    Optional<SoilEcologicalUnit> findByCode(String code);

    boolean existsByCode(String code);

    @Query("SELECT s FROM SoilEcologicalUnit s ORDER BY s.sortOrder ASC, s.code ASC")
    List<SoilEcologicalUnit> findAllOrdered();

    @Query("SELECT s FROM SoilEcologicalUnit s WHERE " +
           "(s.validFrom IS NULL OR s.validFrom <= CURRENT_DATE) " +
           "AND (s.validTo IS NULL OR s.validTo >= CURRENT_DATE) " +
           "ORDER BY s.sortOrder ASC, s.code ASC")
    List<SoilEcologicalUnit> findAllCurrentlyValid();
}

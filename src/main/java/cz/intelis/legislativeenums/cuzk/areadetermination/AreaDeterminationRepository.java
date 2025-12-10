package cz.intelis.legislativeenums.cuzk.areadetermination;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for AreaDetermination entities (Způsob určení výměry).
 *
 * @author Legislative Codelists Team
 */
@Repository
public interface AreaDeterminationRepository extends JpaRepository<AreaDetermination, Long> {

    Optional<AreaDetermination> findByCode(String code);

    boolean existsByCode(String code);

    @Query("SELECT a FROM AreaDetermination a ORDER BY a.sortOrder ASC, a.code ASC")
    List<AreaDetermination> findAllOrdered();

    @Query("SELECT a FROM AreaDetermination a WHERE " +
           "(a.validFrom IS NULL OR a.validFrom <= CURRENT_DATE) " +
           "AND (a.validTo IS NULL OR a.validTo >= CURRENT_DATE) " +
           "ORDER BY a.sortOrder ASC, a.code ASC")
    List<AreaDetermination> findAllCurrentlyValid();
}

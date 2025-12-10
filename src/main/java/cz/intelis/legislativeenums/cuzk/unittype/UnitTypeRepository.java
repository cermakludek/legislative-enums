package cz.intelis.legislativeenums.cuzk.unittype;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for UnitType entities (Typ jednotky).
 *
 * @author Legislative Codelists Team
 */
@Repository
public interface UnitTypeRepository extends JpaRepository<UnitType, Long> {

    Optional<UnitType> findByCode(String code);

    boolean existsByCode(String code);

    @Query("SELECT u FROM UnitType u ORDER BY u.sortOrder ASC, u.code ASC")
    List<UnitType> findAllOrdered();

    @Query("SELECT u FROM UnitType u WHERE " +
           "(u.validFrom IS NULL OR u.validFrom <= CURRENT_DATE) " +
           "AND (u.validTo IS NULL OR u.validTo >= CURRENT_DATE) " +
           "ORDER BY u.sortOrder ASC, u.code ASC")
    List<UnitType> findAllCurrentlyValid();
}

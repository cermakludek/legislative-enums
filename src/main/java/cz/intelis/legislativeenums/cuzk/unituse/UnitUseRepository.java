package cz.intelis.legislativeenums.cuzk.unituse;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for UnitUse entities (Způsob využití jednotky).
 *
 * @author Legislative Codelists Team
 */
@Repository
public interface UnitUseRepository extends JpaRepository<UnitUse, Long> {

    Optional<UnitUse> findByCode(String code);

    boolean existsByCode(String code);

    @Query("SELECT u FROM UnitUse u ORDER BY u.sortOrder ASC, u.code ASC")
    List<UnitUse> findAllOrdered();

    @Query("SELECT u FROM UnitUse u WHERE " +
           "(u.validFrom IS NULL OR u.validFrom <= CURRENT_DATE) " +
           "AND (u.validTo IS NULL OR u.validTo >= CURRENT_DATE) " +
           "ORDER BY u.sortOrder ASC, u.code ASC")
    List<UnitUse> findAllCurrentlyValid();
}

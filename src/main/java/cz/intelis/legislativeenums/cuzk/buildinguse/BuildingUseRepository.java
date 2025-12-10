package cz.intelis.legislativeenums.cuzk.buildinguse;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for BuildingUse entities (Způsob využití stavby).
 *
 * @author Legislative Codelists Team
 */
@Repository
public interface BuildingUseRepository extends JpaRepository<BuildingUse, Long> {

    Optional<BuildingUse> findByCode(String code);

    boolean existsByCode(String code);

    @Query("SELECT b FROM BuildingUse b ORDER BY b.sortOrder ASC, b.code ASC")
    List<BuildingUse> findAllOrdered();

    @Query("SELECT b FROM BuildingUse b WHERE " +
           "(b.validFrom IS NULL OR b.validFrom <= CURRENT_DATE) " +
           "AND (b.validTo IS NULL OR b.validTo >= CURRENT_DATE) " +
           "ORDER BY b.sortOrder ASC, b.code ASC")
    List<BuildingUse> findAllCurrentlyValid();
}

package cz.intelis.legislativeenums.cuzk.buildingtype;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for BuildingType entities (Typ stavby).
 *
 * @author Legislative Codelists Team
 */
@Repository
public interface BuildingTypeRepository extends JpaRepository<BuildingType, Long> {

    Optional<BuildingType> findByCode(String code);

    boolean existsByCode(String code);

    @Query("SELECT b FROM BuildingType b ORDER BY b.sortOrder ASC, b.code ASC")
    List<BuildingType> findAllOrdered();

    @Query("SELECT b FROM BuildingType b WHERE " +
           "(b.validFrom IS NULL OR b.validFrom <= CURRENT_DATE) " +
           "AND (b.validTo IS NULL OR b.validTo >= CURRENT_DATE) " +
           "ORDER BY b.sortOrder ASC, b.code ASC")
    List<BuildingType> findAllCurrentlyValid();
}

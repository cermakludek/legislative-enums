package cz.intelis.legislativeenums.cuzk.buildingtypeuse;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for BuildingTypeUse entities (Vazba typ stavby a využití stavby).
 *
 * @author Legislative Codelists Team
 */
@Repository
public interface BuildingTypeUseRepository extends JpaRepository<BuildingTypeUse, Long> {

    Optional<BuildingTypeUse> findByBuildingTypeCodeAndBuildingUseCode(String buildingTypeCode, String buildingUseCode);

    boolean existsByBuildingTypeCodeAndBuildingUseCode(String buildingTypeCode, String buildingUseCode);

    List<BuildingTypeUse> findByBuildingTypeCode(String buildingTypeCode);

    List<BuildingTypeUse> findByBuildingUseCode(String buildingUseCode);

    @Query("SELECT b FROM BuildingTypeUse b ORDER BY b.buildingTypeCode ASC, b.buildingUseCode ASC")
    List<BuildingTypeUse> findAllOrdered();

    @Query("SELECT b FROM BuildingTypeUse b WHERE " +
           "(b.validFrom IS NULL OR b.validFrom <= CURRENT_DATE) " +
           "AND (b.validTo IS NULL OR b.validTo >= CURRENT_DATE) " +
           "ORDER BY b.buildingTypeCode ASC, b.buildingUseCode ASC")
    List<BuildingTypeUse> findAllCurrentlyValid();
}

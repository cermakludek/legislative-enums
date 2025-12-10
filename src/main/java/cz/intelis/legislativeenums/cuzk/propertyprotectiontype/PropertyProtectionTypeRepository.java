package cz.intelis.legislativeenums.cuzk.propertyprotectiontype;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for PropertyProtectionType entities (Typ ochrany nemovitosti).
 *
 * @author Legislative Codelists Team
 */
@Repository
public interface PropertyProtectionTypeRepository extends JpaRepository<PropertyProtectionType, Long> {

    Optional<PropertyProtectionType> findByCode(String code);

    boolean existsByCode(String code);

    @Query("SELECT p FROM PropertyProtectionType p ORDER BY p.sortOrder ASC, p.code ASC")
    List<PropertyProtectionType> findAllOrdered();

    @Query("SELECT p FROM PropertyProtectionType p WHERE " +
           "(p.validFrom IS NULL OR p.validFrom <= CURRENT_DATE) " +
           "AND (p.validTo IS NULL OR p.validTo >= CURRENT_DATE) " +
           "ORDER BY p.sortOrder ASC, p.code ASC")
    List<PropertyProtectionType> findAllCurrentlyValid();
}

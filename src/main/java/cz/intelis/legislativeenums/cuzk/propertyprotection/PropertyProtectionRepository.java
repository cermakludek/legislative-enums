package cz.intelis.legislativeenums.cuzk.propertyprotection;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for PropertyProtection entities (Způsob ochrany nemovitosti).
 *
 * @author Legislative Codelists Team
 */
@Repository
public interface PropertyProtectionRepository extends JpaRepository<PropertyProtection, Long> {

    Optional<PropertyProtection> findByCode(String code);

    boolean existsByCode(String code);

    List<PropertyProtection> findByProtectionTypeCode(String protectionTypeCode);

    @Query("SELECT p FROM PropertyProtection p ORDER BY p.sortOrder ASC, p.code ASC")
    List<PropertyProtection> findAllOrdered();

    @Query("SELECT p FROM PropertyProtection p WHERE " +
           "(p.validFrom IS NULL OR p.validFrom <= CURRENT_DATE) " +
           "AND (p.validTo IS NULL OR p.validTo >= CURRENT_DATE) " +
           "ORDER BY p.sortOrder ASC, p.code ASC")
    List<PropertyProtection> findAllCurrentlyValid();
}

package cz.intelis.legislativeenums.registry;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for CodelistRegistry entities.
 *
 * @author Legislative Codelists Team
 */
@Repository
public interface CodelistRegistryRepository extends JpaRepository<CodelistRegistry, Long> {

    Optional<CodelistRegistry> findByCode(String code);

    boolean existsByCode(String code);

    @Query("SELECT DISTINCT c FROM CodelistRegistry c LEFT JOIN FETCH c.flags ORDER BY c.sortOrder ASC, c.code ASC")
    List<CodelistRegistry> findAllOrdered();

    @Query("SELECT DISTINCT c FROM CodelistRegistry c JOIN c.flags f WHERE f.id = :flagId ORDER BY c.sortOrder ASC, c.code ASC")
    List<CodelistRegistry> findByFlagId(@Param("flagId") Long flagId);

    @Query("SELECT DISTINCT c FROM CodelistRegistry c LEFT JOIN FETCH c.flags WHERE " +
           "(c.validFrom IS NULL OR c.validFrom <= CURRENT_DATE) " +
           "AND (c.validTo IS NULL OR c.validTo >= CURRENT_DATE) " +
           "ORDER BY c.sortOrder ASC, c.code ASC")
    List<CodelistRegistry> findAllCurrentlyValid();
}

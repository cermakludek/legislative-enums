package cz.intelis.legislativeenums.networktype;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for NetworkType entities.
 *
 * @author Legislative Codelists Team
 */
@Repository
public interface NetworkTypeRepository extends JpaRepository<NetworkType, Long> {

    Optional<NetworkType> findByCode(String code);

    boolean existsByCode(String code);

    @Query("SELECT n FROM NetworkType n ORDER BY n.sortOrder ASC, n.code ASC")
    List<NetworkType> findAllOrdered();

    @Query("SELECT n FROM NetworkType n WHERE " +
           "(n.validFrom IS NULL OR n.validFrom <= CURRENT_DATE) " +
           "AND (n.validTo IS NULL OR n.validTo >= CURRENT_DATE) " +
           "ORDER BY n.sortOrder ASC, n.code ASC")
    List<NetworkType> findAllCurrentlyValid();
}

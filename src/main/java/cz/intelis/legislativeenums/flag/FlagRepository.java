package cz.intelis.legislativeenums.flag;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for Flag entities.
 *
 * @author Legislative Codelists Team
 */
@Repository
public interface FlagRepository extends JpaRepository<Flag, Long> {

    Optional<Flag> findByCode(String code);

    boolean existsByCode(String code);

    @Query("SELECT f FROM Flag f ORDER BY f.sortOrder ASC, f.code ASC")
    List<Flag> findAllOrdered();

    List<Flag> findByActiveTrueOrderBySortOrderAscCodeAsc();
}

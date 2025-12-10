package cz.intelis.legislativeenums.kso;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for BuildingClassification entities.
 *
 * @author Legislative Codelists Team
 */
@Repository
public interface BuildingClassificationRepository extends JpaRepository<BuildingClassification, Long> {

    Optional<BuildingClassification> findByCode(String code);

    boolean existsByCode(String code);

    @Query("SELECT b FROM BuildingClassification b ORDER BY b.code ASC")
    List<BuildingClassification> findAllOrdered();

    /**
     * Find root level items (items without parent).
     */
    @Query("SELECT b FROM BuildingClassification b WHERE b.parent IS NULL ORDER BY b.code ASC")
    List<BuildingClassification> findRootItems();

    /**
     * Find children of a specific parent.
     */
    @Query("SELECT b FROM BuildingClassification b WHERE b.parent.id = :parentId ORDER BY b.code ASC")
    List<BuildingClassification> findByParentId(@Param("parentId") Long parentId);

    /**
     * Find items by level.
     */
    @Query("SELECT b FROM BuildingClassification b WHERE b.level = :level ORDER BY b.code ASC")
    List<BuildingClassification> findByLevel(@Param("level") Integer level);

    /**
     * Search by code or name (case insensitive).
     */
    @Query("SELECT b FROM BuildingClassification b WHERE " +
           "LOWER(b.code) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(b.nameCs) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(b.nameEn) LIKE LOWER(CONCAT('%', :query, '%')) " +
           "ORDER BY b.code ASC")
    List<BuildingClassification> search(@Param("query") String query);

    /**
     * Check if item has children.
     */
    @Query("SELECT COUNT(b) > 0 FROM BuildingClassification b WHERE b.parent.id = :parentId")
    boolean hasChildren(@Param("parentId") Long parentId);
}

package cz.intelis.legislativeenums.kso;

import cz.intelis.legislativeenums.audit.AuditLogService;
import cz.intelis.legislativeenums.notification.CodelistEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service layer for managing BuildingClassification entities.
 * Provides business logic for CRUD operations on building classification (KSO/JKSO).
 *
 * @author Legislative Codelists Team
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BuildingClassificationService {

    private static final String CODELIST_NAME = "Klasifikace staveb (KSO)";
    private static final String CODELIST_CODE = "BUILDING_CLASSIFICATION";

    private final BuildingClassificationRepository repository;
    private final CodelistEventPublisher eventPublisher;
    private final AuditLogService auditLogService;

    /**
     * Retrieves all classifications ordered by code.
     */
    public List<BuildingClassificationDTO> findAll() {
        return repository.findAllOrdered().stream()
                .map(BuildingClassificationDTO::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves the full tree structure (root items with all children).
     */
    public List<BuildingClassificationDTO> findTree() {
        return repository.findRootItems().stream()
                .map(BuildingClassificationDTO::fromEntityWithChildren)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves root level items only.
     */
    public List<BuildingClassificationDTO> findRootItems() {
        return repository.findRootItems().stream()
                .map(BuildingClassificationDTO::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves children of a specific parent.
     */
    public List<BuildingClassificationDTO> findChildren(Long parentId) {
        return repository.findByParentId(parentId).stream()
                .map(BuildingClassificationDTO::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves items by level.
     */
    public List<BuildingClassificationDTO> findByLevel(Integer level) {
        return repository.findByLevel(level).stream()
                .map(BuildingClassificationDTO::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Search by code or name.
     */
    public List<BuildingClassificationDTO> search(String query) {
        return repository.search(query).stream()
                .map(BuildingClassificationDTO::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Finds a classification by its ID.
     */
    public BuildingClassificationDTO findById(Long id) {
        BuildingClassification entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Building classification not found with id: " + id));
        return BuildingClassificationDTO.fromEntity(entity);
    }

    /**
     * Finds a classification by its unique code.
     */
    public BuildingClassificationDTO findByCode(String code) {
        BuildingClassification entity = repository.findByCode(code)
                .orElseThrow(() -> new RuntimeException("Building classification not found with code: " + code));
        return BuildingClassificationDTO.fromEntity(entity);
    }

    /**
     * Creates a new classification.
     */
    @Transactional
    public BuildingClassificationDTO create(BuildingClassificationDTO dto) {
        if (repository.existsByCode(dto.getCode())) {
            throw new RuntimeException("Building classification with code " + dto.getCode() + " already exists");
        }

        BuildingClassification entity = new BuildingClassification();
        mapDtoToEntity(dto, entity);
        entity = repository.save(entity);

        eventPublisher.publishInsert(CODELIST_NAME, CODELIST_CODE,
                entity.getId(), entity.getCode(), entity.getNameCs());

        // Audit log
        auditLogService.logCreate("BuildingClassification", entity.getId(), entity.getCode(),
                toAuditMap(entity));

        return BuildingClassificationDTO.fromEntity(entity);
    }

    /**
     * Updates an existing classification.
     */
    @Transactional
    public BuildingClassificationDTO update(Long id, BuildingClassificationDTO dto) {
        BuildingClassification existing = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Building classification not found with id: " + id));

        if (!existing.getCode().equals(dto.getCode()) && repository.existsByCode(dto.getCode())) {
            throw new RuntimeException("Building classification with code " + dto.getCode() + " already exists");
        }

        // Capture old values for audit
        Map<String, Object> oldValues = toAuditMap(existing);

        mapDtoToEntity(dto, existing);
        existing = repository.save(existing);

        eventPublisher.publishUpdate(CODELIST_NAME, CODELIST_CODE,
                existing.getId(), existing.getCode(), existing.getNameCs());

        // Audit log
        auditLogService.logUpdate("BuildingClassification", existing.getId(), existing.getCode(),
                oldValues, toAuditMap(existing));

        return BuildingClassificationDTO.fromEntity(existing);
    }

    /**
     * Deletes a classification by its ID.
     */
    @Transactional
    public void delete(Long id) {
        BuildingClassification existing = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Building classification not found with id: " + id));

        if (repository.hasChildren(id)) {
            throw new RuntimeException("Cannot delete classification with children. Delete children first.");
        }

        String code = existing.getCode();
        String name = existing.getNameCs();

        // Capture values for audit before deletion
        Map<String, Object> oldValues = toAuditMap(existing);

        repository.deleteById(id);

        eventPublisher.publishDelete(CODELIST_NAME, CODELIST_CODE, id, code, name);

        // Audit log
        auditLogService.logDelete("BuildingClassification", id, code, oldValues);
    }

    /**
     * Get all possible parent items for a given level.
     * Level 1 has no parent, Level 2 can have Level 1 as parent, etc.
     */
    public List<BuildingClassificationDTO> getPossibleParents(Integer level) {
        if (level == null || level <= 1) {
            return List.of();
        }
        return repository.findByLevel(level - 1).stream()
                .map(BuildingClassificationDTO::fromEntity)
                .collect(Collectors.toList());
    }

    private void mapDtoToEntity(BuildingClassificationDTO dto, BuildingClassification entity) {
        entity.setCode(dto.getCode());
        entity.setNameCs(dto.getNameCs());
        entity.setNameEn(dto.getNameEn());
        entity.setDescriptionCs(dto.getDescriptionCs());
        entity.setDescriptionEn(dto.getDescriptionEn());
        entity.setLevel(dto.getLevel());
        entity.setSortOrder(dto.getSortOrder());
        entity.setValidFrom(dto.getValidFrom());
        entity.setValidTo(dto.getValidTo());

        if (dto.getParentId() != null) {
            BuildingClassification parent = repository.findById(dto.getParentId())
                    .orElseThrow(() -> new RuntimeException("Parent classification not found with id: " + dto.getParentId()));
            entity.setParent(parent);
        } else {
            entity.setParent(null);
        }
    }

    /**
     * Convert entity to audit map.
     */
    private Map<String, Object> toAuditMap(BuildingClassification entity) {
        return AuditLogService.createValuesMap(
                "code", entity.getCode(),
                "nameCs", entity.getNameCs(),
                "nameEn", entity.getNameEn(),
                "descriptionCs", entity.getDescriptionCs(),
                "descriptionEn", entity.getDescriptionEn(),
                "level", entity.getLevel(),
                "parentId", entity.getParent() != null ? entity.getParent().getId() : null,
                "validFrom", entity.getValidFrom(),
                "validTo", entity.getValidTo(),
                "sortOrder", entity.getSortOrder()
        );
    }
}

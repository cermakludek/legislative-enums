package cz.intelis.legislativeenums.cuzk.buildingtype;

import cz.intelis.legislativeenums.audit.AuditLogService;
import cz.intelis.legislativeenums.notification.CodelistEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service layer for managing BuildingType entities (Typ stavby).
 *
 * @author Legislative Codelists Team
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BuildingTypeService {

    private static final String CODELIST_NAME = "Typy staveb";
    private static final String CODELIST_CODE = "BUILDING_TYPE";

    private final BuildingTypeRepository repository;
    private final CodelistEventPublisher eventPublisher;
    private final AuditLogService auditLogService;

    /**
     * Retrieves all building types ordered by sort order and code.
     *
     * @return list of all building types as DTOs
     */
    public List<BuildingTypeDTO> findAll() {
        return repository.findAllOrdered().stream()
            .map(BuildingTypeDTO::fromEntity)
            .collect(Collectors.toList());
    }

    /**
     * Retrieves all currently valid building types.
     * A record is valid if current date is within validFrom-validTo range.
     *
     * @return list of currently valid building types as DTOs
     */
    public List<BuildingTypeDTO> findAllCurrentlyValid() {
        return repository.findAllCurrentlyValid().stream()
            .map(BuildingTypeDTO::fromEntity)
            .collect(Collectors.toList());
    }

    /**
     * Finds a building type by its ID.
     *
     * @param id the building type ID
     * @return the building type as DTO
     * @throws RuntimeException if building type not found
     */
    public BuildingTypeDTO findById(Long id) {
        BuildingType entity = repository.findById(id)
            .orElseThrow(() -> new RuntimeException("Building type not found with id: " + id));
        return BuildingTypeDTO.fromEntity(entity);
    }

    /**
     * Finds a building type by its unique code.
     *
     * @param code the building type code
     * @return the building type as DTO
     * @throws RuntimeException if building type not found
     */
    public BuildingTypeDTO findByCode(String code) {
        BuildingType entity = repository.findByCode(code)
            .orElseThrow(() -> new RuntimeException("Building type not found with code: " + code));
        return BuildingTypeDTO.fromEntity(entity);
    }

    /**
     * Creates a new building type.
     *
     * @param dto the building type data
     * @return the created building type as DTO
     * @throws RuntimeException if building type with same code already exists
     */
    @Transactional
    public BuildingTypeDTO create(BuildingTypeDTO dto) {
        if (repository.existsByCode(dto.getCode())) {
            throw new RuntimeException("Building type with code " + dto.getCode() + " already exists");
        }

        BuildingType entity = new BuildingType();
        entity.setCode(dto.getCode());
        entity.setNameCs(dto.getNameCs());
        entity.setNameEn(dto.getNameEn());
        entity.setDescriptionCs(dto.getDescriptionCs());
        entity.setDescriptionEn(dto.getDescriptionEn());
        entity.setAbbreviation(dto.getAbbreviation());
        entity.setEntryCode(dto.getEntryCode());
        entity.setValidFrom(dto.getValidFrom());
        entity.setValidTo(dto.getValidTo());
        entity.setSortOrder(dto.getSortOrder());

        entity = repository.save(entity);

        eventPublisher.publishInsert(CODELIST_NAME, CODELIST_CODE,
                entity.getId(), entity.getCode(), entity.getNameCs());

        // Audit log
        auditLogService.logCreate("BuildingType", entity.getId(), entity.getCode(),
                toAuditMap(entity));

        return BuildingTypeDTO.fromEntity(entity);
    }

    /**
     * Updates an existing building type.
     *
     * @param id the ID of the building type to update
     * @param dto the new building type data
     * @return the updated building type as DTO
     * @throws RuntimeException if building type not found or new code already exists
     */
    @Transactional
    public BuildingTypeDTO update(Long id, BuildingTypeDTO dto) {
        BuildingType existing = repository.findById(id)
            .orElseThrow(() -> new RuntimeException("Building type not found with id: " + id));

        if (!existing.getCode().equals(dto.getCode()) && repository.existsByCode(dto.getCode())) {
            throw new RuntimeException("Building type with code " + dto.getCode() + " already exists");
        }

        // Capture old values for audit
        Map<String, Object> oldValues = toAuditMap(existing);

        existing.setCode(dto.getCode());
        existing.setNameCs(dto.getNameCs());
        existing.setNameEn(dto.getNameEn());
        existing.setDescriptionCs(dto.getDescriptionCs());
        existing.setDescriptionEn(dto.getDescriptionEn());
        existing.setAbbreviation(dto.getAbbreviation());
        existing.setEntryCode(dto.getEntryCode());
        existing.setValidFrom(dto.getValidFrom());
        existing.setValidTo(dto.getValidTo());
        existing.setSortOrder(dto.getSortOrder());

        existing = repository.save(existing);

        eventPublisher.publishUpdate(CODELIST_NAME, CODELIST_CODE,
                existing.getId(), existing.getCode(), existing.getNameCs());

        // Audit log
        auditLogService.logUpdate("BuildingType", existing.getId(), existing.getCode(),
                oldValues, toAuditMap(existing));

        return BuildingTypeDTO.fromEntity(existing);
    }

    /**
     * Deletes a building type by its ID.
     *
     * @param id the ID of the building type to delete
     * @throws RuntimeException if building type not found
     */
    @Transactional
    public void delete(Long id) {
        BuildingType existing = repository.findById(id)
            .orElseThrow(() -> new RuntimeException("Building type not found with id: " + id));

        String code = existing.getCode();
        String name = existing.getNameCs();

        // Capture values for audit before deletion
        Map<String, Object> oldValues = toAuditMap(existing);

        repository.deleteById(id);

        eventPublisher.publishDelete(CODELIST_NAME, CODELIST_CODE, id, code, name);

        // Audit log
        auditLogService.logDelete("BuildingType", id, code, oldValues);
    }

    /**
     * Convert entity to audit map.
     */
    private Map<String, Object> toAuditMap(BuildingType entity) {
        return AuditLogService.createValuesMap(
                "code", entity.getCode(),
                "nameCs", entity.getNameCs(),
                "nameEn", entity.getNameEn(),
                "descriptionCs", entity.getDescriptionCs(),
                "descriptionEn", entity.getDescriptionEn(),
                "abbreviation", entity.getAbbreviation(),
                "entryCode", entity.getEntryCode(),
                "validFrom", entity.getValidFrom(),
                "validTo", entity.getValidTo(),
                "sortOrder", entity.getSortOrder()
        );
    }
}

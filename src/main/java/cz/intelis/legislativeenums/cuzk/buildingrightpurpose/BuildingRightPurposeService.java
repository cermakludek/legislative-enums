package cz.intelis.legislativeenums.cuzk.buildingrightpurpose;

import cz.intelis.legislativeenums.audit.AuditLogService;
import cz.intelis.legislativeenums.notification.CodelistEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service layer for managing BuildingRightPurpose entities (Účel práva stavby).
 *
 * @author Legislative Codelists Team
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BuildingRightPurposeService {

    private static final String CODELIST_NAME = "Účely práva stavby";
    private static final String CODELIST_CODE = "BUILDING_RIGHT_PURPOSE";

    private final BuildingRightPurposeRepository repository;
    private final CodelistEventPublisher eventPublisher;
    private final AuditLogService auditLogService;

    /**
     * Retrieves all building right purposes ordered by sort order and code.
     *
     * @return list of all building right purposes as DTOs
     */
    public List<BuildingRightPurposeDTO> findAll() {
        return repository.findAllOrdered().stream()
            .map(BuildingRightPurposeDTO::fromEntity)
            .collect(Collectors.toList());
    }

    /**
     * Retrieves all currently valid building right purposes.
     * A record is valid if current date is within validFrom-validTo range.
     *
     * @return list of currently valid building right purposes as DTOs
     */
    public List<BuildingRightPurposeDTO> findAllCurrentlyValid() {
        return repository.findAllCurrentlyValid().stream()
            .map(BuildingRightPurposeDTO::fromEntity)
            .collect(Collectors.toList());
    }

    /**
     * Finds a building right purpose by its ID.
     *
     * @param id the building right purpose ID
     * @return the building right purpose as DTO
     * @throws RuntimeException if building right purpose not found
     */
    public BuildingRightPurposeDTO findById(Long id) {
        BuildingRightPurpose entity = repository.findById(id)
            .orElseThrow(() -> new RuntimeException("Building right purpose not found with id: " + id));
        return BuildingRightPurposeDTO.fromEntity(entity);
    }

    /**
     * Finds a building right purpose by its unique code.
     *
     * @param code the building right purpose code
     * @return the building right purpose as DTO
     * @throws RuntimeException if building right purpose not found
     */
    public BuildingRightPurposeDTO findByCode(String code) {
        BuildingRightPurpose entity = repository.findByCode(code)
            .orElseThrow(() -> new RuntimeException("Building right purpose not found with code: " + code));
        return BuildingRightPurposeDTO.fromEntity(entity);
    }

    /**
     * Creates a new building right purpose.
     *
     * @param dto the building right purpose data
     * @return the created building right purpose as DTO
     * @throws RuntimeException if building right purpose with same code already exists
     */
    @Transactional
    public BuildingRightPurposeDTO create(BuildingRightPurposeDTO dto) {
        if (repository.existsByCode(dto.getCode())) {
            throw new RuntimeException("Building right purpose with code " + dto.getCode() + " already exists");
        }

        BuildingRightPurpose entity = new BuildingRightPurpose();
        entity.setCode(dto.getCode());
        entity.setNameCs(dto.getNameCs());
        entity.setNameEn(dto.getNameEn());
        entity.setDescriptionCs(dto.getDescriptionCs());
        entity.setDescriptionEn(dto.getDescriptionEn());
        entity.setValidFrom(dto.getValidFrom());
        entity.setValidTo(dto.getValidTo());
        entity.setSortOrder(dto.getSortOrder());

        entity = repository.save(entity);

        eventPublisher.publishInsert(CODELIST_NAME, CODELIST_CODE,
                entity.getId(), entity.getCode(), entity.getNameCs());

        // Audit log
        auditLogService.logCreate("BuildingRightPurpose", entity.getId(), entity.getCode(),
                toAuditMap(entity));

        return BuildingRightPurposeDTO.fromEntity(entity);
    }

    /**
     * Updates an existing building right purpose.
     *
     * @param id the ID of the building right purpose to update
     * @param dto the new building right purpose data
     * @return the updated building right purpose as DTO
     * @throws RuntimeException if building right purpose not found or new code already exists
     */
    @Transactional
    public BuildingRightPurposeDTO update(Long id, BuildingRightPurposeDTO dto) {
        BuildingRightPurpose existing = repository.findById(id)
            .orElseThrow(() -> new RuntimeException("Building right purpose not found with id: " + id));

        if (!existing.getCode().equals(dto.getCode()) && repository.existsByCode(dto.getCode())) {
            throw new RuntimeException("Building right purpose with code " + dto.getCode() + " already exists");
        }

        // Capture old values for audit
        Map<String, Object> oldValues = toAuditMap(existing);

        existing.setCode(dto.getCode());
        existing.setNameCs(dto.getNameCs());
        existing.setNameEn(dto.getNameEn());
        existing.setDescriptionCs(dto.getDescriptionCs());
        existing.setDescriptionEn(dto.getDescriptionEn());
        existing.setValidFrom(dto.getValidFrom());
        existing.setValidTo(dto.getValidTo());
        existing.setSortOrder(dto.getSortOrder());

        existing = repository.save(existing);

        eventPublisher.publishUpdate(CODELIST_NAME, CODELIST_CODE,
                existing.getId(), existing.getCode(), existing.getNameCs());

        // Audit log
        auditLogService.logUpdate("BuildingRightPurpose", existing.getId(), existing.getCode(),
                oldValues, toAuditMap(existing));

        return BuildingRightPurposeDTO.fromEntity(existing);
    }

    /**
     * Deletes a building right purpose by its ID.
     *
     * @param id the ID of the building right purpose to delete
     * @throws RuntimeException if building right purpose not found
     */
    @Transactional
    public void delete(Long id) {
        BuildingRightPurpose existing = repository.findById(id)
            .orElseThrow(() -> new RuntimeException("Building right purpose not found with id: " + id));

        String code = existing.getCode();
        String name = existing.getNameCs();

        // Capture values for audit before deletion
        Map<String, Object> oldValues = toAuditMap(existing);

        repository.deleteById(id);

        eventPublisher.publishDelete(CODELIST_NAME, CODELIST_CODE, id, code, name);

        // Audit log
        auditLogService.logDelete("BuildingRightPurpose", id, code, oldValues);
    }

    /**
     * Convert entity to audit map.
     */
    private Map<String, Object> toAuditMap(BuildingRightPurpose entity) {
        return AuditLogService.createValuesMap(
                "code", entity.getCode(),
                "nameCs", entity.getNameCs(),
                "nameEn", entity.getNameEn(),
                "descriptionCs", entity.getDescriptionCs(),
                "descriptionEn", entity.getDescriptionEn(),
                "validFrom", entity.getValidFrom(),
                "validTo", entity.getValidTo(),
                "sortOrder", entity.getSortOrder()
        );
    }
}

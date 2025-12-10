package cz.intelis.legislativeenums.cuzk.buildinguse;

import cz.intelis.legislativeenums.audit.AuditLogService;
import cz.intelis.legislativeenums.notification.CodelistEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service layer for managing BuildingUse entities (Způsob využití stavby).
 *
 * @author Legislative Codelists Team
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BuildingUseService {

    private static final String CODELIST_NAME = "Způsoby využití stavby";
    private static final String CODELIST_CODE = "BUILDING_USE";

    private final BuildingUseRepository repository;
    private final CodelistEventPublisher eventPublisher;
    private final AuditLogService auditLogService;

    /**
     * Retrieves all building uses ordered by sort order and code.
     *
     * @return list of all building uses as DTOs
     */
    public List<BuildingUseDTO> findAll() {
        return repository.findAllOrdered().stream()
            .map(BuildingUseDTO::fromEntity)
            .collect(Collectors.toList());
    }

    /**
     * Retrieves all currently valid building uses.
     * A record is valid if current date is within validFrom-validTo range.
     *
     * @return list of currently valid building uses as DTOs
     */
    public List<BuildingUseDTO> findAllCurrentlyValid() {
        return repository.findAllCurrentlyValid().stream()
            .map(BuildingUseDTO::fromEntity)
            .collect(Collectors.toList());
    }

    /**
     * Finds a building use by its ID.
     *
     * @param id the building use ID
     * @return the building use as DTO
     * @throws RuntimeException if building use not found
     */
    public BuildingUseDTO findById(Long id) {
        BuildingUse entity = repository.findById(id)
            .orElseThrow(() -> new RuntimeException("Building use not found with id: " + id));
        return BuildingUseDTO.fromEntity(entity);
    }

    /**
     * Finds a building use by its unique code.
     *
     * @param code the building use code
     * @return the building use as DTO
     * @throws RuntimeException if building use not found
     */
    public BuildingUseDTO findByCode(String code) {
        BuildingUse entity = repository.findByCode(code)
            .orElseThrow(() -> new RuntimeException("Building use not found with code: " + code));
        return BuildingUseDTO.fromEntity(entity);
    }

    /**
     * Creates a new building use.
     *
     * @param dto the building use data
     * @return the created building use as DTO
     * @throws RuntimeException if building use with same code already exists
     */
    @Transactional
    public BuildingUseDTO create(BuildingUseDTO dto) {
        if (repository.existsByCode(dto.getCode())) {
            throw new RuntimeException("Building use with code " + dto.getCode() + " already exists");
        }

        BuildingUse entity = new BuildingUse();
        entity.setCode(dto.getCode());
        entity.setNameCs(dto.getNameCs());
        entity.setNameEn(dto.getNameEn());
        entity.setDescriptionCs(dto.getDescriptionCs());
        entity.setDescriptionEn(dto.getDescriptionEn());
        entity.setAbbreviation(dto.getAbbreviation());
        entity.setValidFrom(dto.getValidFrom());
        entity.setValidTo(dto.getValidTo());
        entity.setSortOrder(dto.getSortOrder());

        entity = repository.save(entity);

        eventPublisher.publishInsert(CODELIST_NAME, CODELIST_CODE,
                entity.getId(), entity.getCode(), entity.getNameCs());

        // Audit log
        auditLogService.logCreate("BuildingUse", entity.getId(), entity.getCode(),
                toAuditMap(entity));

        return BuildingUseDTO.fromEntity(entity);
    }

    /**
     * Updates an existing building use.
     *
     * @param id the ID of the building use to update
     * @param dto the new building use data
     * @return the updated building use as DTO
     * @throws RuntimeException if building use not found or new code already exists
     */
    @Transactional
    public BuildingUseDTO update(Long id, BuildingUseDTO dto) {
        BuildingUse existing = repository.findById(id)
            .orElseThrow(() -> new RuntimeException("Building use not found with id: " + id));

        if (!existing.getCode().equals(dto.getCode()) && repository.existsByCode(dto.getCode())) {
            throw new RuntimeException("Building use with code " + dto.getCode() + " already exists");
        }

        // Capture old values for audit
        Map<String, Object> oldValues = toAuditMap(existing);

        existing.setCode(dto.getCode());
        existing.setNameCs(dto.getNameCs());
        existing.setNameEn(dto.getNameEn());
        existing.setDescriptionCs(dto.getDescriptionCs());
        existing.setDescriptionEn(dto.getDescriptionEn());
        existing.setAbbreviation(dto.getAbbreviation());
        existing.setValidFrom(dto.getValidFrom());
        existing.setValidTo(dto.getValidTo());
        existing.setSortOrder(dto.getSortOrder());

        existing = repository.save(existing);

        eventPublisher.publishUpdate(CODELIST_NAME, CODELIST_CODE,
                existing.getId(), existing.getCode(), existing.getNameCs());

        // Audit log
        auditLogService.logUpdate("BuildingUse", existing.getId(), existing.getCode(),
                oldValues, toAuditMap(existing));

        return BuildingUseDTO.fromEntity(existing);
    }

    /**
     * Deletes a building use by its ID.
     *
     * @param id the ID of the building use to delete
     * @throws RuntimeException if building use not found
     */
    @Transactional
    public void delete(Long id) {
        BuildingUse existing = repository.findById(id)
            .orElseThrow(() -> new RuntimeException("Building use not found with id: " + id));

        String code = existing.getCode();
        String name = existing.getNameCs();

        // Capture values for audit before deletion
        Map<String, Object> oldValues = toAuditMap(existing);

        repository.deleteById(id);

        eventPublisher.publishDelete(CODELIST_NAME, CODELIST_CODE, id, code, name);

        // Audit log
        auditLogService.logDelete("BuildingUse", id, code, oldValues);
    }

    /**
     * Convert entity to audit map.
     */
    private Map<String, Object> toAuditMap(BuildingUse entity) {
        return AuditLogService.createValuesMap(
                "code", entity.getCode(),
                "nameCs", entity.getNameCs(),
                "nameEn", entity.getNameEn(),
                "descriptionCs", entity.getDescriptionCs(),
                "descriptionEn", entity.getDescriptionEn(),
                "abbreviation", entity.getAbbreviation(),
                "validFrom", entity.getValidFrom(),
                "validTo", entity.getValidTo(),
                "sortOrder", entity.getSortOrder()
        );
    }
}

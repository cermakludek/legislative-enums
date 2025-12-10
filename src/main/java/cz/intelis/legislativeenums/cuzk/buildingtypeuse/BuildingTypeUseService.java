package cz.intelis.legislativeenums.cuzk.buildingtypeuse;

import cz.intelis.legislativeenums.audit.AuditLogService;
import cz.intelis.legislativeenums.notification.CodelistEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service layer for managing BuildingTypeUse entities (Vazba typ stavby a využití stavby).
 *
 * @author Legislative Codelists Team
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BuildingTypeUseService {

    private static final String CODELIST_NAME = "Vazby typ stavby - využití";
    private static final String CODELIST_CODE = "BUILDING_TYPE_USE";

    private final BuildingTypeUseRepository repository;
    private final CodelistEventPublisher eventPublisher;
    private final AuditLogService auditLogService;

    /**
     * Retrieves all building type-use relations ordered by building type code and building use code.
     *
     * @return list of all building type-use relations as DTOs
     */
    public List<BuildingTypeUseDTO> findAll() {
        return repository.findAllOrdered().stream()
            .map(BuildingTypeUseDTO::fromEntity)
            .collect(Collectors.toList());
    }

    /**
     * Retrieves all currently valid building type-use relations.
     * A record is valid if current date is within validFrom-validTo range.
     *
     * @return list of currently valid building type-use relations as DTOs
     */
    public List<BuildingTypeUseDTO> findAllCurrentlyValid() {
        return repository.findAllCurrentlyValid().stream()
            .map(BuildingTypeUseDTO::fromEntity)
            .collect(Collectors.toList());
    }

    /**
     * Finds a building type-use relation by its ID.
     *
     * @param id the building type-use relation ID
     * @return the building type-use relation as DTO
     * @throws RuntimeException if building type-use relation not found
     */
    public BuildingTypeUseDTO findById(Long id) {
        BuildingTypeUse entity = repository.findById(id)
            .orElseThrow(() -> new RuntimeException("Building type use relation not found with id: " + id));
        return BuildingTypeUseDTO.fromEntity(entity);
    }

    /**
     * Finds all building type-use relations for a specific building type.
     *
     * @param buildingTypeCode the building type code to search for
     * @return list of matching building type-use relations as DTOs
     */
    public List<BuildingTypeUseDTO> findByBuildingTypeCode(String buildingTypeCode) {
        return repository.findByBuildingTypeCode(buildingTypeCode).stream()
            .map(BuildingTypeUseDTO::fromEntity)
            .collect(Collectors.toList());
    }

    /**
     * Finds all building type-use relations for a specific building use.
     *
     * @param buildingUseCode the building use code to search for
     * @return list of matching building type-use relations as DTOs
     */
    public List<BuildingTypeUseDTO> findByBuildingUseCode(String buildingUseCode) {
        return repository.findByBuildingUseCode(buildingUseCode).stream()
            .map(BuildingTypeUseDTO::fromEntity)
            .collect(Collectors.toList());
    }

    /**
     * Creates a new building type-use relation.
     *
     * @param dto the building type-use relation data
     * @return the created building type-use relation as DTO
     * @throws RuntimeException if relation for given building type and building use already exists
     */
    @Transactional
    public BuildingTypeUseDTO create(BuildingTypeUseDTO dto) {
        if (repository.existsByBuildingTypeCodeAndBuildingUseCode(dto.getBuildingTypeCode(), dto.getBuildingUseCode())) {
            throw new RuntimeException("Building type use relation already exists for building type " + dto.getBuildingTypeCode() + " and building use " + dto.getBuildingUseCode());
        }

        BuildingTypeUse entity = new BuildingTypeUse();
        entity.setBuildingTypeCode(dto.getBuildingTypeCode());
        entity.setBuildingUseCode(dto.getBuildingUseCode());
        entity.setValidFrom(dto.getValidFrom());
        entity.setValidTo(dto.getValidTo());

        entity = repository.save(entity);

        String code = entity.getBuildingTypeCode() + "-" + entity.getBuildingUseCode();
        eventPublisher.publishInsert(CODELIST_NAME, CODELIST_CODE,
                entity.getId(), code, code);

        auditLogService.logCreate("BuildingTypeUse", entity.getId(), code, toAuditMap(entity));

        return BuildingTypeUseDTO.fromEntity(entity);
    }

    /**
     * Updates an existing building type-use relation.
     *
     * @param id the ID of the building type-use relation to update
     * @param dto the new building type-use relation data
     * @return the updated building type-use relation as DTO
     * @throws RuntimeException if relation not found or new combination already exists
     */
    @Transactional
    public BuildingTypeUseDTO update(Long id, BuildingTypeUseDTO dto) {
        BuildingTypeUse existing = repository.findById(id)
            .orElseThrow(() -> new RuntimeException("Building type use relation not found with id: " + id));

        if ((!existing.getBuildingTypeCode().equals(dto.getBuildingTypeCode()) || !existing.getBuildingUseCode().equals(dto.getBuildingUseCode()))
                && repository.existsByBuildingTypeCodeAndBuildingUseCode(dto.getBuildingTypeCode(), dto.getBuildingUseCode())) {
            throw new RuntimeException("Building type use relation already exists for building type " + dto.getBuildingTypeCode() + " and building use " + dto.getBuildingUseCode());
        }

        Map<String, Object> oldValues = toAuditMap(existing);

        existing.setBuildingTypeCode(dto.getBuildingTypeCode());
        existing.setBuildingUseCode(dto.getBuildingUseCode());
        existing.setValidFrom(dto.getValidFrom());
        existing.setValidTo(dto.getValidTo());

        existing = repository.save(existing);

        String code = existing.getBuildingTypeCode() + "-" + existing.getBuildingUseCode();
        eventPublisher.publishUpdate(CODELIST_NAME, CODELIST_CODE,
                existing.getId(), code, code);

        auditLogService.logUpdate("BuildingTypeUse", existing.getId(), code, oldValues, toAuditMap(existing));

        return BuildingTypeUseDTO.fromEntity(existing);
    }

    /**
     * Deletes a building type-use relation by its ID.
     *
     * @param id the ID of the building type-use relation to delete
     * @throws RuntimeException if building type-use relation not found
     */
    @Transactional
    public void delete(Long id) {
        BuildingTypeUse existing = repository.findById(id)
            .orElseThrow(() -> new RuntimeException("Building type use relation not found with id: " + id));

        String code = existing.getBuildingTypeCode() + "-" + existing.getBuildingUseCode();
        Map<String, Object> oldValues = toAuditMap(existing);

        repository.deleteById(id);

        eventPublisher.publishDelete(CODELIST_NAME, CODELIST_CODE, id, code, code);

        auditLogService.logDelete("BuildingTypeUse", id, code, oldValues);
    }

    private Map<String, Object> toAuditMap(BuildingTypeUse entity) {
        return Map.of(
            "buildingTypeCode", entity.getBuildingTypeCode(),
            "buildingUseCode", entity.getBuildingUseCode(),
            "validFrom", entity.getValidFrom() != null ? entity.getValidFrom().toString() : "",
            "validTo", entity.getValidTo() != null ? entity.getValidTo().toString() : ""
        );
    }
}

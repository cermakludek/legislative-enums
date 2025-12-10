package cz.intelis.legislativeenums.cuzk.landtype;

import cz.intelis.legislativeenums.audit.AuditLogService;
import cz.intelis.legislativeenums.notification.CodelistEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service layer for managing LandType entities (Druh pozemku).
 *
 * @author Legislative Codelists Team
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LandTypeService {

    private static final String CODELIST_NAME = "Druhy pozemků";
    private static final String CODELIST_CODE = "LAND_TYPE";

    private final LandTypeRepository repository;
    private final CodelistEventPublisher eventPublisher;
    private final AuditLogService auditLogService;

    /**
     * Retrieves all land types ordered by sort order and code.
     *
     * @return list of all land types as DTOs
     */
    public List<LandTypeDTO> findAll() {
        return repository.findAllOrdered().stream()
            .map(LandTypeDTO::fromEntity)
            .collect(Collectors.toList());
    }

    /**
     * Retrieves all currently valid land types.
     * A record is valid if current date is within validFrom-validTo range.
     *
     * @return list of currently valid land types as DTOs
     */
    public List<LandTypeDTO> findAllCurrentlyValid() {
        return repository.findAllCurrentlyValid().stream()
            .map(LandTypeDTO::fromEntity)
            .collect(Collectors.toList());
    }

    /**
     * Finds a land type by its ID.
     *
     * @param id the land type ID
     * @return the land type as DTO
     * @throws RuntimeException if land type not found
     */
    public LandTypeDTO findById(Long id) {
        LandType entity = repository.findById(id)
            .orElseThrow(() -> new RuntimeException("Land type not found with id: " + id));
        return LandTypeDTO.fromEntity(entity);
    }

    /**
     * Finds a land type by its unique code.
     *
     * @param code the land type code
     * @return the land type as DTO
     * @throws RuntimeException if land type not found
     */
    public LandTypeDTO findByCode(String code) {
        LandType entity = repository.findByCode(code)
            .orElseThrow(() -> new RuntimeException("Land type not found with code: " + code));
        return LandTypeDTO.fromEntity(entity);
    }

    /**
     * Creates a new land type.
     *
     * @param dto the land type data
     * @return the created land type as DTO
     * @throws RuntimeException if land type with same code already exists
     */
    @Transactional
    public LandTypeDTO create(LandTypeDTO dto) {
        if (repository.existsByCode(dto.getCode())) {
            throw new RuntimeException("Land type with code " + dto.getCode() + " already exists");
        }

        LandType entity = new LandType();
        entity.setCode(dto.getCode());
        entity.setNameCs(dto.getNameCs());
        entity.setNameEn(dto.getNameEn());
        entity.setDescriptionCs(dto.getDescriptionCs());
        entity.setDescriptionEn(dto.getDescriptionEn());
        entity.setAbbreviation(dto.getAbbreviation());
        entity.setAgriculturalLand(dto.getAgriculturalLand());
        entity.setLandParcelTypeCode(dto.getLandParcelTypeCode());
        entity.setBuildingParcel(dto.getBuildingParcel());
        entity.setMandatoryLandProtection(dto.getMandatoryLandProtection());
        entity.setMandatoryLandUse(dto.getMandatoryLandUse());
        entity.setValidFrom(dto.getValidFrom());
        entity.setValidTo(dto.getValidTo());
        entity.setSortOrder(dto.getSortOrder());

        entity = repository.save(entity);

        eventPublisher.publishInsert(CODELIST_NAME, CODELIST_CODE,
                entity.getId(), entity.getCode(), entity.getNameCs());

        // Audit log
        auditLogService.logCreate("LandType", entity.getId(), entity.getCode(),
                toAuditMap(entity));

        return LandTypeDTO.fromEntity(entity);
    }

    /**
     * Updates an existing land type.
     *
     * @param id the ID of the land type to update
     * @param dto the new land type data
     * @return the updated land type as DTO
     * @throws RuntimeException if land type not found or new code already exists
     */
    @Transactional
    public LandTypeDTO update(Long id, LandTypeDTO dto) {
        LandType existing = repository.findById(id)
            .orElseThrow(() -> new RuntimeException("Land type not found with id: " + id));

        if (!existing.getCode().equals(dto.getCode()) && repository.existsByCode(dto.getCode())) {
            throw new RuntimeException("Land type with code " + dto.getCode() + " already exists");
        }

        // Capture old values for audit
        Map<String, Object> oldValues = toAuditMap(existing);

        existing.setCode(dto.getCode());
        existing.setNameCs(dto.getNameCs());
        existing.setNameEn(dto.getNameEn());
        existing.setDescriptionCs(dto.getDescriptionCs());
        existing.setDescriptionEn(dto.getDescriptionEn());
        existing.setAbbreviation(dto.getAbbreviation());
        existing.setAgriculturalLand(dto.getAgriculturalLand());
        existing.setLandParcelTypeCode(dto.getLandParcelTypeCode());
        existing.setBuildingParcel(dto.getBuildingParcel());
        existing.setMandatoryLandProtection(dto.getMandatoryLandProtection());
        existing.setMandatoryLandUse(dto.getMandatoryLandUse());
        existing.setValidFrom(dto.getValidFrom());
        existing.setValidTo(dto.getValidTo());
        existing.setSortOrder(dto.getSortOrder());

        existing = repository.save(existing);

        eventPublisher.publishUpdate(CODELIST_NAME, CODELIST_CODE,
                existing.getId(), existing.getCode(), existing.getNameCs());

        // Audit log
        auditLogService.logUpdate("LandType", existing.getId(), existing.getCode(),
                oldValues, toAuditMap(existing));

        return LandTypeDTO.fromEntity(existing);
    }

    /**
     * Deletes a land type by its ID.
     *
     * @param id the ID of the land type to delete
     * @throws RuntimeException if land type not found
     */
    @Transactional
    public void delete(Long id) {
        LandType existing = repository.findById(id)
            .orElseThrow(() -> new RuntimeException("Land type not found with id: " + id));

        String code = existing.getCode();
        String name = existing.getNameCs();

        // Capture values for audit before deletion
        Map<String, Object> oldValues = toAuditMap(existing);

        repository.deleteById(id);

        eventPublisher.publishDelete(CODELIST_NAME, CODELIST_CODE, id, code, name);

        // Audit log
        auditLogService.logDelete("LandType", id, code, oldValues);
    }

    /**
     * Convert entity to audit map.
     */
    private Map<String, Object> toAuditMap(LandType entity) {
        return AuditLogService.createValuesMap(
                "code", entity.getCode(),
                "nameCs", entity.getNameCs(),
                "nameEn", entity.getNameEn(),
                "descriptionCs", entity.getDescriptionCs(),
                "descriptionEn", entity.getDescriptionEn(),
                "abbreviation", entity.getAbbreviation(),
                "agriculturalLand", entity.getAgriculturalLand(),
                "landParcelTypeCode", entity.getLandParcelTypeCode(),
                "buildingParcel", entity.getBuildingParcel(),
                "mandatoryLandProtection", entity.getMandatoryLandProtection(),
                "mandatoryLandUse", entity.getMandatoryLandUse(),
                "validFrom", entity.getValidFrom(),
                "validTo", entity.getValidTo(),
                "sortOrder", entity.getSortOrder()
        );
    }
}

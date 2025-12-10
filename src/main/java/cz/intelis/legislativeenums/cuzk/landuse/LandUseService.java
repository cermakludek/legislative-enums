package cz.intelis.legislativeenums.cuzk.landuse;

import cz.intelis.legislativeenums.audit.AuditLogService;
import cz.intelis.legislativeenums.notification.CodelistEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service layer for managing LandUse entities (Způsob využití pozemku).
 *
 * @author Legislative Codelists Team
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LandUseService {

    private static final String CODELIST_NAME = "Způsoby využití pozemku";
    private static final String CODELIST_CODE = "LAND_USE";

    private final LandUseRepository repository;
    private final CodelistEventPublisher eventPublisher;
    private final AuditLogService auditLogService;

    /**
     * Retrieves all land uses ordered by sort order and code.
     *
     * @return list of all land uses as DTOs
     */
    public List<LandUseDTO> findAll() {
        return repository.findAllOrdered().stream()
            .map(LandUseDTO::fromEntity)
            .collect(Collectors.toList());
    }

    /**
     * Retrieves all currently valid land uses.
     * A record is valid if current date is within validFrom-validTo range.
     *
     * @return list of currently valid land uses as DTOs
     */
    public List<LandUseDTO> findAllCurrentlyValid() {
        return repository.findAllCurrentlyValid().stream()
            .map(LandUseDTO::fromEntity)
            .collect(Collectors.toList());
    }

    /**
     * Finds a land use by its ID.
     *
     * @param id the land use ID
     * @return the land use as DTO
     * @throws RuntimeException if land use not found
     */
    public LandUseDTO findById(Long id) {
        LandUse entity = repository.findById(id)
            .orElseThrow(() -> new RuntimeException("Land use not found with id: " + id));
        return LandUseDTO.fromEntity(entity);
    }

    /**
     * Finds a land use by its unique code.
     *
     * @param code the land use code
     * @return the land use as DTO
     * @throws RuntimeException if land use not found
     */
    public LandUseDTO findByCode(String code) {
        LandUse entity = repository.findByCode(code)
            .orElseThrow(() -> new RuntimeException("Land use not found with code: " + code));
        return LandUseDTO.fromEntity(entity);
    }

    /**
     * Creates a new land use.
     *
     * @param dto the land use data
     * @return the created land use as DTO
     * @throws RuntimeException if land use with same code already exists
     */
    @Transactional
    public LandUseDTO create(LandUseDTO dto) {
        if (repository.existsByCode(dto.getCode())) {
            throw new RuntimeException("Land use with code " + dto.getCode() + " already exists");
        }

        LandUse entity = new LandUse();
        entity.setCode(dto.getCode());
        entity.setNameCs(dto.getNameCs());
        entity.setNameEn(dto.getNameEn());
        entity.setDescriptionCs(dto.getDescriptionCs());
        entity.setDescriptionEn(dto.getDescriptionEn());
        entity.setAbbreviation(dto.getAbbreviation());
        entity.setLandParcelTypeCode(dto.getLandParcelTypeCode());
        entity.setValidFrom(dto.getValidFrom());
        entity.setValidTo(dto.getValidTo());
        entity.setSortOrder(dto.getSortOrder());

        entity = repository.save(entity);

        eventPublisher.publishInsert(CODELIST_NAME, CODELIST_CODE,
                entity.getId(), entity.getCode(), entity.getNameCs());

        // Audit log
        auditLogService.logCreate("LandUse", entity.getId(), entity.getCode(),
                toAuditMap(entity));

        return LandUseDTO.fromEntity(entity);
    }

    /**
     * Updates an existing land use.
     *
     * @param id the ID of the land use to update
     * @param dto the new land use data
     * @return the updated land use as DTO
     * @throws RuntimeException if land use not found or new code already exists
     */
    @Transactional
    public LandUseDTO update(Long id, LandUseDTO dto) {
        LandUse existing = repository.findById(id)
            .orElseThrow(() -> new RuntimeException("Land use not found with id: " + id));

        if (!existing.getCode().equals(dto.getCode()) && repository.existsByCode(dto.getCode())) {
            throw new RuntimeException("Land use with code " + dto.getCode() + " already exists");
        }

        // Capture old values for audit
        Map<String, Object> oldValues = toAuditMap(existing);

        existing.setCode(dto.getCode());
        existing.setNameCs(dto.getNameCs());
        existing.setNameEn(dto.getNameEn());
        existing.setDescriptionCs(dto.getDescriptionCs());
        existing.setDescriptionEn(dto.getDescriptionEn());
        existing.setAbbreviation(dto.getAbbreviation());
        existing.setLandParcelTypeCode(dto.getLandParcelTypeCode());
        existing.setValidFrom(dto.getValidFrom());
        existing.setValidTo(dto.getValidTo());
        existing.setSortOrder(dto.getSortOrder());

        existing = repository.save(existing);

        eventPublisher.publishUpdate(CODELIST_NAME, CODELIST_CODE,
                existing.getId(), existing.getCode(), existing.getNameCs());

        // Audit log
        auditLogService.logUpdate("LandUse", existing.getId(), existing.getCode(),
                oldValues, toAuditMap(existing));

        return LandUseDTO.fromEntity(existing);
    }

    /**
     * Deletes a land use by its ID.
     *
     * @param id the ID of the land use to delete
     * @throws RuntimeException if land use not found
     */
    @Transactional
    public void delete(Long id) {
        LandUse existing = repository.findById(id)
            .orElseThrow(() -> new RuntimeException("Land use not found with id: " + id));

        String code = existing.getCode();
        String name = existing.getNameCs();

        // Capture values for audit before deletion
        Map<String, Object> oldValues = toAuditMap(existing);

        repository.deleteById(id);

        eventPublisher.publishDelete(CODELIST_NAME, CODELIST_CODE, id, code, name);

        // Audit log
        auditLogService.logDelete("LandUse", id, code, oldValues);
    }

    /**
     * Convert entity to audit map.
     */
    private Map<String, Object> toAuditMap(LandUse entity) {
        return AuditLogService.createValuesMap(
                "code", entity.getCode(),
                "nameCs", entity.getNameCs(),
                "nameEn", entity.getNameEn(),
                "descriptionCs", entity.getDescriptionCs(),
                "descriptionEn", entity.getDescriptionEn(),
                "abbreviation", entity.getAbbreviation(),
                "landParcelTypeCode", entity.getLandParcelTypeCode(),
                "validFrom", entity.getValidFrom(),
                "validTo", entity.getValidTo(),
                "sortOrder", entity.getSortOrder()
        );
    }
}

package cz.intelis.legislativeenums.cuzk.unittype;

import cz.intelis.legislativeenums.audit.AuditLogService;
import cz.intelis.legislativeenums.notification.CodelistEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service layer for managing UnitType entities (Typ jednotky).
 *
 * @author Legislative Codelists Team
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UnitTypeService {

    private static final String CODELIST_NAME = "Typy jednotek";
    private static final String CODELIST_CODE = "UNIT_TYPE";

    private final UnitTypeRepository repository;
    private final CodelistEventPublisher eventPublisher;
    private final AuditLogService auditLogService;

    /**
     * Retrieves all unit types ordered by sort order and code.
     *
     * @return list of all unit types as DTOs
     */
    public List<UnitTypeDTO> findAll() {
        return repository.findAllOrdered().stream()
            .map(UnitTypeDTO::fromEntity)
            .collect(Collectors.toList());
    }

    /**
     * Retrieves all currently valid unit types.
     * A record is valid if current date is within validFrom-validTo range.
     *
     * @return list of currently valid unit types as DTOs
     */
    public List<UnitTypeDTO> findAllCurrentlyValid() {
        return repository.findAllCurrentlyValid().stream()
            .map(UnitTypeDTO::fromEntity)
            .collect(Collectors.toList());
    }

    /**
     * Finds a unit type by its ID.
     *
     * @param id the unit type ID
     * @return the unit type as DTO
     * @throws RuntimeException if unit type not found
     */
    public UnitTypeDTO findById(Long id) {
        UnitType entity = repository.findById(id)
            .orElseThrow(() -> new RuntimeException("Unit type not found with id: " + id));
        return UnitTypeDTO.fromEntity(entity);
    }

    /**
     * Finds a unit type by its unique code.
     *
     * @param code the unit type code
     * @return the unit type as DTO
     * @throws RuntimeException if unit type not found
     */
    public UnitTypeDTO findByCode(String code) {
        UnitType entity = repository.findByCode(code)
            .orElseThrow(() -> new RuntimeException("Unit type not found with code: " + code));
        return UnitTypeDTO.fromEntity(entity);
    }

    /**
     * Creates a new unit type.
     *
     * @param dto the unit type data
     * @return the created unit type as DTO
     * @throws RuntimeException if unit type with same code already exists
     */
    @Transactional
    public UnitTypeDTO create(UnitTypeDTO dto) {
        if (repository.existsByCode(dto.getCode())) {
            throw new RuntimeException("Unit type with code " + dto.getCode() + " already exists");
        }

        UnitType entity = new UnitType();
        entity.setCode(dto.getCode());
        entity.setNameCs(dto.getNameCs());
        entity.setNameEn(dto.getNameEn());
        entity.setDescriptionCs(dto.getDescriptionCs());
        entity.setDescriptionEn(dto.getDescriptionEn());
        entity.setAbbreviation(dto.getAbbreviation());
        entity.setCivilCode(dto.getCivilCode());
        entity.setValidFrom(dto.getValidFrom());
        entity.setValidTo(dto.getValidTo());
        entity.setSortOrder(dto.getSortOrder());

        entity = repository.save(entity);

        eventPublisher.publishInsert(CODELIST_NAME, CODELIST_CODE,
                entity.getId(), entity.getCode(), entity.getNameCs());

        // Audit log
        auditLogService.logCreate("UnitType", entity.getId(), entity.getCode(),
                toAuditMap(entity));

        return UnitTypeDTO.fromEntity(entity);
    }

    /**
     * Updates an existing unit type.
     *
     * @param id the ID of the unit type to update
     * @param dto the new unit type data
     * @return the updated unit type as DTO
     * @throws RuntimeException if unit type not found or new code already exists
     */
    @Transactional
    public UnitTypeDTO update(Long id, UnitTypeDTO dto) {
        UnitType existing = repository.findById(id)
            .orElseThrow(() -> new RuntimeException("Unit type not found with id: " + id));

        if (!existing.getCode().equals(dto.getCode()) && repository.existsByCode(dto.getCode())) {
            throw new RuntimeException("Unit type with code " + dto.getCode() + " already exists");
        }

        // Capture old values for audit
        Map<String, Object> oldValues = toAuditMap(existing);

        existing.setCode(dto.getCode());
        existing.setNameCs(dto.getNameCs());
        existing.setNameEn(dto.getNameEn());
        existing.setDescriptionCs(dto.getDescriptionCs());
        existing.setDescriptionEn(dto.getDescriptionEn());
        existing.setAbbreviation(dto.getAbbreviation());
        existing.setCivilCode(dto.getCivilCode());
        existing.setValidFrom(dto.getValidFrom());
        existing.setValidTo(dto.getValidTo());
        existing.setSortOrder(dto.getSortOrder());

        existing = repository.save(existing);

        eventPublisher.publishUpdate(CODELIST_NAME, CODELIST_CODE,
                existing.getId(), existing.getCode(), existing.getNameCs());

        // Audit log
        auditLogService.logUpdate("UnitType", existing.getId(), existing.getCode(),
                oldValues, toAuditMap(existing));

        return UnitTypeDTO.fromEntity(existing);
    }

    /**
     * Deletes a unit type by its ID.
     *
     * @param id the ID of the unit type to delete
     * @throws RuntimeException if unit type not found
     */
    @Transactional
    public void delete(Long id) {
        UnitType existing = repository.findById(id)
            .orElseThrow(() -> new RuntimeException("Unit type not found with id: " + id));

        String code = existing.getCode();
        String name = existing.getNameCs();

        // Capture values for audit before deletion
        Map<String, Object> oldValues = toAuditMap(existing);

        repository.deleteById(id);

        eventPublisher.publishDelete(CODELIST_NAME, CODELIST_CODE, id, code, name);

        // Audit log
        auditLogService.logDelete("UnitType", id, code, oldValues);
    }

    /**
     * Convert entity to audit map.
     */
    private Map<String, Object> toAuditMap(UnitType entity) {
        return AuditLogService.createValuesMap(
                "code", entity.getCode(),
                "nameCs", entity.getNameCs(),
                "nameEn", entity.getNameEn(),
                "descriptionCs", entity.getDescriptionCs(),
                "descriptionEn", entity.getDescriptionEn(),
                "abbreviation", entity.getAbbreviation(),
                "civilCode", entity.getCivilCode(),
                "validFrom", entity.getValidFrom(),
                "validTo", entity.getValidTo(),
                "sortOrder", entity.getSortOrder()
        );
    }
}

package cz.intelis.legislativeenums.cuzk.unituse;

import cz.intelis.legislativeenums.audit.AuditLogService;
import cz.intelis.legislativeenums.notification.CodelistEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service layer for managing UnitUse entities (Způsob využití jednotky).
 *
 * @author Legislative Codelists Team
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UnitUseService {

    private static final String CODELIST_NAME = "Způsoby využití jednotky";
    private static final String CODELIST_CODE = "UNIT_USE";

    private final UnitUseRepository repository;
    private final CodelistEventPublisher eventPublisher;
    private final AuditLogService auditLogService;

    /**
     * Retrieves all unit uses ordered by sort order and code.
     *
     * @return list of all unit uses as DTOs
     */
    public List<UnitUseDTO> findAll() {
        return repository.findAllOrdered().stream()
            .map(UnitUseDTO::fromEntity)
            .collect(Collectors.toList());
    }

    /**
     * Retrieves all currently valid unit uses.
     * A record is valid if current date is within validFrom-validTo range.
     *
     * @return list of currently valid unit uses as DTOs
     */
    public List<UnitUseDTO> findAllCurrentlyValid() {
        return repository.findAllCurrentlyValid().stream()
            .map(UnitUseDTO::fromEntity)
            .collect(Collectors.toList());
    }

    /**
     * Finds a unit use by its ID.
     *
     * @param id the unit use ID
     * @return the unit use as DTO
     * @throws RuntimeException if unit use not found
     */
    public UnitUseDTO findById(Long id) {
        UnitUse entity = repository.findById(id)
            .orElseThrow(() -> new RuntimeException("Unit use not found with id: " + id));
        return UnitUseDTO.fromEntity(entity);
    }

    /**
     * Finds a unit use by its unique code.
     *
     * @param code the unit use code
     * @return the unit use as DTO
     * @throws RuntimeException if unit use not found
     */
    public UnitUseDTO findByCode(String code) {
        UnitUse entity = repository.findByCode(code)
            .orElseThrow(() -> new RuntimeException("Unit use not found with code: " + code));
        return UnitUseDTO.fromEntity(entity);
    }

    /**
     * Creates a new unit use.
     *
     * @param dto the unit use data
     * @return the created unit use as DTO
     * @throws RuntimeException if unit use with same code already exists
     */
    @Transactional
    public UnitUseDTO create(UnitUseDTO dto) {
        if (repository.existsByCode(dto.getCode())) {
            throw new RuntimeException("Unit use with code " + dto.getCode() + " already exists");
        }

        UnitUse entity = new UnitUse();
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
        auditLogService.logCreate("UnitUse", entity.getId(), entity.getCode(),
                toAuditMap(entity));

        return UnitUseDTO.fromEntity(entity);
    }

    /**
     * Updates an existing unit use.
     *
     * @param id the ID of the unit use to update
     * @param dto the new unit use data
     * @return the updated unit use as DTO
     * @throws RuntimeException if unit use not found or new code already exists
     */
    @Transactional
    public UnitUseDTO update(Long id, UnitUseDTO dto) {
        UnitUse existing = repository.findById(id)
            .orElseThrow(() -> new RuntimeException("Unit use not found with id: " + id));

        if (!existing.getCode().equals(dto.getCode()) && repository.existsByCode(dto.getCode())) {
            throw new RuntimeException("Unit use with code " + dto.getCode() + " already exists");
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
        auditLogService.logUpdate("UnitUse", existing.getId(), existing.getCode(),
                oldValues, toAuditMap(existing));

        return UnitUseDTO.fromEntity(existing);
    }

    /**
     * Deletes a unit use by its ID.
     *
     * @param id the ID of the unit use to delete
     * @throws RuntimeException if unit use not found
     */
    @Transactional
    public void delete(Long id) {
        UnitUse existing = repository.findById(id)
            .orElseThrow(() -> new RuntimeException("Unit use not found with id: " + id));

        String code = existing.getCode();
        String name = existing.getNameCs();

        // Capture values for audit before deletion
        Map<String, Object> oldValues = toAuditMap(existing);

        repository.deleteById(id);

        eventPublisher.publishDelete(CODELIST_NAME, CODELIST_CODE, id, code, name);

        // Audit log
        auditLogService.logDelete("UnitUse", id, code, oldValues);
    }

    /**
     * Convert entity to audit map.
     */
    private Map<String, Object> toAuditMap(UnitUse entity) {
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

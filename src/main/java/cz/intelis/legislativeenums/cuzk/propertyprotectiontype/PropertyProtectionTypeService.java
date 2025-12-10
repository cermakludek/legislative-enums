package cz.intelis.legislativeenums.cuzk.propertyprotectiontype;

import cz.intelis.legislativeenums.audit.AuditLogService;
import cz.intelis.legislativeenums.notification.CodelistEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service layer for managing PropertyProtectionType entities (Typ ochrany nemovitosti).
 *
 * @author Legislative Codelists Team
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PropertyProtectionTypeService {

    private static final String CODELIST_NAME = "Typy ochrany nemovitosti";
    private static final String CODELIST_CODE = "PROPERTY_PROTECTION_TYPE";

    private final PropertyProtectionTypeRepository repository;
    private final CodelistEventPublisher eventPublisher;
    private final AuditLogService auditLogService;

    /**
     * Retrieves all property protection types ordered by sort order and code.
     *
     * @return list of all property protection types as DTOs
     */
    public List<PropertyProtectionTypeDTO> findAll() {
        return repository.findAllOrdered().stream()
            .map(PropertyProtectionTypeDTO::fromEntity)
            .collect(Collectors.toList());
    }

    /**
     * Retrieves all currently valid property protection types.
     * A record is valid if current date is within validFrom-validTo range.
     *
     * @return list of currently valid property protection types as DTOs
     */
    public List<PropertyProtectionTypeDTO> findAllCurrentlyValid() {
        return repository.findAllCurrentlyValid().stream()
            .map(PropertyProtectionTypeDTO::fromEntity)
            .collect(Collectors.toList());
    }

    /**
     * Finds a property protection type by its ID.
     *
     * @param id the property protection type ID
     * @return the property protection type as DTO
     * @throws RuntimeException if property protection type not found
     */
    public PropertyProtectionTypeDTO findById(Long id) {
        PropertyProtectionType entity = repository.findById(id)
            .orElseThrow(() -> new RuntimeException("Property protection type not found with id: " + id));
        return PropertyProtectionTypeDTO.fromEntity(entity);
    }

    /**
     * Finds a property protection type by its code.
     *
     * @param code the property protection type code
     * @return the property protection type as DTO
     * @throws RuntimeException if property protection type not found
     */
    public PropertyProtectionTypeDTO findByCode(String code) {
        PropertyProtectionType entity = repository.findByCode(code)
            .orElseThrow(() -> new RuntimeException("Property protection type not found with code: " + code));
        return PropertyProtectionTypeDTO.fromEntity(entity);
    }

    /**
     * Creates a new property protection type.
     *
     * @param dto the property protection type data
     * @return the created property protection type as DTO
     * @throws RuntimeException if property protection type with same code already exists
     */
    @Transactional
    public PropertyProtectionTypeDTO create(PropertyProtectionTypeDTO dto) {
        if (repository.existsByCode(dto.getCode())) {
            throw new RuntimeException("Property protection type with code " + dto.getCode() + " already exists");
        }

        PropertyProtectionType entity = new PropertyProtectionType();
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

        auditLogService.logCreate("PropertyProtectionType", entity.getId(), entity.getCode(), toAuditMap(entity));

        return PropertyProtectionTypeDTO.fromEntity(entity);
    }

    /**
     * Updates an existing property protection type.
     *
     * @param id the property protection type ID
     * @param dto the updated property protection type data
     * @return the updated property protection type as DTO
     * @throws RuntimeException if property protection type not found or code already exists
     */
    @Transactional
    public PropertyProtectionTypeDTO update(Long id, PropertyProtectionTypeDTO dto) {
        PropertyProtectionType existing = repository.findById(id)
            .orElseThrow(() -> new RuntimeException("Property protection type not found with id: " + id));

        if (!existing.getCode().equals(dto.getCode()) && repository.existsByCode(dto.getCode())) {
            throw new RuntimeException("Property protection type with code " + dto.getCode() + " already exists");
        }

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

        auditLogService.logUpdate("PropertyProtectionType", existing.getId(), existing.getCode(), oldValues, toAuditMap(existing));

        return PropertyProtectionTypeDTO.fromEntity(existing);
    }

    /**
     * Deletes a property protection type by its ID.
     *
     * @param id the property protection type ID
     * @throws RuntimeException if property protection type not found
     */
    @Transactional
    public void delete(Long id) {
        PropertyProtectionType existing = repository.findById(id)
            .orElseThrow(() -> new RuntimeException("Property protection type not found with id: " + id));

        String code = existing.getCode();
        String name = existing.getNameCs();
        Map<String, Object> oldValues = toAuditMap(existing);

        repository.deleteById(id);

        eventPublisher.publishDelete(CODELIST_NAME, CODELIST_CODE, id, code, name);

        auditLogService.logDelete("PropertyProtectionType", id, code, oldValues);
    }

    private Map<String, Object> toAuditMap(PropertyProtectionType entity) {
        Map<String, Object> map = new HashMap<>();
        map.put("code", entity.getCode());
        map.put("nameCs", entity.getNameCs() != null ? entity.getNameCs() : "");
        map.put("nameEn", entity.getNameEn() != null ? entity.getNameEn() : "");
        map.put("descriptionCs", entity.getDescriptionCs() != null ? entity.getDescriptionCs() : "");
        map.put("descriptionEn", entity.getDescriptionEn() != null ? entity.getDescriptionEn() : "");
        map.put("validFrom", entity.getValidFrom() != null ? entity.getValidFrom().toString() : "");
        map.put("validTo", entity.getValidTo() != null ? entity.getValidTo().toString() : "");
        map.put("sortOrder", entity.getSortOrder() != null ? entity.getSortOrder().toString() : "");
        return map;
    }
}

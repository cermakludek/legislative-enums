package cz.intelis.legislativeenums.cuzk.propertyprotection;

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
 * Service layer for managing PropertyProtection entities (Způsob ochrany nemovitosti).
 *
 * @author Legislative Codelists Team
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PropertyProtectionService {

    private static final String CODELIST_NAME = "Způsoby ochrany nemovitosti";
    private static final String CODELIST_CODE = "PROPERTY_PROTECTION";

    private final PropertyProtectionRepository repository;
    private final CodelistEventPublisher eventPublisher;
    private final AuditLogService auditLogService;

    /**
     * Retrieves all property protections ordered by sort order and code.
     *
     * @return list of all property protections as DTOs
     */
    public List<PropertyProtectionDTO> findAll() {
        return repository.findAllOrdered().stream()
            .map(PropertyProtectionDTO::fromEntity)
            .collect(Collectors.toList());
    }

    /**
     * Retrieves all currently valid property protections.
     * A record is valid if current date is within validFrom-validTo range.
     *
     * @return list of currently valid property protections as DTOs
     */
    public List<PropertyProtectionDTO> findAllCurrentlyValid() {
        return repository.findAllCurrentlyValid().stream()
            .map(PropertyProtectionDTO::fromEntity)
            .collect(Collectors.toList());
    }

    /**
     * Finds a property protection by its ID.
     *
     * @param id the property protection ID
     * @return the property protection as DTO
     * @throws RuntimeException if property protection not found
     */
    public PropertyProtectionDTO findById(Long id) {
        PropertyProtection entity = repository.findById(id)
            .orElseThrow(() -> new RuntimeException("Property protection not found with id: " + id));
        return PropertyProtectionDTO.fromEntity(entity);
    }

    /**
     * Finds a property protection by its code.
     *
     * @param code the property protection code
     * @return the property protection as DTO
     * @throws RuntimeException if property protection not found
     */
    public PropertyProtectionDTO findByCode(String code) {
        PropertyProtection entity = repository.findByCode(code)
            .orElseThrow(() -> new RuntimeException("Property protection not found with code: " + code));
        return PropertyProtectionDTO.fromEntity(entity);
    }

    /**
     * Finds all property protections by protection type code.
     *
     * @param protectionTypeCode the protection type code to filter by
     * @return list of property protections matching the protection type code
     */
    public List<PropertyProtectionDTO> findByProtectionTypeCode(String protectionTypeCode) {
        return repository.findByProtectionTypeCode(protectionTypeCode).stream()
            .map(PropertyProtectionDTO::fromEntity)
            .collect(Collectors.toList());
    }

    /**
     * Creates a new property protection.
     *
     * @param dto the property protection data
     * @return the created property protection as DTO
     * @throws RuntimeException if property protection with same code already exists
     */
    @Transactional
    public PropertyProtectionDTO create(PropertyProtectionDTO dto) {
        if (repository.existsByCode(dto.getCode())) {
            throw new RuntimeException("Property protection with code " + dto.getCode() + " already exists");
        }

        PropertyProtection entity = new PropertyProtection();
        entity.setCode(dto.getCode());
        entity.setNameCs(dto.getNameCs());
        entity.setNameEn(dto.getNameEn());
        entity.setDescriptionCs(dto.getDescriptionCs());
        entity.setDescriptionEn(dto.getDescriptionEn());
        entity.setProtectionTypeCode(dto.getProtectionTypeCode());
        entity.setAppliesToLand(dto.getAppliesToLand());
        entity.setAppliesToBuilding(dto.getAppliesToBuilding());
        entity.setAppliesToUnit(dto.getAppliesToUnit());
        entity.setAppliesToBuildingRight(dto.getAppliesToBuildingRight());
        entity.setValidFrom(dto.getValidFrom());
        entity.setValidTo(dto.getValidTo());
        entity.setSortOrder(dto.getSortOrder());

        entity = repository.save(entity);

        eventPublisher.publishInsert(CODELIST_NAME, CODELIST_CODE,
                entity.getId(), entity.getCode(), entity.getNameCs());

        auditLogService.logCreate("PropertyProtection", entity.getId(), entity.getCode(), toAuditMap(entity));

        return PropertyProtectionDTO.fromEntity(entity);
    }

    /**
     * Updates an existing property protection.
     *
     * @param id the property protection ID
     * @param dto the updated property protection data
     * @return the updated property protection as DTO
     * @throws RuntimeException if property protection not found or code already exists
     */
    @Transactional
    public PropertyProtectionDTO update(Long id, PropertyProtectionDTO dto) {
        PropertyProtection existing = repository.findById(id)
            .orElseThrow(() -> new RuntimeException("Property protection not found with id: " + id));

        if (!existing.getCode().equals(dto.getCode()) && repository.existsByCode(dto.getCode())) {
            throw new RuntimeException("Property protection with code " + dto.getCode() + " already exists");
        }

        Map<String, Object> oldValues = toAuditMap(existing);

        existing.setCode(dto.getCode());
        existing.setNameCs(dto.getNameCs());
        existing.setNameEn(dto.getNameEn());
        existing.setDescriptionCs(dto.getDescriptionCs());
        existing.setDescriptionEn(dto.getDescriptionEn());
        existing.setProtectionTypeCode(dto.getProtectionTypeCode());
        existing.setAppliesToLand(dto.getAppliesToLand());
        existing.setAppliesToBuilding(dto.getAppliesToBuilding());
        existing.setAppliesToUnit(dto.getAppliesToUnit());
        existing.setAppliesToBuildingRight(dto.getAppliesToBuildingRight());
        existing.setValidFrom(dto.getValidFrom());
        existing.setValidTo(dto.getValidTo());
        existing.setSortOrder(dto.getSortOrder());

        existing = repository.save(existing);

        eventPublisher.publishUpdate(CODELIST_NAME, CODELIST_CODE,
                existing.getId(), existing.getCode(), existing.getNameCs());

        auditLogService.logUpdate("PropertyProtection", existing.getId(), existing.getCode(), oldValues, toAuditMap(existing));

        return PropertyProtectionDTO.fromEntity(existing);
    }

    /**
     * Deletes a property protection by its ID.
     *
     * @param id the property protection ID
     * @throws RuntimeException if property protection not found
     */
    @Transactional
    public void delete(Long id) {
        PropertyProtection existing = repository.findById(id)
            .orElseThrow(() -> new RuntimeException("Property protection not found with id: " + id));

        String code = existing.getCode();
        String name = existing.getNameCs();
        Map<String, Object> oldValues = toAuditMap(existing);

        repository.deleteById(id);

        eventPublisher.publishDelete(CODELIST_NAME, CODELIST_CODE, id, code, name);

        auditLogService.logDelete("PropertyProtection", id, code, oldValues);
    }

    private Map<String, Object> toAuditMap(PropertyProtection entity) {
        Map<String, Object> map = new HashMap<>();
        map.put("code", entity.getCode());
        map.put("nameCs", entity.getNameCs() != null ? entity.getNameCs() : "");
        map.put("nameEn", entity.getNameEn() != null ? entity.getNameEn() : "");
        map.put("descriptionCs", entity.getDescriptionCs() != null ? entity.getDescriptionCs() : "");
        map.put("descriptionEn", entity.getDescriptionEn() != null ? entity.getDescriptionEn() : "");
        map.put("protectionTypeCode", entity.getProtectionTypeCode() != null ? entity.getProtectionTypeCode() : "");
        map.put("appliesToLand", entity.getAppliesToLand() != null ? entity.getAppliesToLand().toString() : "false");
        map.put("appliesToBuilding", entity.getAppliesToBuilding() != null ? entity.getAppliesToBuilding().toString() : "false");
        map.put("appliesToUnit", entity.getAppliesToUnit() != null ? entity.getAppliesToUnit().toString() : "false");
        map.put("appliesToBuildingRight", entity.getAppliesToBuildingRight() != null ? entity.getAppliesToBuildingRight().toString() : "false");
        map.put("validFrom", entity.getValidFrom() != null ? entity.getValidFrom().toString() : "");
        map.put("validTo", entity.getValidTo() != null ? entity.getValidTo().toString() : "");
        map.put("sortOrder", entity.getSortOrder() != null ? entity.getSortOrder().toString() : "");
        return map;
    }
}

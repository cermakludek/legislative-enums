package cz.intelis.legislativeenums.cuzk.soilecologicalunit;

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
 * Service layer for managing SoilEcologicalUnit entities (Bonitované půdně ekologické jednotky - BPEJ).
 *
 * @author Legislative Codelists Team
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SoilEcologicalUnitService {

    private static final String CODELIST_NAME = "BPEJ";
    private static final String CODELIST_CODE = "SOIL_ECOLOGICAL_UNIT";

    private final SoilEcologicalUnitRepository repository;
    private final CodelistEventPublisher eventPublisher;
    private final AuditLogService auditLogService;

    /**
     * Retrieves all soil ecological units ordered by sort order and code.
     *
     * @return list of all soil ecological units as DTOs
     */
    public List<SoilEcologicalUnitDTO> findAll() {
        return repository.findAllOrdered().stream()
            .map(SoilEcologicalUnitDTO::fromEntity)
            .collect(Collectors.toList());
    }

    /**
     * Retrieves all currently valid soil ecological units.
     * A record is valid if current date is within validFrom-validTo range.
     *
     * @return list of currently valid soil ecological units as DTOs
     */
    public List<SoilEcologicalUnitDTO> findAllCurrentlyValid() {
        return repository.findAllCurrentlyValid().stream()
            .map(SoilEcologicalUnitDTO::fromEntity)
            .collect(Collectors.toList());
    }

    /**
     * Finds a soil ecological unit by its ID.
     *
     * @param id the soil ecological unit ID
     * @return the soil ecological unit as DTO
     * @throws RuntimeException if soil ecological unit not found
     */
    public SoilEcologicalUnitDTO findById(Long id) {
        SoilEcologicalUnit entity = repository.findById(id)
            .orElseThrow(() -> new RuntimeException("Soil ecological unit not found with id: " + id));
        return SoilEcologicalUnitDTO.fromEntity(entity);
    }

    /**
     * Finds a soil ecological unit by its code.
     *
     * @param code the soil ecological unit code
     * @return the soil ecological unit as DTO
     * @throws RuntimeException if soil ecological unit not found
     */
    public SoilEcologicalUnitDTO findByCode(String code) {
        SoilEcologicalUnit entity = repository.findByCode(code)
            .orElseThrow(() -> new RuntimeException("Soil ecological unit not found with code: " + code));
        return SoilEcologicalUnitDTO.fromEntity(entity);
    }

    /**
     * Creates a new soil ecological unit.
     *
     * @param dto the soil ecological unit data
     * @return the created soil ecological unit as DTO
     * @throws RuntimeException if soil ecological unit with same code already exists
     */
    @Transactional
    public SoilEcologicalUnitDTO create(SoilEcologicalUnitDTO dto) {
        if (repository.existsByCode(dto.getCode())) {
            throw new RuntimeException("Soil ecological unit with code " + dto.getCode() + " already exists");
        }

        SoilEcologicalUnit entity = new SoilEcologicalUnit();
        entity.setCode(dto.getCode());
        entity.setNameCs(dto.getNameCs());
        entity.setNameEn(dto.getNameEn());
        entity.setDescriptionCs(dto.getDescriptionCs());
        entity.setDescriptionEn(dto.getDescriptionEn());
        entity.setPrice(dto.getPrice());
        entity.setDetailedDescription(dto.getDetailedDescription());
        entity.setValidFrom(dto.getValidFrom());
        entity.setValidTo(dto.getValidTo());
        entity.setSortOrder(dto.getSortOrder());

        entity = repository.save(entity);

        eventPublisher.publishInsert(CODELIST_NAME, CODELIST_CODE,
                entity.getId(), entity.getCode(), entity.getNameCs());

        auditLogService.logCreate("SoilEcologicalUnit", entity.getId(), entity.getCode(), toAuditMap(entity));

        return SoilEcologicalUnitDTO.fromEntity(entity);
    }

    /**
     * Updates an existing soil ecological unit.
     *
     * @param id the soil ecological unit ID
     * @param dto the updated soil ecological unit data
     * @return the updated soil ecological unit as DTO
     * @throws RuntimeException if soil ecological unit not found or code already exists
     */
    @Transactional
    public SoilEcologicalUnitDTO update(Long id, SoilEcologicalUnitDTO dto) {
        SoilEcologicalUnit existing = repository.findById(id)
            .orElseThrow(() -> new RuntimeException("Soil ecological unit not found with id: " + id));

        if (!existing.getCode().equals(dto.getCode()) && repository.existsByCode(dto.getCode())) {
            throw new RuntimeException("Soil ecological unit with code " + dto.getCode() + " already exists");
        }

        Map<String, Object> oldValues = toAuditMap(existing);

        existing.setCode(dto.getCode());
        existing.setNameCs(dto.getNameCs());
        existing.setNameEn(dto.getNameEn());
        existing.setDescriptionCs(dto.getDescriptionCs());
        existing.setDescriptionEn(dto.getDescriptionEn());
        existing.setPrice(dto.getPrice());
        existing.setDetailedDescription(dto.getDetailedDescription());
        existing.setValidFrom(dto.getValidFrom());
        existing.setValidTo(dto.getValidTo());
        existing.setSortOrder(dto.getSortOrder());

        existing = repository.save(existing);

        eventPublisher.publishUpdate(CODELIST_NAME, CODELIST_CODE,
                existing.getId(), existing.getCode(), existing.getNameCs());

        auditLogService.logUpdate("SoilEcologicalUnit", existing.getId(), existing.getCode(), oldValues, toAuditMap(existing));

        return SoilEcologicalUnitDTO.fromEntity(existing);
    }

    /**
     * Deletes a soil ecological unit by its ID.
     *
     * @param id the soil ecological unit ID
     * @throws RuntimeException if soil ecological unit not found
     */
    @Transactional
    public void delete(Long id) {
        SoilEcologicalUnit existing = repository.findById(id)
            .orElseThrow(() -> new RuntimeException("Soil ecological unit not found with id: " + id));

        String code = existing.getCode();
        String name = existing.getNameCs();
        Map<String, Object> oldValues = toAuditMap(existing);

        repository.deleteById(id);

        eventPublisher.publishDelete(CODELIST_NAME, CODELIST_CODE, id, code, name);

        auditLogService.logDelete("SoilEcologicalUnit", id, code, oldValues);
    }

    private Map<String, Object> toAuditMap(SoilEcologicalUnit entity) {
        Map<String, Object> map = new HashMap<>();
        map.put("code", entity.getCode());
        map.put("nameCs", entity.getNameCs() != null ? entity.getNameCs() : "");
        map.put("nameEn", entity.getNameEn() != null ? entity.getNameEn() : "");
        map.put("descriptionCs", entity.getDescriptionCs() != null ? entity.getDescriptionCs() : "");
        map.put("descriptionEn", entity.getDescriptionEn() != null ? entity.getDescriptionEn() : "");
        map.put("price", entity.getPrice() != null ? entity.getPrice().toString() : "");
        map.put("detailedDescription", entity.getDetailedDescription() != null ? entity.getDetailedDescription() : "");
        map.put("validFrom", entity.getValidFrom() != null ? entity.getValidFrom().toString() : "");
        map.put("validTo", entity.getValidTo() != null ? entity.getValidTo().toString() : "");
        map.put("sortOrder", entity.getSortOrder() != null ? entity.getSortOrder().toString() : "");
        return map;
    }
}

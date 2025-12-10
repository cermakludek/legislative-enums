package cz.intelis.legislativeenums.cuzk.areadetermination;

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
 * Service layer for managing AreaDetermination entities (Způsob určení výměry).
 *
 * @author Legislative Codelists Team
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AreaDeterminationService {

    private static final String CODELIST_NAME = "Způsoby určení výměry";
    private static final String CODELIST_CODE = "AREA_DETERMINATION";

    private final AreaDeterminationRepository repository;
    private final CodelistEventPublisher eventPublisher;
    private final AuditLogService auditLogService;

    /**
     * Retrieves all area determinations ordered by sort order and code.
     *
     * @return list of all area determinations as DTOs
     */
    public List<AreaDeterminationDTO> findAll() {
        return repository.findAllOrdered().stream()
            .map(AreaDeterminationDTO::fromEntity)
            .collect(Collectors.toList());
    }

    /**
     * Retrieves all currently valid area determinations.
     * A record is valid if current date is within validFrom-validTo range.
     *
     * @return list of currently valid area determinations as DTOs
     */
    public List<AreaDeterminationDTO> findAllCurrentlyValid() {
        return repository.findAllCurrentlyValid().stream()
            .map(AreaDeterminationDTO::fromEntity)
            .collect(Collectors.toList());
    }

    /**
     * Finds an area determination by its ID.
     *
     * @param id the area determination ID
     * @return the area determination as DTO
     * @throws RuntimeException if area determination not found
     */
    public AreaDeterminationDTO findById(Long id) {
        AreaDetermination entity = repository.findById(id)
            .orElseThrow(() -> new RuntimeException("Area determination not found with id: " + id));
        return AreaDeterminationDTO.fromEntity(entity);
    }

    /**
     * Finds an area determination by its unique code.
     *
     * @param code the area determination code
     * @return the area determination as DTO
     * @throws RuntimeException if area determination not found
     */
    public AreaDeterminationDTO findByCode(String code) {
        AreaDetermination entity = repository.findByCode(code)
            .orElseThrow(() -> new RuntimeException("Area determination not found with code: " + code));
        return AreaDeterminationDTO.fromEntity(entity);
    }

    /**
     * Creates a new area determination.
     *
     * @param dto the area determination data
     * @return the created area determination as DTO
     * @throws RuntimeException if area determination with same code already exists
     */
    @Transactional
    public AreaDeterminationDTO create(AreaDeterminationDTO dto) {
        if (repository.existsByCode(dto.getCode())) {
            throw new RuntimeException("Area determination with code " + dto.getCode() + " already exists");
        }

        AreaDetermination entity = new AreaDetermination();
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

        auditLogService.logCreate("AreaDetermination", entity.getId(), entity.getCode(), toAuditMap(entity));

        return AreaDeterminationDTO.fromEntity(entity);
    }

    /**
     * Updates an existing area determination.
     *
     * @param id the ID of the area determination to update
     * @param dto the new area determination data
     * @return the updated area determination as DTO
     * @throws RuntimeException if area determination not found or new code already exists
     */
    @Transactional
    public AreaDeterminationDTO update(Long id, AreaDeterminationDTO dto) {
        AreaDetermination existing = repository.findById(id)
            .orElseThrow(() -> new RuntimeException("Area determination not found with id: " + id));

        if (!existing.getCode().equals(dto.getCode()) && repository.existsByCode(dto.getCode())) {
            throw new RuntimeException("Area determination with code " + dto.getCode() + " already exists");
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

        auditLogService.logUpdate("AreaDetermination", existing.getId(), existing.getCode(), oldValues, toAuditMap(existing));

        return AreaDeterminationDTO.fromEntity(existing);
    }

    /**
     * Deletes an area determination by its ID.
     *
     * @param id the ID of the area determination to delete
     * @throws RuntimeException if area determination not found
     */
    @Transactional
    public void delete(Long id) {
        AreaDetermination existing = repository.findById(id)
            .orElseThrow(() -> new RuntimeException("Area determination not found with id: " + id));

        String code = existing.getCode();
        String name = existing.getNameCs();
        Map<String, Object> oldValues = toAuditMap(existing);

        repository.deleteById(id);

        eventPublisher.publishDelete(CODELIST_NAME, CODELIST_CODE, id, code, name);

        auditLogService.logDelete("AreaDetermination", id, code, oldValues);
    }

    private Map<String, Object> toAuditMap(AreaDetermination entity) {
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

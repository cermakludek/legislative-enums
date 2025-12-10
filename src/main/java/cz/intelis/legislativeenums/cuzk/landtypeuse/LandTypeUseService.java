package cz.intelis.legislativeenums.cuzk.landtypeuse;

import cz.intelis.legislativeenums.audit.AuditLogService;
import cz.intelis.legislativeenums.notification.CodelistEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service layer for managing LandTypeUse entities (Vazba druh pozemku a využití).
 *
 * @author Legislative Codelists Team
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LandTypeUseService {

    private static final String CODELIST_NAME = "Vazby druh pozemku - využití";
    private static final String CODELIST_CODE = "LAND_TYPE_USE";

    private final LandTypeUseRepository repository;
    private final CodelistEventPublisher eventPublisher;
    private final AuditLogService auditLogService;

    /**
     * Retrieves all land type-use relations ordered by land type code and land use code.
     *
     * @return list of all land type-use relations as DTOs
     */
    public List<LandTypeUseDTO> findAll() {
        return repository.findAllOrdered().stream()
            .map(LandTypeUseDTO::fromEntity)
            .collect(Collectors.toList());
    }

    /**
     * Retrieves all currently valid land type-use relations.
     * A record is valid if current date is within validFrom-validTo range.
     *
     * @return list of currently valid land type-use relations as DTOs
     */
    public List<LandTypeUseDTO> findAllCurrentlyValid() {
        return repository.findAllCurrentlyValid().stream()
            .map(LandTypeUseDTO::fromEntity)
            .collect(Collectors.toList());
    }

    /**
     * Finds a land type-use relation by its ID.
     *
     * @param id the land type-use relation ID
     * @return the land type-use relation as DTO
     * @throws RuntimeException if land type-use relation not found
     */
    public LandTypeUseDTO findById(Long id) {
        LandTypeUse entity = repository.findById(id)
            .orElseThrow(() -> new RuntimeException("Land type use relation not found with id: " + id));
        return LandTypeUseDTO.fromEntity(entity);
    }

    /**
     * Finds all land type-use relations for a specific land type.
     *
     * @param landTypeCode the land type code to search for
     * @return list of matching land type-use relations as DTOs
     */
    public List<LandTypeUseDTO> findByLandTypeCode(String landTypeCode) {
        return repository.findByLandTypeCode(landTypeCode).stream()
            .map(LandTypeUseDTO::fromEntity)
            .collect(Collectors.toList());
    }

    /**
     * Finds all land type-use relations for a specific land use.
     *
     * @param landUseCode the land use code to search for
     * @return list of matching land type-use relations as DTOs
     */
    public List<LandTypeUseDTO> findByLandUseCode(String landUseCode) {
        return repository.findByLandUseCode(landUseCode).stream()
            .map(LandTypeUseDTO::fromEntity)
            .collect(Collectors.toList());
    }

    /**
     * Creates a new land type-use relation.
     *
     * @param dto the land type-use relation data
     * @return the created land type-use relation as DTO
     * @throws RuntimeException if relation for given land type and land use already exists
     */
    @Transactional
    public LandTypeUseDTO create(LandTypeUseDTO dto) {
        if (repository.existsByLandTypeCodeAndLandUseCode(dto.getLandTypeCode(), dto.getLandUseCode())) {
            throw new RuntimeException("Land type use relation already exists for land type " + dto.getLandTypeCode() + " and land use " + dto.getLandUseCode());
        }

        LandTypeUse entity = new LandTypeUse();
        entity.setLandTypeCode(dto.getLandTypeCode());
        entity.setLandUseCode(dto.getLandUseCode());
        entity.setValidFrom(dto.getValidFrom());
        entity.setValidTo(dto.getValidTo());

        entity = repository.save(entity);

        String code = entity.getLandTypeCode() + "-" + entity.getLandUseCode();
        eventPublisher.publishInsert(CODELIST_NAME, CODELIST_CODE,
                entity.getId(), code, code);

        auditLogService.logCreate("LandTypeUse", entity.getId(), code, toAuditMap(entity));

        return LandTypeUseDTO.fromEntity(entity);
    }

    /**
     * Updates an existing land type-use relation.
     *
     * @param id the ID of the land type-use relation to update
     * @param dto the new land type-use relation data
     * @return the updated land type-use relation as DTO
     * @throws RuntimeException if relation not found or new combination already exists
     */
    @Transactional
    public LandTypeUseDTO update(Long id, LandTypeUseDTO dto) {
        LandTypeUse existing = repository.findById(id)
            .orElseThrow(() -> new RuntimeException("Land type use relation not found with id: " + id));

        if ((!existing.getLandTypeCode().equals(dto.getLandTypeCode()) || !existing.getLandUseCode().equals(dto.getLandUseCode()))
                && repository.existsByLandTypeCodeAndLandUseCode(dto.getLandTypeCode(), dto.getLandUseCode())) {
            throw new RuntimeException("Land type use relation already exists for land type " + dto.getLandTypeCode() + " and land use " + dto.getLandUseCode());
        }

        Map<String, Object> oldValues = toAuditMap(existing);

        existing.setLandTypeCode(dto.getLandTypeCode());
        existing.setLandUseCode(dto.getLandUseCode());
        existing.setValidFrom(dto.getValidFrom());
        existing.setValidTo(dto.getValidTo());

        existing = repository.save(existing);

        String code = existing.getLandTypeCode() + "-" + existing.getLandUseCode();
        eventPublisher.publishUpdate(CODELIST_NAME, CODELIST_CODE,
                existing.getId(), code, code);

        auditLogService.logUpdate("LandTypeUse", existing.getId(), code, oldValues, toAuditMap(existing));

        return LandTypeUseDTO.fromEntity(existing);
    }

    /**
     * Deletes a land type-use relation by its ID.
     *
     * @param id the ID of the land type-use relation to delete
     * @throws RuntimeException if land type-use relation not found
     */
    @Transactional
    public void delete(Long id) {
        LandTypeUse existing = repository.findById(id)
            .orElseThrow(() -> new RuntimeException("Land type use relation not found with id: " + id));

        String code = existing.getLandTypeCode() + "-" + existing.getLandUseCode();
        Map<String, Object> oldValues = toAuditMap(existing);

        repository.deleteById(id);

        eventPublisher.publishDelete(CODELIST_NAME, CODELIST_CODE, id, code, code);

        auditLogService.logDelete("LandTypeUse", id, code, oldValues);
    }

    private Map<String, Object> toAuditMap(LandTypeUse entity) {
        return Map.of(
            "landTypeCode", entity.getLandTypeCode(),
            "landUseCode", entity.getLandUseCode(),
            "validFrom", entity.getValidFrom() != null ? entity.getValidFrom().toString() : "",
            "validTo", entity.getValidTo() != null ? entity.getValidTo().toString() : ""
        );
    }
}

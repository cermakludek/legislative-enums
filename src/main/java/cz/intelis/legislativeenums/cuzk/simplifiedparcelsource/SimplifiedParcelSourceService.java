package cz.intelis.legislativeenums.cuzk.simplifiedparcelsource;

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
 * Service layer for managing SimplifiedParcelSource entities (Zdroje parcel zjednodušené evidence).
 *
 * @author Legislative Codelists Team
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SimplifiedParcelSourceService {

    private static final String CODELIST_NAME = "Zdroje parcel zjednodušené evidence";
    private static final String CODELIST_CODE = "SIMPLIFIED_PARCEL_SOURCE";

    private final SimplifiedParcelSourceRepository repository;
    private final CodelistEventPublisher eventPublisher;
    private final AuditLogService auditLogService;

    /**
     * Retrieves all simplified parcel sources ordered by sort order and code.
     *
     * @return list of all simplified parcel sources as DTOs
     */
    public List<SimplifiedParcelSourceDTO> findAll() {
        return repository.findAllOrdered().stream()
            .map(SimplifiedParcelSourceDTO::fromEntity)
            .collect(Collectors.toList());
    }

    /**
     * Retrieves all currently valid simplified parcel sources.
     * A record is valid if current date is within validFrom-validTo range.
     *
     * @return list of currently valid simplified parcel sources as DTOs
     */
    public List<SimplifiedParcelSourceDTO> findAllCurrentlyValid() {
        return repository.findAllCurrentlyValid().stream()
            .map(SimplifiedParcelSourceDTO::fromEntity)
            .collect(Collectors.toList());
    }

    /**
     * Finds a simplified parcel source by its ID.
     *
     * @param id the simplified parcel source ID
     * @return the simplified parcel source as DTO
     * @throws RuntimeException if simplified parcel source not found
     */
    public SimplifiedParcelSourceDTO findById(Long id) {
        SimplifiedParcelSource entity = repository.findById(id)
            .orElseThrow(() -> new RuntimeException("Simplified parcel source not found with id: " + id));
        return SimplifiedParcelSourceDTO.fromEntity(entity);
    }

    /**
     * Finds a simplified parcel source by its code.
     *
     * @param code the simplified parcel source code
     * @return the simplified parcel source as DTO
     * @throws RuntimeException if simplified parcel source not found
     */
    public SimplifiedParcelSourceDTO findByCode(String code) {
        SimplifiedParcelSource entity = repository.findByCode(code)
            .orElseThrow(() -> new RuntimeException("Simplified parcel source not found with code: " + code));
        return SimplifiedParcelSourceDTO.fromEntity(entity);
    }

    /**
     * Creates a new simplified parcel source.
     *
     * @param dto the simplified parcel source data
     * @return the created simplified parcel source as DTO
     * @throws RuntimeException if simplified parcel source with same code already exists
     */
    @Transactional
    public SimplifiedParcelSourceDTO create(SimplifiedParcelSourceDTO dto) {
        if (repository.existsByCode(dto.getCode())) {
            throw new RuntimeException("Simplified parcel source with code " + dto.getCode() + " already exists");
        }

        SimplifiedParcelSource entity = new SimplifiedParcelSource();
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

        auditLogService.logCreate("SimplifiedParcelSource", entity.getId(), entity.getCode(), toAuditMap(entity));

        return SimplifiedParcelSourceDTO.fromEntity(entity);
    }

    /**
     * Updates an existing simplified parcel source.
     *
     * @param id the simplified parcel source ID
     * @param dto the updated simplified parcel source data
     * @return the updated simplified parcel source as DTO
     * @throws RuntimeException if simplified parcel source not found or code already exists
     */
    @Transactional
    public SimplifiedParcelSourceDTO update(Long id, SimplifiedParcelSourceDTO dto) {
        SimplifiedParcelSource existing = repository.findById(id)
            .orElseThrow(() -> new RuntimeException("Simplified parcel source not found with id: " + id));

        if (!existing.getCode().equals(dto.getCode()) && repository.existsByCode(dto.getCode())) {
            throw new RuntimeException("Simplified parcel source with code " + dto.getCode() + " already exists");
        }

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

        auditLogService.logUpdate("SimplifiedParcelSource", existing.getId(), existing.getCode(), oldValues, toAuditMap(existing));

        return SimplifiedParcelSourceDTO.fromEntity(existing);
    }

    /**
     * Deletes a simplified parcel source by its ID.
     *
     * @param id the simplified parcel source ID
     * @throws RuntimeException if simplified parcel source not found
     */
    @Transactional
    public void delete(Long id) {
        SimplifiedParcelSource existing = repository.findById(id)
            .orElseThrow(() -> new RuntimeException("Simplified parcel source not found with id: " + id));

        String code = existing.getCode();
        String name = existing.getNameCs();
        Map<String, Object> oldValues = toAuditMap(existing);

        repository.deleteById(id);

        eventPublisher.publishDelete(CODELIST_NAME, CODELIST_CODE, id, code, name);

        auditLogService.logDelete("SimplifiedParcelSource", id, code, oldValues);
    }

    private Map<String, Object> toAuditMap(SimplifiedParcelSource entity) {
        Map<String, Object> map = new HashMap<>();
        map.put("code", entity.getCode());
        map.put("nameCs", entity.getNameCs() != null ? entity.getNameCs() : "");
        map.put("nameEn", entity.getNameEn() != null ? entity.getNameEn() : "");
        map.put("descriptionCs", entity.getDescriptionCs() != null ? entity.getDescriptionCs() : "");
        map.put("descriptionEn", entity.getDescriptionEn() != null ? entity.getDescriptionEn() : "");
        map.put("abbreviation", entity.getAbbreviation() != null ? entity.getAbbreviation() : "");
        map.put("validFrom", entity.getValidFrom() != null ? entity.getValidFrom().toString() : "");
        map.put("validTo", entity.getValidTo() != null ? entity.getValidTo().toString() : "");
        map.put("sortOrder", entity.getSortOrder() != null ? entity.getSortOrder().toString() : "");
        return map;
    }
}

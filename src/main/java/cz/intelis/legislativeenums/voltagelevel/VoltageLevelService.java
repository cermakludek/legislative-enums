package cz.intelis.legislativeenums.voltagelevel;

import cz.intelis.legislativeenums.audit.AuditLogService;
import cz.intelis.legislativeenums.notification.CodelistEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service layer for managing VoltageLevel entities.
 * Provides business logic for CRUD operations on voltage level classifications
 * (Rozdělení napětí dle velikosti).
 *
 * @author Legislative Codelists Team
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class VoltageLevelService {

    private static final String CODELIST_NAME = "Úrovně napětí";
    private static final String CODELIST_CODE = "VOLTAGE_LEVEL";

    private final VoltageLevelRepository voltageLevelRepository;
    private final CodelistEventPublisher eventPublisher;
    private final AuditLogService auditLogService;

    /**
     * Retrieves all voltage levels ordered by sort order and code.
     *
     * @return list of all voltage levels as DTOs
     */
    public List<VoltageLevelDTO> findAll() {
        return voltageLevelRepository.findAllOrdered().stream()
            .map(VoltageLevelDTO::fromEntity)
            .collect(Collectors.toList());
    }

    /**
     * Retrieves all currently valid voltage levels.
     * A record is valid if current date is within validFrom-validTo range.
     *
     * @return list of currently valid voltage levels as DTOs
     */
    public List<VoltageLevelDTO> findAllCurrentlyValid() {
        return voltageLevelRepository.findAllCurrentlyValid().stream()
            .map(VoltageLevelDTO::fromEntity)
            .collect(Collectors.toList());
    }

    /**
     * Finds a voltage level by its ID.
     *
     * @param id the voltage level ID
     * @return the voltage level as DTO
     * @throws RuntimeException if voltage level not found
     */
    public VoltageLevelDTO findById(Long id) {
        VoltageLevel entity = voltageLevelRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Voltage level not found with id: " + id));
        return VoltageLevelDTO.fromEntity(entity);
    }

    /**
     * Finds a voltage level by its unique code.
     *
     * @param code the voltage level code
     * @return the voltage level as DTO
     * @throws RuntimeException if voltage level not found
     */
    public VoltageLevelDTO findByCode(String code) {
        VoltageLevel entity = voltageLevelRepository.findByCode(code)
            .orElseThrow(() -> new RuntimeException("Voltage level not found with code: " + code));
        return VoltageLevelDTO.fromEntity(entity);
    }

    /**
     * Creates a new voltage level.
     *
     * @param dto the voltage level data
     * @return the created voltage level as DTO
     * @throws RuntimeException if voltage level with same code already exists
     */
    @Transactional
    public VoltageLevelDTO create(VoltageLevelDTO dto) {
        if (voltageLevelRepository.existsByCode(dto.getCode())) {
            throw new RuntimeException("Voltage level with code " + dto.getCode() + " already exists");
        }

        VoltageLevel entity = new VoltageLevel();
        entity.setCode(dto.getCode());
        entity.setNameCs(dto.getNameCs());
        entity.setNameEn(dto.getNameEn());
        entity.setVoltageRangeCs(dto.getVoltageRangeCs());
        entity.setVoltageRangeEn(dto.getVoltageRangeEn());
        entity.setValidFrom(dto.getValidFrom());
        entity.setValidTo(dto.getValidTo());
        entity.setSortOrder(dto.getSortOrder());

        entity = voltageLevelRepository.save(entity);

        eventPublisher.publishInsert(CODELIST_NAME, CODELIST_CODE,
                entity.getId(), entity.getCode(), entity.getNameCs());

        // Audit log
        auditLogService.logCreate("VoltageLevel", entity.getId(), entity.getCode(),
                toAuditMap(entity));

        return VoltageLevelDTO.fromEntity(entity);
    }

    /**
     * Updates an existing voltage level.
     *
     * @param id the ID of the voltage level to update
     * @param dto the new voltage level data
     * @return the updated voltage level as DTO
     * @throws RuntimeException if voltage level not found or new code already exists
     */
    @Transactional
    public VoltageLevelDTO update(Long id, VoltageLevelDTO dto) {
        VoltageLevel existing = voltageLevelRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Voltage level not found with id: " + id));

        if (!existing.getCode().equals(dto.getCode()) &&
            voltageLevelRepository.existsByCode(dto.getCode())) {
            throw new RuntimeException("Voltage level with code " + dto.getCode() + " already exists");
        }

        // Capture old values for audit
        Map<String, Object> oldValues = toAuditMap(existing);

        existing.setCode(dto.getCode());
        existing.setNameCs(dto.getNameCs());
        existing.setNameEn(dto.getNameEn());
        existing.setVoltageRangeCs(dto.getVoltageRangeCs());
        existing.setVoltageRangeEn(dto.getVoltageRangeEn());
        existing.setValidFrom(dto.getValidFrom());
        existing.setValidTo(dto.getValidTo());
        existing.setSortOrder(dto.getSortOrder());

        existing = voltageLevelRepository.save(existing);

        eventPublisher.publishUpdate(CODELIST_NAME, CODELIST_CODE,
                existing.getId(), existing.getCode(), existing.getNameCs());

        // Audit log
        auditLogService.logUpdate("VoltageLevel", existing.getId(), existing.getCode(),
                oldValues, toAuditMap(existing));

        return VoltageLevelDTO.fromEntity(existing);
    }

    /**
     * Deletes a voltage level by its ID.
     *
     * @param id the ID of the voltage level to delete
     * @throws RuntimeException if voltage level not found
     */
    @Transactional
    public void delete(Long id) {
        VoltageLevel existing = voltageLevelRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Voltage level not found with id: " + id));

        String code = existing.getCode();
        String name = existing.getNameCs();

        // Capture values for audit before deletion
        Map<String, Object> oldValues = toAuditMap(existing);

        voltageLevelRepository.deleteById(id);

        eventPublisher.publishDelete(CODELIST_NAME, CODELIST_CODE, id, code, name);

        // Audit log
        auditLogService.logDelete("VoltageLevel", id, code, oldValues);
    }

    /**
     * Convert entity to audit map.
     */
    private Map<String, Object> toAuditMap(VoltageLevel entity) {
        return AuditLogService.createValuesMap(
                "code", entity.getCode(),
                "nameCs", entity.getNameCs(),
                "nameEn", entity.getNameEn(),
                "voltageRangeCs", entity.getVoltageRangeCs(),
                "voltageRangeEn", entity.getVoltageRangeEn(),
                "validFrom", entity.getValidFrom(),
                "validTo", entity.getValidTo(),
                "sortOrder", entity.getSortOrder()
        );
    }
}

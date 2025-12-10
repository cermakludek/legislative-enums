package cz.intelis.legislativeenums.networktype;

import cz.intelis.legislativeenums.audit.AuditLogService;
import cz.intelis.legislativeenums.notification.CodelistEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service layer for managing NetworkType entities.
 * Provides business logic for CRUD operations on network type classifications
 * (Rozdělení sítí z hlediska vedení).
 *
 * @author Legislative Codelists Team
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NetworkTypeService {

    private static final String CODELIST_NAME = "Typy sítí";
    private static final String CODELIST_CODE = "NETWORK_TYPE";

    private final NetworkTypeRepository networkTypeRepository;
    private final CodelistEventPublisher eventPublisher;
    private final AuditLogService auditLogService;

    /**
     * Retrieves all network types ordered by sort order and code.
     *
     * @return list of all network types as DTOs
     */
    public List<NetworkTypeDTO> findAll() {
        return networkTypeRepository.findAllOrdered().stream()
            .map(NetworkTypeDTO::fromEntity)
            .collect(Collectors.toList());
    }

    /**
     * Retrieves all currently valid network types.
     * A record is valid if current date is within validFrom-validTo range.
     *
     * @return list of currently valid network types as DTOs
     */
    public List<NetworkTypeDTO> findAllCurrentlyValid() {
        return networkTypeRepository.findAllCurrentlyValid().stream()
            .map(NetworkTypeDTO::fromEntity)
            .collect(Collectors.toList());
    }

    /**
     * Finds a network type by its ID.
     *
     * @param id the network type ID
     * @return the network type as DTO
     * @throws RuntimeException if network type not found
     */
    public NetworkTypeDTO findById(Long id) {
        NetworkType entity = networkTypeRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Network type not found with id: " + id));
        return NetworkTypeDTO.fromEntity(entity);
    }

    /**
     * Finds a network type by its unique code.
     *
     * @param code the network type code
     * @return the network type as DTO
     * @throws RuntimeException if network type not found
     */
    public NetworkTypeDTO findByCode(String code) {
        NetworkType entity = networkTypeRepository.findByCode(code)
            .orElseThrow(() -> new RuntimeException("Network type not found with code: " + code));
        return NetworkTypeDTO.fromEntity(entity);
    }

    /**
     * Creates a new network type.
     *
     * @param dto the network type data
     * @return the created network type as DTO
     * @throws RuntimeException if network type with same code already exists
     */
    @Transactional
    public NetworkTypeDTO create(NetworkTypeDTO dto) {
        if (networkTypeRepository.existsByCode(dto.getCode())) {
            throw new RuntimeException("Network type with code " + dto.getCode() + " already exists");
        }

        NetworkType entity = new NetworkType();
        entity.setCode(dto.getCode());
        entity.setNameCs(dto.getNameCs());
        entity.setNameEn(dto.getNameEn());
        entity.setDescriptionCs(dto.getDescriptionCs());
        entity.setDescriptionEn(dto.getDescriptionEn());
        entity.setValidFrom(dto.getValidFrom());
        entity.setValidTo(dto.getValidTo());
        entity.setSortOrder(dto.getSortOrder());

        entity = networkTypeRepository.save(entity);

        eventPublisher.publishInsert(CODELIST_NAME, CODELIST_CODE,
                entity.getId(), entity.getCode(), entity.getNameCs());

        // Audit log
        auditLogService.logCreate("NetworkType", entity.getId(), entity.getCode(),
                toAuditMap(entity));

        return NetworkTypeDTO.fromEntity(entity);
    }

    /**
     * Updates an existing network type.
     *
     * @param id the ID of the network type to update
     * @param dto the new network type data
     * @return the updated network type as DTO
     * @throws RuntimeException if network type not found or new code already exists
     */
    @Transactional
    public NetworkTypeDTO update(Long id, NetworkTypeDTO dto) {
        NetworkType existing = networkTypeRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Network type not found with id: " + id));

        if (!existing.getCode().equals(dto.getCode()) &&
            networkTypeRepository.existsByCode(dto.getCode())) {
            throw new RuntimeException("Network type with code " + dto.getCode() + " already exists");
        }

        // Capture old values for audit
        Map<String, Object> oldValues = toAuditMap(existing);

        existing.setCode(dto.getCode());
        existing.setNameCs(dto.getNameCs());
        existing.setNameEn(dto.getNameEn());
        existing.setDescriptionCs(dto.getDescriptionCs());
        existing.setDescriptionEn(dto.getDescriptionEn());
        existing.setValidFrom(dto.getValidFrom());
        existing.setValidTo(dto.getValidTo());
        existing.setSortOrder(dto.getSortOrder());

        existing = networkTypeRepository.save(existing);

        eventPublisher.publishUpdate(CODELIST_NAME, CODELIST_CODE,
                existing.getId(), existing.getCode(), existing.getNameCs());

        // Audit log
        auditLogService.logUpdate("NetworkType", existing.getId(), existing.getCode(),
                oldValues, toAuditMap(existing));

        return NetworkTypeDTO.fromEntity(existing);
    }

    /**
     * Deletes a network type by its ID.
     *
     * @param id the ID of the network type to delete
     * @throws RuntimeException if network type not found
     */
    @Transactional
    public void delete(Long id) {
        NetworkType existing = networkTypeRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Network type not found with id: " + id));

        String code = existing.getCode();
        String name = existing.getNameCs();

        // Capture values for audit before deletion
        Map<String, Object> oldValues = toAuditMap(existing);

        networkTypeRepository.deleteById(id);

        eventPublisher.publishDelete(CODELIST_NAME, CODELIST_CODE, id, code, name);

        // Audit log
        auditLogService.logDelete("NetworkType", id, code, oldValues);
    }

    /**
     * Convert entity to audit map.
     */
    private Map<String, Object> toAuditMap(NetworkType entity) {
        return AuditLogService.createValuesMap(
                "code", entity.getCode(),
                "nameCs", entity.getNameCs(),
                "nameEn", entity.getNameEn(),
                "descriptionCs", entity.getDescriptionCs(),
                "descriptionEn", entity.getDescriptionEn(),
                "validFrom", entity.getValidFrom(),
                "validTo", entity.getValidTo(),
                "sortOrder", entity.getSortOrder()
        );
    }
}

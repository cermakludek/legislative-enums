package cz.intelis.legislativeenums.registry;

import cz.intelis.legislativeenums.flag.Flag;
import cz.intelis.legislativeenums.flag.FlagRepository;
import cz.intelis.legislativeenums.notification.CodelistEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Service layer for managing CodelistRegistry entities.
 * Provides business logic for CRUD operations on codelist registry entries.
 *
 * @author Legislative Codelists Team
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CodelistRegistryService {

    private static final String CODELIST_NAME = "Registr číselníků";
    private static final String CODELIST_CODE = "CODELIST_REGISTRY";

    private final CodelistRegistryRepository codelistRegistryRepository;
    private final FlagRepository flagRepository;
    private final CodelistEventPublisher eventPublisher;

    /**
     * Retrieves all codelist registry entries ordered by sort order and code.
     *
     * @return list of all codelist registry entries as DTOs
     */
    public List<CodelistRegistryDTO> findAll() {
        return codelistRegistryRepository.findAllOrdered().stream()
            .map(CodelistRegistryDTO::fromEntity)
            .collect(Collectors.toList());
    }

    /**
     * Retrieves all currently valid codelist registry entries with their flags.
     * A record is valid if current date is within validFrom-validTo range.
     *
     * @return list of currently valid codelist registry entries as DTOs
     */
    public List<CodelistRegistryDTO> findAllCurrentlyValid() {
        return codelistRegistryRepository.findAllCurrentlyValid().stream()
            .map(CodelistRegistryDTO::fromEntity)
            .collect(Collectors.toList());
    }

    /**
     * Retrieves all codelist registry entries filtered by flag.
     *
     * @param flagId the flag ID to filter by
     * @return list of codelist registry entries as DTOs
     */
    public List<CodelistRegistryDTO> findByFlag(Long flagId) {
        return codelistRegistryRepository.findByFlagId(flagId).stream()
            .map(CodelistRegistryDTO::fromEntity)
            .collect(Collectors.toList());
    }

    /**
     * Finds a codelist registry entry by its ID.
     *
     * @param id the codelist registry entry ID
     * @return the codelist registry entry as DTO
     * @throws RuntimeException if codelist registry entry not found
     */
    public CodelistRegistryDTO findById(Long id) {
        CodelistRegistry entity = codelistRegistryRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Codelist registry entry not found with id: " + id));
        return CodelistRegistryDTO.fromEntity(entity);
    }

    /**
     * Finds a codelist registry entry by its unique code.
     *
     * @param code the codelist registry entry code
     * @return the codelist registry entry as DTO
     * @throws RuntimeException if codelist registry entry not found
     */
    public CodelistRegistryDTO findByCode(String code) {
        CodelistRegistry entity = codelistRegistryRepository.findByCode(code)
            .orElseThrow(() -> new RuntimeException("Codelist registry entry not found with code: " + code));
        return CodelistRegistryDTO.fromEntity(entity);
    }

    /**
     * Creates a new codelist registry entry.
     *
     * @param dto the codelist registry entry data
     * @return the created codelist registry entry as DTO
     * @throws RuntimeException if codelist registry entry with same code already exists
     */
    @Transactional
    public CodelistRegistryDTO create(CodelistRegistryDTO dto) {
        if (codelistRegistryRepository.existsByCode(dto.getCode())) {
            throw new RuntimeException("Codelist registry entry with code " + dto.getCode() + " already exists");
        }

        CodelistRegistry entity = new CodelistRegistry();
        entity.setCode(dto.getCode());
        entity.setNameCs(dto.getNameCs());
        entity.setNameEn(dto.getNameEn());
        entity.setDescriptionCs(dto.getDescriptionCs());
        entity.setDescriptionEn(dto.getDescriptionEn());
        entity.setWebUrl(dto.getWebUrl());
        entity.setApiUrl(dto.getApiUrl());
        entity.setIconClass(dto.getIconClass());
        entity.setValidFrom(dto.getValidFrom());
        entity.setValidTo(dto.getValidTo());
        entity.setSortOrder(dto.getSortOrder());
        updateFlags(entity, dto.getFlagIds());

        entity = codelistRegistryRepository.save(entity);

        eventPublisher.publishInsert(CODELIST_NAME, CODELIST_CODE,
                entity.getId(), entity.getCode(), entity.getNameCs());

        return CodelistRegistryDTO.fromEntity(entity);
    }

    /**
     * Updates an existing codelist registry entry.
     *
     * @param id the ID of the codelist registry entry to update
     * @param dto the new codelist registry entry data
     * @return the updated codelist registry entry as DTO
     * @throws RuntimeException if codelist registry entry not found or new code already exists
     */
    @Transactional
    public CodelistRegistryDTO update(Long id, CodelistRegistryDTO dto) {
        CodelistRegistry existing = codelistRegistryRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Codelist registry entry not found with id: " + id));

        if (!existing.getCode().equals(dto.getCode()) &&
            codelistRegistryRepository.existsByCode(dto.getCode())) {
            throw new RuntimeException("Codelist registry entry with code " + dto.getCode() + " already exists");
        }

        existing.setCode(dto.getCode());
        existing.setNameCs(dto.getNameCs());
        existing.setNameEn(dto.getNameEn());
        existing.setDescriptionCs(dto.getDescriptionCs());
        existing.setDescriptionEn(dto.getDescriptionEn());
        existing.setWebUrl(dto.getWebUrl());
        existing.setApiUrl(dto.getApiUrl());
        existing.setIconClass(dto.getIconClass());
        existing.setValidFrom(dto.getValidFrom());
        existing.setValidTo(dto.getValidTo());
        existing.setSortOrder(dto.getSortOrder());
        updateFlags(existing, dto.getFlagIds());

        existing = codelistRegistryRepository.save(existing);

        eventPublisher.publishUpdate(CODELIST_NAME, CODELIST_CODE,
                existing.getId(), existing.getCode(), existing.getNameCs());

        return CodelistRegistryDTO.fromEntity(existing);
    }

    /**
     * Adds a flag to a codelist.
     *
     * @param codelistId the codelist ID
     * @param flagId the flag ID to add
     */
    @Transactional
    public void addFlag(Long codelistId, Long flagId) {
        CodelistRegistry codelist = codelistRegistryRepository.findById(codelistId)
            .orElseThrow(() -> new RuntimeException("Codelist not found with id: " + codelistId));
        Flag flag = flagRepository.findById(flagId)
            .orElseThrow(() -> new RuntimeException("Flag not found with id: " + flagId));
        codelist.getFlags().add(flag);
        codelistRegistryRepository.save(codelist);
    }

    /**
     * Removes a flag from a codelist.
     *
     * @param codelistId the codelist ID
     * @param flagId the flag ID to remove
     */
    @Transactional
    public void removeFlag(Long codelistId, Long flagId) {
        CodelistRegistry codelist = codelistRegistryRepository.findById(codelistId)
            .orElseThrow(() -> new RuntimeException("Codelist not found with id: " + codelistId));
        codelist.getFlags().removeIf(f -> f.getId().equals(flagId));
        codelistRegistryRepository.save(codelist);
    }

    private void updateFlags(CodelistRegistry entity, Set<Long> flagIds) {
        if (flagIds != null && !flagIds.isEmpty()) {
            Set<Flag> flags = new HashSet<>(flagRepository.findAllById(flagIds));
            entity.setFlags(flags);
        } else {
            entity.getFlags().clear();
        }
    }

    /**
     * Deletes a codelist registry entry by its ID.
     *
     * @param id the ID of the codelist registry entry to delete
     * @throws RuntimeException if codelist registry entry not found
     */
    @Transactional
    public void delete(Long id) {
        CodelistRegistry existing = codelistRegistryRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Codelist registry entry not found with id: " + id));

        String code = existing.getCode();
        String name = existing.getNameCs();

        codelistRegistryRepository.deleteById(id);

        eventPublisher.publishDelete(CODELIST_NAME, CODELIST_CODE, id, code, name);
    }
}

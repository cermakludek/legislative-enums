package cz.intelis.legislativeenums.flag;

import cz.intelis.legislativeenums.notification.CodelistEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service layer for managing Flag entities.
 * Provides business logic for CRUD operations on flags.
 *
 * @author Legislative Codelists Team
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FlagService {

    private static final String CODELIST_NAME = "Příznaky";
    private static final String CODELIST_CODE = "FLAG";

    private final FlagRepository flagRepository;
    private final CodelistEventPublisher eventPublisher;

    /**
     * Retrieves all flags ordered by sort order and code.
     *
     * @return list of all flags as DTOs
     */
    public List<FlagDTO> findAll() {
        return flagRepository.findAllOrdered().stream()
            .map(FlagDTO::fromEntity)
            .collect(Collectors.toList());
    }

    /**
     * Retrieves all active flags.
     *
     * @return list of active flags as DTOs
     */
    public List<FlagDTO> findAllActive() {
        return flagRepository.findByActiveTrueOrderBySortOrderAscCodeAsc().stream()
            .map(FlagDTO::fromEntity)
            .collect(Collectors.toList());
    }

    /**
     * Retrieves all currently valid (active) flags.
     * For flags, validity is determined by the active attribute only.
     *
     * @return list of active flags as DTOs
     */
    public List<FlagDTO> findAllCurrentlyValid() {
        return flagRepository.findByActiveTrueOrderBySortOrderAscCodeAsc().stream()
            .map(FlagDTO::fromEntity)
            .collect(Collectors.toList());
    }

    /**
     * Finds a flag by its ID.
     *
     * @param id the flag ID
     * @return the flag as DTO
     * @throws RuntimeException if flag not found
     */
    public FlagDTO findById(Long id) {
        Flag entity = flagRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Flag not found with id: " + id));
        return FlagDTO.fromEntity(entity);
    }

    /**
     * Finds a flag by its unique code.
     *
     * @param code the flag code
     * @return the flag as DTO
     * @throws RuntimeException if flag not found
     */
    public FlagDTO findByCode(String code) {
        Flag entity = flagRepository.findByCode(code)
            .orElseThrow(() -> new RuntimeException("Flag not found with code: " + code));
        return FlagDTO.fromEntity(entity);
    }

    /**
     * Creates a new flag.
     *
     * @param dto the flag data
     * @return the created flag as DTO
     * @throws RuntimeException if flag with same code already exists
     */
    @Transactional
    public FlagDTO create(FlagDTO dto) {
        if (flagRepository.existsByCode(dto.getCode())) {
            throw new RuntimeException("Flag with code " + dto.getCode() + " already exists");
        }

        Flag entity = new Flag();
        entity.setCode(dto.getCode());
        entity.setNameCs(dto.getNameCs());
        entity.setNameEn(dto.getNameEn());
        entity.setDescriptionCs(dto.getDescriptionCs());
        entity.setDescriptionEn(dto.getDescriptionEn());
        entity.setColor(dto.getColor());
        entity.setIconClass(dto.getIconClass());
        entity.setActive(dto.getActive() != null ? dto.getActive() : true);
        entity.setSortOrder(dto.getSortOrder());

        entity = flagRepository.save(entity);

        eventPublisher.publishInsert(CODELIST_NAME, CODELIST_CODE,
                entity.getId(), entity.getCode(), entity.getNameCs());

        return FlagDTO.fromEntity(entity);
    }

    /**
     * Updates an existing flag.
     *
     * @param id the ID of the flag to update
     * @param dto the new flag data
     * @return the updated flag as DTO
     * @throws RuntimeException if flag not found or new code already exists
     */
    @Transactional
    public FlagDTO update(Long id, FlagDTO dto) {
        Flag existing = flagRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Flag not found with id: " + id));

        if (!existing.getCode().equals(dto.getCode()) &&
            flagRepository.existsByCode(dto.getCode())) {
            throw new RuntimeException("Flag with code " + dto.getCode() + " already exists");
        }

        existing.setCode(dto.getCode());
        existing.setNameCs(dto.getNameCs());
        existing.setNameEn(dto.getNameEn());
        existing.setDescriptionCs(dto.getDescriptionCs());
        existing.setDescriptionEn(dto.getDescriptionEn());
        existing.setColor(dto.getColor());
        existing.setIconClass(dto.getIconClass());
        existing.setActive(dto.getActive());
        existing.setSortOrder(dto.getSortOrder());

        existing = flagRepository.save(existing);

        eventPublisher.publishUpdate(CODELIST_NAME, CODELIST_CODE,
                existing.getId(), existing.getCode(), existing.getNameCs());

        return FlagDTO.fromEntity(existing);
    }

    /**
     * Deletes a flag by its ID.
     *
     * @param id the ID of the flag to delete
     * @throws RuntimeException if flag not found
     */
    @Transactional
    public void delete(Long id) {
        Flag existing = flagRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Flag not found with id: " + id));

        String code = existing.getCode();
        String name = existing.getNameCs();

        flagRepository.deleteById(id);

        eventPublisher.publishDelete(CODELIST_NAME, CODELIST_CODE, id, code, name);
    }
}

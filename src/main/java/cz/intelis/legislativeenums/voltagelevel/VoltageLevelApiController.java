package cz.intelis.legislativeenums.voltagelevel;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST API controller for managing VoltageLevel entities.
 * Provides RESTful CRUD operations for voltage level classifications
 * (Rozdělení napětí dle velikosti).
 *
 * @author Legislative Codelists Team
 */
@RestController
@RequestMapping(value = "/api/v1/voltage-levels", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
@RequiredArgsConstructor
@Tag(name = "Voltage Levels", description = "API for managing voltage level classifications")
public class VoltageLevelApiController {

    private final VoltageLevelService voltageLevelService;

    /**
     * Retrieves all voltage levels, optionally filtered by validity status.
     *
     * @param validOnly if true (default), returns only currently valid voltage levels
     * @return list of voltage levels as DTOs
     */
    @GetMapping
    @Operation(summary = "Get all voltage levels", description = "Returns list of voltage levels. By default returns only currently valid items (validOnly=true). Set validOnly=false to get all items.")
    public ResponseEntity<List<VoltageLevelDTO>> findAll(
            @RequestParam(required = false, defaultValue = "true") boolean validOnly) {
        List<VoltageLevelDTO> result = validOnly
            ? voltageLevelService.findAllCurrentlyValid()
            : voltageLevelService.findAll();
        return ResponseEntity.ok(result);
    }

    /**
     * Retrieves a voltage level by its ID.
     *
     * @param id the voltage level ID
     * @return the voltage level as DTO
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get voltage level by ID", description = "Returns a single voltage level classification by its unique identifier")
    public ResponseEntity<VoltageLevelDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(voltageLevelService.findById(id));
    }

    /**
     * Retrieves a voltage level by its unique code.
     *
     * @param code the voltage level code
     * @return the voltage level as DTO
     */
    @GetMapping("/code/{code}")
    @Operation(summary = "Get voltage level by code", description = "Returns a single voltage level by its unique code (e.g., NN, VN, VVN)")
    public ResponseEntity<VoltageLevelDTO> findByCode(@PathVariable String code) {
        return ResponseEntity.ok(voltageLevelService.findByCode(code));
    }

    /**
     * Creates a new voltage level.
     *
     * @param dto the voltage level data
     * @return the created voltage level as DTO with HTTP 201 status
     */
    @PostMapping
    @Operation(summary = "Create a new voltage level", description = "Creates a new voltage level classification. Code must be unique.")
    public ResponseEntity<VoltageLevelDTO> create(@Valid @RequestBody VoltageLevelDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(voltageLevelService.create(dto));
    }

    /**
     * Updates an existing voltage level.
     *
     * @param id the ID of the voltage level to update
     * @param dto the new voltage level data
     * @return the updated voltage level as DTO
     */
    @PutMapping("/{id}")
    @Operation(summary = "Update an existing voltage level", description = "Updates an existing voltage level classification. Returns 404 if not found.")
    public ResponseEntity<VoltageLevelDTO> update(@PathVariable Long id, @Valid @RequestBody VoltageLevelDTO dto) {
        return ResponseEntity.ok(voltageLevelService.update(id, dto));
    }

    /**
     * Deletes a voltage level by its ID.
     *
     * @param id the ID of the voltage level to delete
     * @return HTTP 204 No Content on success
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a voltage level", description = "Permanently deletes a voltage level classification by ID. Returns 404 if not found.")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        voltageLevelService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

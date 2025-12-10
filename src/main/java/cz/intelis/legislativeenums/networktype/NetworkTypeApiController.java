package cz.intelis.legislativeenums.networktype;

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
 * REST API controller for managing NetworkType entities.
 * Provides RESTful CRUD operations for network type classifications
 * (Rozdělení sítí z hlediska vedení).
 *
 * @author Legislative Codelists Team
 */
@RestController
@RequestMapping(value = "/api/v1/network-types", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
@RequiredArgsConstructor
@Tag(name = "Network Types", description = "API for managing network type classifications")
public class NetworkTypeApiController {

    private final NetworkTypeService networkTypeService;

    /**
     * Retrieves all network types, optionally filtered by validity status.
     *
     * @param validOnly if true, returns only currently valid network types (default: true)
     * @return list of network types as DTOs
     */
    @GetMapping
    @Operation(summary = "Get all network types", description = "Returns list of network types. By default returns only currently valid items (validOnly=true). Set validOnly=false to get all items.")
    public ResponseEntity<List<NetworkTypeDTO>> findAll(
            @RequestParam(required = false, defaultValue = "true") boolean validOnly) {
        List<NetworkTypeDTO> result = validOnly
            ? networkTypeService.findAllCurrentlyValid()
            : networkTypeService.findAll();
        return ResponseEntity.ok(result);
    }

    /**
     * Retrieves a network type by its ID.
     *
     * @param id the network type ID
     * @return the network type as DTO
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get network type by ID", description = "Returns a single network type classification by its unique identifier")
    public ResponseEntity<NetworkTypeDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(networkTypeService.findById(id));
    }

    /**
     * Retrieves a network type by its unique code.
     *
     * @param code the network type code
     * @return the network type as DTO
     */
    @GetMapping("/code/{code}")
    @Operation(summary = "Get network type by code", description = "Returns a single network type by its unique code (e.g., PAP, OKR, MRI)")
    public ResponseEntity<NetworkTypeDTO> findByCode(@PathVariable String code) {
        return ResponseEntity.ok(networkTypeService.findByCode(code));
    }

    /**
     * Creates a new network type.
     *
     * @param dto the network type data
     * @return the created network type as DTO with HTTP 201 status
     */
    @PostMapping
    @Operation(summary = "Create a new network type", description = "Creates a new network type classification. Code must be unique.")
    public ResponseEntity<NetworkTypeDTO> create(@Valid @RequestBody NetworkTypeDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(networkTypeService.create(dto));
    }

    /**
     * Updates an existing network type.
     *
     * @param id the ID of the network type to update
     * @param dto the new network type data
     * @return the updated network type as DTO
     */
    @PutMapping("/{id}")
    @Operation(summary = "Update an existing network type", description = "Updates an existing network type classification. Returns 404 if not found.")
    public ResponseEntity<NetworkTypeDTO> update(@PathVariable Long id, @Valid @RequestBody NetworkTypeDTO dto) {
        return ResponseEntity.ok(networkTypeService.update(id, dto));
    }

    /**
     * Deletes a network type by its ID.
     *
     * @param id the ID of the network type to delete
     * @return HTTP 204 No Content on success
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a network type", description = "Permanently deletes a network type classification by ID. Returns 404 if not found.")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        networkTypeService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

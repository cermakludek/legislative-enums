package cz.intelis.legislativeenums.cuzk.buildingrightpurpose;

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
 * REST API controller for managing BuildingRightPurpose entities (Účel práva stavby).
 * Based on ČÚZK codelist SC_UCELY_PS.
 *
 * @author Legislative Codelists Team
 */
@RestController
@RequestMapping(value = "/api/v1/cuzk/building-right-purposes", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
@RequiredArgsConstructor
@Tag(name = "ČÚZK - Building Right Purposes", description = "API for building right purposes (Účel práva stavby)")
public class BuildingRightPurposeApiController {

    private final BuildingRightPurposeService service;

    @GetMapping
    @Operation(summary = "Get all building right purposes", description = "Returns list of building right purposes. By default returns only currently valid items (validOnly=true). Set validOnly=false to get all items.")
    public ResponseEntity<List<BuildingRightPurposeDTO>> findAll(
            @RequestParam(required = false, defaultValue = "true") boolean validOnly) {
        List<BuildingRightPurposeDTO> result = validOnly ? service.findAllCurrentlyValid() : service.findAll();
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get building right purpose by ID")
    public ResponseEntity<BuildingRightPurposeDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @GetMapping("/code/{code}")
    @Operation(summary = "Get building right purpose by code")
    public ResponseEntity<BuildingRightPurposeDTO> findByCode(@PathVariable String code) {
        return ResponseEntity.ok(service.findByCode(code));
    }

    @PostMapping
    @Operation(summary = "Create a new building right purpose")
    public ResponseEntity<BuildingRightPurposeDTO> create(@Valid @RequestBody BuildingRightPurposeDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(dto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing building right purpose")
    public ResponseEntity<BuildingRightPurposeDTO> update(@PathVariable Long id, @Valid @RequestBody BuildingRightPurposeDTO dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a building right purpose")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}

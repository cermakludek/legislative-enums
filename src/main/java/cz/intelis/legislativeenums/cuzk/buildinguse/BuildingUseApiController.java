package cz.intelis.legislativeenums.cuzk.buildinguse;

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
 * REST API controller for managing BuildingUse entities (Způsob využití stavby).
 * Based on ČÚZK codelist SC_ZP_VYUZITI_BUD.
 *
 * @author Legislative Codelists Team
 */
@RestController
@RequestMapping(value = "/api/v1/cuzk/building-uses", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
@RequiredArgsConstructor
@Tag(name = "ČÚZK - Building Uses", description = "API for building use classifications (Způsob využití stavby)")
public class BuildingUseApiController {

    private final BuildingUseService service;

    @GetMapping
    @Operation(summary = "Get all building uses", description = "Returns list of building uses. By default returns only currently valid items (validOnly=true). Set validOnly=false to get all items.")
    public ResponseEntity<List<BuildingUseDTO>> findAll(
            @RequestParam(required = false, defaultValue = "true") boolean validOnly) {
        List<BuildingUseDTO> result = validOnly ? service.findAllCurrentlyValid() : service.findAll();
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get building use by ID")
    public ResponseEntity<BuildingUseDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @GetMapping("/code/{code}")
    @Operation(summary = "Get building use by code")
    public ResponseEntity<BuildingUseDTO> findByCode(@PathVariable String code) {
        return ResponseEntity.ok(service.findByCode(code));
    }

    @PostMapping
    @Operation(summary = "Create a new building use")
    public ResponseEntity<BuildingUseDTO> create(@Valid @RequestBody BuildingUseDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(dto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing building use")
    public ResponseEntity<BuildingUseDTO> update(@PathVariable Long id, @Valid @RequestBody BuildingUseDTO dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a building use")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}

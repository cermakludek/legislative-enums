package cz.intelis.legislativeenums.cuzk.buildingtype;

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
 * REST API controller for managing BuildingType entities (Typ stavby).
 * Based on ČÚZK codelist SC_T_BUDOV.
 *
 * @author Legislative Codelists Team
 */
@RestController
@RequestMapping(value = "/api/v1/cuzk/building-types", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
@RequiredArgsConstructor
@Tag(name = "ČÚZK - Building Types", description = "API for building type classifications (Typ stavby)")
public class BuildingTypeApiController {

    private final BuildingTypeService service;

    @GetMapping
    @Operation(summary = "Get all building types", description = "Returns list of building types. By default returns only currently valid items (validOnly=true). Set validOnly=false to get all items.")
    public ResponseEntity<List<BuildingTypeDTO>> findAll(
            @RequestParam(required = false, defaultValue = "true") boolean validOnly) {
        List<BuildingTypeDTO> result = validOnly ? service.findAllCurrentlyValid() : service.findAll();
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get building type by ID")
    public ResponseEntity<BuildingTypeDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @GetMapping("/code/{code}")
    @Operation(summary = "Get building type by code")
    public ResponseEntity<BuildingTypeDTO> findByCode(@PathVariable String code) {
        return ResponseEntity.ok(service.findByCode(code));
    }

    @PostMapping
    @Operation(summary = "Create a new building type")
    public ResponseEntity<BuildingTypeDTO> create(@Valid @RequestBody BuildingTypeDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(dto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing building type")
    public ResponseEntity<BuildingTypeDTO> update(@PathVariable Long id, @Valid @RequestBody BuildingTypeDTO dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a building type")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}

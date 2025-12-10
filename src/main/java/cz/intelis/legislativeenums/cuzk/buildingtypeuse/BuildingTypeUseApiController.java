package cz.intelis.legislativeenums.cuzk.buildingtypeuse;

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
 * REST API controller for managing BuildingTypeUse entities (Vazba typ stavby a využití stavby).
 * Based on ČÚZK codelist SC_TYPB_ZPVYB.
 *
 * @author Legislative Codelists Team
 */
@RestController
@RequestMapping(value = "/api/v1/cuzk/building-type-uses", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
@RequiredArgsConstructor
@Tag(name = "ČÚZK - Building Type Uses", description = "API for building type and use relationships (Vazba typ stavby a využití stavby)")
public class BuildingTypeUseApiController {

    private final BuildingTypeUseService service;

    @GetMapping
    @Operation(summary = "Get all building type uses", description = "Returns list of building type use relations. By default returns only currently valid items (validOnly=true). Set validOnly=false to get all items.")
    public ResponseEntity<List<BuildingTypeUseDTO>> findAll(
            @RequestParam(required = false, defaultValue = "true") boolean validOnly) {
        List<BuildingTypeUseDTO> result = validOnly ? service.findAllCurrentlyValid() : service.findAll();
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get building type use by ID")
    public ResponseEntity<BuildingTypeUseDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @GetMapping("/building-type/{buildingTypeCode}")
    @Operation(summary = "Get building type uses by building type code")
    public ResponseEntity<List<BuildingTypeUseDTO>> findByBuildingTypeCode(@PathVariable String buildingTypeCode) {
        return ResponseEntity.ok(service.findByBuildingTypeCode(buildingTypeCode));
    }

    @GetMapping("/building-use/{buildingUseCode}")
    @Operation(summary = "Get building type uses by building use code")
    public ResponseEntity<List<BuildingTypeUseDTO>> findByBuildingUseCode(@PathVariable String buildingUseCode) {
        return ResponseEntity.ok(service.findByBuildingUseCode(buildingUseCode));
    }

    @PostMapping
    @Operation(summary = "Create a new building type use relation")
    public ResponseEntity<BuildingTypeUseDTO> create(@Valid @RequestBody BuildingTypeUseDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(dto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing building type use relation")
    public ResponseEntity<BuildingTypeUseDTO> update(@PathVariable Long id, @Valid @RequestBody BuildingTypeUseDTO dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a building type use relation")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}

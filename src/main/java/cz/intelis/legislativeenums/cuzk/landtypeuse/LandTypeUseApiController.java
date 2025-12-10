package cz.intelis.legislativeenums.cuzk.landtypeuse;

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
 * REST API controller for managing LandTypeUse entities (Vazba druh pozemku a využití).
 * Based on ČÚZK codelist SC_POZEMEK_VYUZITI.
 *
 * @author Legislative Codelists Team
 */
@RestController
@RequestMapping(value = "/api/v1/cuzk/land-type-uses", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
@RequiredArgsConstructor
@Tag(name = "ČÚZK - Land Type Uses", description = "API for land type and use relationships (Vazba druh pozemku a využití)")
public class LandTypeUseApiController {

    private final LandTypeUseService service;

    @GetMapping
    @Operation(summary = "Get all land type uses", description = "Returns list of land type use relations. By default returns only currently valid items (validOnly=true). Set validOnly=false to get all items.")
    public ResponseEntity<List<LandTypeUseDTO>> findAll(
            @RequestParam(required = false, defaultValue = "true") boolean validOnly) {
        List<LandTypeUseDTO> result = validOnly ? service.findAllCurrentlyValid() : service.findAll();
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get land type use by ID")
    public ResponseEntity<LandTypeUseDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @GetMapping("/land-type/{landTypeCode}")
    @Operation(summary = "Get land type uses by land type code")
    public ResponseEntity<List<LandTypeUseDTO>> findByLandTypeCode(@PathVariable String landTypeCode) {
        return ResponseEntity.ok(service.findByLandTypeCode(landTypeCode));
    }

    @GetMapping("/land-use/{landUseCode}")
    @Operation(summary = "Get land type uses by land use code")
    public ResponseEntity<List<LandTypeUseDTO>> findByLandUseCode(@PathVariable String landUseCode) {
        return ResponseEntity.ok(service.findByLandUseCode(landUseCode));
    }

    @PostMapping
    @Operation(summary = "Create a new land type use relation")
    public ResponseEntity<LandTypeUseDTO> create(@Valid @RequestBody LandTypeUseDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(dto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing land type use relation")
    public ResponseEntity<LandTypeUseDTO> update(@PathVariable Long id, @Valid @RequestBody LandTypeUseDTO dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a land type use relation")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}

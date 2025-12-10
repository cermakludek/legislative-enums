package cz.intelis.legislativeenums.cuzk.landuse;

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
 * REST API controller for managing LandUse entities (Způsob využití pozemku).
 * Based on ČÚZK codelist SC_ZP_VYUZITI_POZ.
 *
 * @author Legislative Codelists Team
 */
@RestController
@RequestMapping(value = "/api/v1/cuzk/land-uses", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
@RequiredArgsConstructor
@Tag(name = "ČÚZK - Land Uses", description = "API for land use classifications (Způsob využití pozemku)")
public class LandUseApiController {

    private final LandUseService service;

    @GetMapping
    @Operation(summary = "Get all land uses", description = "Returns list of land uses. By default returns only currently valid items (validOnly=true). Set validOnly=false to get all items.")
    public ResponseEntity<List<LandUseDTO>> findAll(
            @RequestParam(required = false, defaultValue = "true") boolean validOnly) {
        List<LandUseDTO> result = validOnly ? service.findAllCurrentlyValid() : service.findAll();
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get land use by ID")
    public ResponseEntity<LandUseDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @GetMapping("/code/{code}")
    @Operation(summary = "Get land use by code")
    public ResponseEntity<LandUseDTO> findByCode(@PathVariable String code) {
        return ResponseEntity.ok(service.findByCode(code));
    }

    @PostMapping
    @Operation(summary = "Create a new land use")
    public ResponseEntity<LandUseDTO> create(@Valid @RequestBody LandUseDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(dto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing land use")
    public ResponseEntity<LandUseDTO> update(@PathVariable Long id, @Valid @RequestBody LandUseDTO dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a land use")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}

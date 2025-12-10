package cz.intelis.legislativeenums.cuzk.soilecologicalunit;

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
 * REST API controller for managing SoilEcologicalUnit entities (Bonitované půdně ekologické jednotky - BPEJ).
 * Based on ČÚZK codelist SC_BPEJ.
 *
 * @author Legislative Codelists Team
 */
@RestController
@RequestMapping(value = "/api/v1/cuzk/soil-ecological-units", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
@RequiredArgsConstructor
@Tag(name = "ČÚZK - Soil Ecological Units", description = "API for soil ecological units (Bonitované půdně ekologické jednotky - BPEJ)")
public class SoilEcologicalUnitApiController {

    private final SoilEcologicalUnitService service;

    @GetMapping
    @Operation(summary = "Get all soil ecological units", description = "Returns list of soil ecological units. By default returns only currently valid items (validOnly=true). Set validOnly=false to get all items.")
    public ResponseEntity<List<SoilEcologicalUnitDTO>> findAll(
            @RequestParam(required = false, defaultValue = "true") boolean validOnly) {
        List<SoilEcologicalUnitDTO> result = validOnly ? service.findAllCurrentlyValid() : service.findAll();
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get soil ecological unit by ID")
    public ResponseEntity<SoilEcologicalUnitDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @GetMapping("/code/{code}")
    @Operation(summary = "Get soil ecological unit by code")
    public ResponseEntity<SoilEcologicalUnitDTO> findByCode(@PathVariable String code) {
        return ResponseEntity.ok(service.findByCode(code));
    }

    @PostMapping
    @Operation(summary = "Create a new soil ecological unit")
    public ResponseEntity<SoilEcologicalUnitDTO> create(@Valid @RequestBody SoilEcologicalUnitDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(dto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing soil ecological unit")
    public ResponseEntity<SoilEcologicalUnitDTO> update(@PathVariable Long id, @Valid @RequestBody SoilEcologicalUnitDTO dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a soil ecological unit")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}

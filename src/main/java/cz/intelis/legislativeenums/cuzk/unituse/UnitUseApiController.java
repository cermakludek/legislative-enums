package cz.intelis.legislativeenums.cuzk.unituse;

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
 * REST API controller for managing UnitUse entities (Způsob využití jednotky).
 * Based on ČÚZK codelist SC_ZP_VYUZITI_JED.
 *
 * @author Legislative Codelists Team
 */
@RestController
@RequestMapping(value = "/api/v1/cuzk/unit-uses", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
@RequiredArgsConstructor
@Tag(name = "ČÚZK - Unit Uses", description = "API for unit use classifications (Způsob využití jednotky)")
public class UnitUseApiController {

    private final UnitUseService service;

    @GetMapping
    @Operation(summary = "Get all unit uses", description = "Returns list of unit uses. By default returns only currently valid items (validOnly=true). Set validOnly=false to get all items.")
    public ResponseEntity<List<UnitUseDTO>> findAll(
            @RequestParam(required = false, defaultValue = "true") boolean validOnly) {
        List<UnitUseDTO> result = validOnly ? service.findAllCurrentlyValid() : service.findAll();
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get unit use by ID")
    public ResponseEntity<UnitUseDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @GetMapping("/code/{code}")
    @Operation(summary = "Get unit use by code")
    public ResponseEntity<UnitUseDTO> findByCode(@PathVariable String code) {
        return ResponseEntity.ok(service.findByCode(code));
    }

    @PostMapping
    @Operation(summary = "Create a new unit use")
    public ResponseEntity<UnitUseDTO> create(@Valid @RequestBody UnitUseDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(dto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing unit use")
    public ResponseEntity<UnitUseDTO> update(@PathVariable Long id, @Valid @RequestBody UnitUseDTO dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a unit use")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}

package cz.intelis.legislativeenums.cuzk.areadetermination;

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
 * REST API controller for managing AreaDetermination entities (Způsob určení výměry).
 * Based on ČÚZK codelist SC_ZP_URCENI_VYMERY.
 *
 * @author Legislative Codelists Team
 */
@RestController
@RequestMapping(value = "/api/v1/cuzk/area-determinations", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
@RequiredArgsConstructor
@Tag(name = "ČÚZK - Area Determinations", description = "API for area determination methods (Způsob určení výměry)")
public class AreaDeterminationApiController {

    private final AreaDeterminationService service;

    @GetMapping
    @Operation(summary = "Get all area determinations", description = "Returns list of area determinations. By default returns only currently valid items (validOnly=true). Set validOnly=false to get all items.")
    public ResponseEntity<List<AreaDeterminationDTO>> findAll(
            @RequestParam(required = false, defaultValue = "true") boolean validOnly) {
        List<AreaDeterminationDTO> result = validOnly ? service.findAllCurrentlyValid() : service.findAll();
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get area determination by ID")
    public ResponseEntity<AreaDeterminationDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @GetMapping("/code/{code}")
    @Operation(summary = "Get area determination by code")
    public ResponseEntity<AreaDeterminationDTO> findByCode(@PathVariable String code) {
        return ResponseEntity.ok(service.findByCode(code));
    }

    @PostMapping
    @Operation(summary = "Create a new area determination")
    public ResponseEntity<AreaDeterminationDTO> create(@Valid @RequestBody AreaDeterminationDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(dto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing area determination")
    public ResponseEntity<AreaDeterminationDTO> update(@PathVariable Long id, @Valid @RequestBody AreaDeterminationDTO dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete an area determination")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}

package cz.intelis.legislativeenums.cuzk.simplifiedparcelsource;

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
 * REST API controller for managing SimplifiedParcelSource entities (Zdroje parcel zjednodušené evidence).
 * Based on ČÚZK codelist SC_ZDROJE_PARCEL_ZE.
 *
 * @author Legislative Codelists Team
 */
@RestController
@RequestMapping(value = "/api/v1/cuzk/simplified-parcel-sources", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
@RequiredArgsConstructor
@Tag(name = "ČÚZK - Simplified Parcel Sources", description = "API for simplified evidence parcel sources (Zdroje parcel zjednodušené evidence)")
public class SimplifiedParcelSourceApiController {

    private final SimplifiedParcelSourceService service;

    @GetMapping
    @Operation(summary = "Get all simplified parcel sources", description = "Returns list of simplified parcel sources. By default returns only currently valid items (validOnly=true). Set validOnly=false to get all items.")
    public ResponseEntity<List<SimplifiedParcelSourceDTO>> findAll(
            @RequestParam(required = false, defaultValue = "true") boolean validOnly) {
        List<SimplifiedParcelSourceDTO> result = validOnly ? service.findAllCurrentlyValid() : service.findAll();
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get simplified parcel source by ID")
    public ResponseEntity<SimplifiedParcelSourceDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @GetMapping("/code/{code}")
    @Operation(summary = "Get simplified parcel source by code")
    public ResponseEntity<SimplifiedParcelSourceDTO> findByCode(@PathVariable String code) {
        return ResponseEntity.ok(service.findByCode(code));
    }

    @PostMapping
    @Operation(summary = "Create a new simplified parcel source")
    public ResponseEntity<SimplifiedParcelSourceDTO> create(@Valid @RequestBody SimplifiedParcelSourceDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(dto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing simplified parcel source")
    public ResponseEntity<SimplifiedParcelSourceDTO> update(@PathVariable Long id, @Valid @RequestBody SimplifiedParcelSourceDTO dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a simplified parcel source")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}

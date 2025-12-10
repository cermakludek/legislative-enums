package cz.intelis.legislativeenums.cuzk.propertyprotectiontype;

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
 * REST API controller for managing PropertyProtectionType entities (Typ ochrany nemovitosti).
 * Based on ČÚZK codelist SC_T_OCHRANY_NEM.
 *
 * @author Legislative Codelists Team
 */
@RestController
@RequestMapping(value = "/api/v1/cuzk/property-protection-types", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
@RequiredArgsConstructor
@Tag(name = "ČÚZK - Property Protection Types", description = "API for property protection types (Typ ochrany nemovitosti)")
public class PropertyProtectionTypeApiController {

    private final PropertyProtectionTypeService service;

    @GetMapping
    @Operation(summary = "Get all property protection types", description = "Returns list of property protection types. By default returns only currently valid items (validOnly=true). Set validOnly=false to get all items.")
    public ResponseEntity<List<PropertyProtectionTypeDTO>> findAll(
            @RequestParam(required = false, defaultValue = "true") boolean validOnly) {
        List<PropertyProtectionTypeDTO> result = validOnly ? service.findAllCurrentlyValid() : service.findAll();
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get property protection type by ID")
    public ResponseEntity<PropertyProtectionTypeDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @GetMapping("/code/{code}")
    @Operation(summary = "Get property protection type by code")
    public ResponseEntity<PropertyProtectionTypeDTO> findByCode(@PathVariable String code) {
        return ResponseEntity.ok(service.findByCode(code));
    }

    @PostMapping
    @Operation(summary = "Create a new property protection type")
    public ResponseEntity<PropertyProtectionTypeDTO> create(@Valid @RequestBody PropertyProtectionTypeDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(dto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing property protection type")
    public ResponseEntity<PropertyProtectionTypeDTO> update(@PathVariable Long id, @Valid @RequestBody PropertyProtectionTypeDTO dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a property protection type")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}

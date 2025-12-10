package cz.intelis.legislativeenums.cuzk.propertyprotection;

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
 * REST API controller for managing PropertyProtection entities (Způsob ochrany nemovitosti).
 * Based on ČÚZK codelist SC_ZP_OCHRANY_NEM.
 *
 * @author Legislative Codelists Team
 */
@RestController
@RequestMapping(value = "/api/v1/cuzk/property-protections", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
@RequiredArgsConstructor
@Tag(name = "ČÚZK - Property Protections", description = "API for property protection methods (Způsob ochrany nemovitosti)")
public class PropertyProtectionApiController {

    private final PropertyProtectionService service;

    @GetMapping
    @Operation(summary = "Get all property protections", description = "Returns list of property protections. By default returns only currently valid items (validOnly=true). Set validOnly=false to get all items.")
    public ResponseEntity<List<PropertyProtectionDTO>> findAll(
            @RequestParam(required = false, defaultValue = "true") boolean validOnly) {
        List<PropertyProtectionDTO> result = validOnly ? service.findAllCurrentlyValid() : service.findAll();
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get property protection by ID")
    public ResponseEntity<PropertyProtectionDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @GetMapping("/code/{code}")
    @Operation(summary = "Get property protection by code")
    public ResponseEntity<PropertyProtectionDTO> findByCode(@PathVariable String code) {
        return ResponseEntity.ok(service.findByCode(code));
    }

    @GetMapping("/type/{protectionTypeCode}")
    @Operation(summary = "Get property protections by protection type code")
    public ResponseEntity<List<PropertyProtectionDTO>> findByProtectionTypeCode(@PathVariable String protectionTypeCode) {
        return ResponseEntity.ok(service.findByProtectionTypeCode(protectionTypeCode));
    }

    @PostMapping
    @Operation(summary = "Create a new property protection")
    public ResponseEntity<PropertyProtectionDTO> create(@Valid @RequestBody PropertyProtectionDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(dto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing property protection")
    public ResponseEntity<PropertyProtectionDTO> update(@PathVariable Long id, @Valid @RequestBody PropertyProtectionDTO dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a property protection")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}

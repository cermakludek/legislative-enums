package cz.intelis.legislativeenums.kso;

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
 * REST API controller for managing BuildingClassification entities.
 * Provides RESTful CRUD operations for building classification (KSO/JKSO).
 *
 * @author Legislative Codelists Team
 */
@RestController
@RequestMapping(value = "/api/v1/building-classifications", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
@RequiredArgsConstructor
@Tag(name = "Building Classifications (KSO)", description = "API for managing building object classifications (Klasifikace stavebních objektů)")
public class BuildingClassificationApiController {

    private final BuildingClassificationService service;

    /**
     * Retrieves all building classifications as flat list.
     */
    @GetMapping
    @Operation(summary = "Get all building classifications",
               description = "Returns flat list of all classifications.")
    public ResponseEntity<List<BuildingClassificationDTO>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    /**
     * Retrieves building classifications as hierarchical tree.
     */
    @GetMapping("/tree")
    @Operation(summary = "Get building classifications as tree",
               description = "Returns hierarchical tree structure starting from root items")
    public ResponseEntity<List<BuildingClassificationDTO>> findTree() {
        return ResponseEntity.ok(service.findTree());
    }

    /**
     * Retrieves root level items only.
     */
    @GetMapping("/roots")
    @Operation(summary = "Get root level items",
               description = "Returns only the top-level items (Obory)")
    public ResponseEntity<List<BuildingClassificationDTO>> findRoots() {
        return ResponseEntity.ok(service.findRootItems());
    }

    /**
     * Retrieves children of a specific parent.
     */
    @GetMapping("/{id}/children")
    @Operation(summary = "Get children of a classification",
               description = "Returns direct children of a specific classification")
    public ResponseEntity<List<BuildingClassificationDTO>> findChildren(@PathVariable Long id) {
        return ResponseEntity.ok(service.findChildren(id));
    }

    /**
     * Retrieves items by level.
     */
    @GetMapping("/level/{level}")
    @Operation(summary = "Get items by level",
               description = "Returns all items at a specific hierarchy level (1=Obor, 2=Skupina, 3=Podskupina)")
    public ResponseEntity<List<BuildingClassificationDTO>> findByLevel(@PathVariable Integer level) {
        return ResponseEntity.ok(service.findByLevel(level));
    }

    /**
     * Search classifications by code or name.
     */
    @GetMapping("/search")
    @Operation(summary = "Search classifications",
               description = "Search by code or name (case insensitive)")
    public ResponseEntity<List<BuildingClassificationDTO>> search(@RequestParam String query) {
        return ResponseEntity.ok(service.search(query));
    }

    /**
     * Retrieves a classification by its ID.
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get classification by ID",
               description = "Returns a single classification by its unique identifier")
    public ResponseEntity<BuildingClassificationDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    /**
     * Retrieves a classification by its unique code.
     */
    @GetMapping("/code/{code}")
    @Operation(summary = "Get classification by code",
               description = "Returns a single classification by its unique code (e.g., 801, 802)")
    public ResponseEntity<BuildingClassificationDTO> findByCode(@PathVariable String code) {
        return ResponseEntity.ok(service.findByCode(code));
    }

    /**
     * Creates a new classification.
     */
    @PostMapping
    @Operation(summary = "Create a new classification",
               description = "Creates a new building classification. Code must be unique.")
    public ResponseEntity<BuildingClassificationDTO> create(@Valid @RequestBody BuildingClassificationDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(dto));
    }

    /**
     * Updates an existing classification.
     */
    @PutMapping("/{id}")
    @Operation(summary = "Update an existing classification",
               description = "Updates an existing classification. Returns 404 if not found.")
    public ResponseEntity<BuildingClassificationDTO> update(@PathVariable Long id,
                                                            @Valid @RequestBody BuildingClassificationDTO dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    /**
     * Deletes a classification by its ID.
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a classification",
               description = "Permanently deletes a classification by ID. Cannot delete items with children.")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}

package com.example.demo.controllers;

import com.example.demo.entities.CheckListEntity;
import com.example.demo.services.CheckListService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.parameters.RequestBody;


@Tag(name = "CheckList", description = "Operations related to users' checklist")
@RestController
@RequestMapping("/checklists")
public class CheckListController {

    private final CheckListService checkListService;

    @Autowired
    public CheckListController(CheckListService checkListService) {
        this.checkListService = checkListService;
    }


    @Operation(
            summary = "Get all checklist items",
            description = "Returns a list of all checklist items registered in the system."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Checklist retrieved successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CheckListEntity.class))),
            @ApiResponse(responseCode = "204", description = "No checklist items found")
    })
    // Obtener todos los ítems del checklist
    @GetMapping
    public ResponseEntity<List<CheckListEntity>> getAllItems() {
        return ResponseEntity.ok(checkListService.findAll());
    }


    @Operation(
            summary = "Get a checklist item by ID",
            description = "Returns a specific checklist item by its ID if it exists.",
            parameters = {
                    @Parameter(name = "id", description = "ID of the checklist item to retrieve", required = true)
            }
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Checklist item found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CheckListEntity.class))),
            @ApiResponse(responseCode = "404", description = "Checklist item not found")
    })
    // Obtener un ítem por ID
    @GetMapping("/{id}")
    public ResponseEntity<CheckListEntity> getItemById(@PathVariable Long id) {
        CheckListEntity item = checkListService.findById(id);
        return ResponseEntity.ok(item);
    }

    @Operation(
            summary = "Create a new checklist item",
            description = "Creates a new checklist item for a user, including its text and status.",
            requestBody = @RequestBody(
                    description = "Checklist item data to be created",
                    required = true,
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CheckListEntity.class)))
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Checklist item successfully created"),
            @ApiResponse(responseCode = "400", description = "Invalid item data")
    })
    // Crear un nuevo ítem
    @PostMapping
    public ResponseEntity<CheckListEntity> createItem(@RequestBody @Valid CheckListEntity item) {
        if (item.getItem() == null || item.getItem().isBlank()) {
            throw new IllegalArgumentException("El ítem no puede estar vacío.");
        }
        checkListService.save(item);
        return ResponseEntity.status(HttpStatus.CREATED).body(item);
    }

    @Operation(
            summary = "Update a checklist item",
            description = "Updates an existing checklist item by its ID.",
            parameters = {
                    @Parameter(name = "id", description = "ID of the item to update", required = true)
            },
            requestBody = @RequestBody(
                    description = "Updated checklist item data",
                    required = true,
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CheckListEntity.class)))
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Checklist item successfully updated",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CheckListEntity.class))),
            @ApiResponse(responseCode = "404", description = "Checklist item not found")
    })
    // Actualizar un ítem
    @PutMapping("/{id}")
    public ResponseEntity<CheckListEntity> updateItem(@PathVariable Long id,
                                                      @RequestBody @Valid CheckListEntity updatedItem) {
        CheckListEntity existing = checkListService.findById(id);

        existing.setItem(updatedItem.getItem());
        existing.setStatus(updatedItem.isStatus());

        checkListService.save(existing);
        return ResponseEntity.ok(existing);
    }


    @Operation(
            summary = "Delete a checklist item",
            description = "Deletes a checklist item by its ID if it exists.",
            parameters = {
                    @Parameter(name = "id", description = "ID of the checklist item to delete", required = true)
            }
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Checklist item successfully deleted"),
            @ApiResponse(responseCode = "404", description = "Checklist item not found")
    })
    // Eliminar un ítem
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteItem(@PathVariable Long id) {
        CheckListEntity item = checkListService.findById(id);
        checkListService.delete(item);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Get checklist items by user ID",
            description = "Returns the list of checklist items associated with a specific user ID.",
            parameters = {
                    @Parameter(name = "userId", description = "ID of the user", required = true)
            }
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Checklist items retrieved successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CheckListEntity.class))),
            @ApiResponse(responseCode = "404", description = "User not found or has no items")
    })
    // Ver lista de CheckList por usuario
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<CheckListEntity>> getCheckListByUserId(@PathVariable Long userId) {
        List<CheckListEntity> items = checkListService.findByUserId(userId);
        return ResponseEntity.ok(items);
    }
}


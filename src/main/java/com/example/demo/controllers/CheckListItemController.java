package com.example.demo.controllers;

import com.example.demo.DTOs.CheckList.CheckListItemCreateDTO;
import com.example.demo.DTOs.CheckList.CheckListItemResponseDTO;
import com.example.demo.DTOs.CheckList.CheckListItemUpdateDTO;
import com.example.demo.controllers.hateoas.CheckListItemModelAssembler;
import com.example.demo.services.CheckListItemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Checklist Items", description = "Operations related to user travel checklist items")
@RestController
@RequestMapping("/checklist-items")
@RequiredArgsConstructor
public class CheckListItemController {

    private final CheckListItemService service;
    private final CheckListItemModelAssembler assembler;

    @Operation(summary = "Get all items", description = "Returns a list of all items from all checklists.")
    @ApiResponse(responseCode = "200", description = "Items successfully retrieved",
            content = @Content(schema = @Schema(implementation = CheckListItemResponseDTO.class)))
    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<CheckListItemResponseDTO>>> getAll() {
        List<CheckListItemResponseDTO> items = service.findAll();

        return ResponseEntity.ok(assembler.toCollectionModel(items));
    }


    @Operation(summary = "Get an item by ID", description = "Retrieves a checklist item by its unique ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Item found",
                    content = @Content(schema = @Schema(implementation = CheckListItemResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Item not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<CheckListItemResponseDTO>> getById(@PathVariable Long id) {
        CheckListItemResponseDTO checkListItem = service.findById(id);

        return ResponseEntity.ok(assembler.toModel(checkListItem));
    }

    @Operation(
            summary = "Create a new item",
            description = "Creates a new checklist item associated with a specific checklist.",
            requestBody = @RequestBody(
                    required = true,
                    description = "Data of the item to be created",
                    content = @Content(schema = @Schema(implementation = CheckListItemCreateDTO.class))
            )
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Item successfully created",
                    content = @Content(schema = @Schema(implementation = CheckListItemResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid data provided")
    })
    @PostMapping
    public ResponseEntity<CheckListItemResponseDTO> createItem(
            @org.springframework.web.bind.annotation.RequestBody @Valid CheckListItemCreateDTO dto) {

        CheckListItemResponseDTO createdItem = service.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdItem);
    }

    @Operation(
            summary = "Update an item",
            description = "Updates an existing checklist item using its ID and the provided updated data.",
            parameters = {
                    @Parameter(name = "id", description = "ID of the checklist item to update", required = true)
            },
            requestBody = @RequestBody(
                    required = true,
                    description = "Updated data for the item",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CheckListItemUpdateDTO.class)
                    )
            )
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Item successfully updated",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CheckListItemResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid data provided"),
            @ApiResponse(responseCode = "404", description = "Item or checklist not found")
    })
    @PutMapping("/{id}")
    public ResponseEntity<CheckListItemResponseDTO> update(
            @PathVariable Long id,
            @org.springframework.web.bind.annotation.RequestBody @Valid CheckListItemUpdateDTO dto) {

        CheckListItemResponseDTO updatedItem = service.update(id, dto);
        return ResponseEntity.ok(updatedItem);
    }

    @Operation(summary = "Delete an item", description = "Deletes a checklist item by its ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Item successfully deleted"),
            @ApiResponse(responseCode = "404", description = "Item not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
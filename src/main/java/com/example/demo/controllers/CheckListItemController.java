package com.example.demo.controllers;

import com.example.demo.DTOs.CheckList.Request.CheckListItemCreateDTO;
import com.example.demo.DTOs.CheckList.Response.CheckListItemResponseDTO;
import com.example.demo.DTOs.CheckList.CheckListItemUpdateDTO;
import com.example.demo.DTOs.Filter.CheckListItemFilterDTO;
import com.example.demo.controllers.hateoas.CheckListItemModelAssembler;
import com.example.demo.exceptions.OwnershipException;
import com.example.demo.security.entities.CredentialEntity;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Checklist Items", description = "Operations related to user travel checklist items")
@RestController
@RequestMapping("/checklist-items")
public class CheckListItemController {

    private final CheckListItemService service;
    private final CheckListItemModelAssembler assembler;
    private final PagedResourcesAssembler<CheckListItemResponseDTO> pagedResourcesAssembler;

    @Autowired
    public CheckListItemController(CheckListItemService service, CheckListItemModelAssembler assembler, PagedResourcesAssembler<CheckListItemResponseDTO> pagedResourcesAssembler) {
        this.service = service;
        this.assembler = assembler;
        this.pagedResourcesAssembler = pagedResourcesAssembler;
    }

    @Operation(
            summary = "Get all items",
            description = "Returns a list of all items from all checklists. You can optionally filter by completion status."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Items successfully retrieved",
                    content = @Content(schema = @Schema(implementation = CheckListItemResponseDTO.class))
            )
    })
    @PreAuthorize("hasAuthority('VER_TODOS_CHECKLISTITEM')")
    @GetMapping
    public ResponseEntity<PagedModel<EntityModel<CheckListItemResponseDTO>>> getAll(
            Pageable pageable,
            @RequestParam(required = false) Boolean completed) {

        Page<CheckListItemResponseDTO> items;
        PagedModel<EntityModel<CheckListItemResponseDTO>> model;

        if (completed != null) {
            items = service.findByStatus(completed, pageable);
        } else {
            items = service.findAll(pageable);
        }

        model = pagedResourcesAssembler.toModel(items, assembler);

        return ResponseEntity.ok(model);
    }


    @Operation(summary = "Get an item by ID", description = "Retrieves a checklist item by its unique ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Item found",
                    content = @Content(schema = @Schema(implementation = CheckListItemResponseDTO.class))),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "Item not found")
    })
    @PreAuthorize("hasAuthority('VER_CHECKLISTITEM')")
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<CheckListItemResponseDTO>> getById(
            @PathVariable Long id,
            @AuthenticationPrincipal CredentialEntity credential) {

        Long userId = credential.getUser().getId();

        CheckListItemResponseDTO checkListItem = service.findByIdIfOwned(id, userId);
        return ResponseEntity.ok(assembler.toModel(checkListItem));
    }


    @Operation(summary = "Get all checklist items by user ID", description = "Returns all checklist items for the authenticated user.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Items retrieved successfully",
                    content = @Content(schema = @Schema(implementation = CheckListItemResponseDTO.class))),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "User not found or no items")
    })
    @PreAuthorize("hasAuthority('VER_CHECKLISTITEM_USER')")
    @GetMapping("/user/{userId}")
    public ResponseEntity<PagedModel<EntityModel<CheckListItemResponseDTO>>> getItemsByUserId(
            @PathVariable Long userId,
            @ModelAttribute CheckListItemFilterDTO filters,
            @AuthenticationPrincipal CredentialEntity credential,
            Pageable pageable) {

        if (!credential.getUser().getId().equals(userId)) {
            throw new OwnershipException("No tienes permiso para acceder a este recurso.");
        }

        Page<CheckListItemResponseDTO> items = service.findByUserIdWithFilters(userId, filters, pageable);
        PagedModel<EntityModel<CheckListItemResponseDTO>> model = pagedResourcesAssembler.toModel(items, assembler);
        return ResponseEntity.ok(model);
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
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "400", description = "Invalid data provided")
    })
    @PreAuthorize("hasAuthority('CREAR_CHECKLISTITEM')")
    @PostMapping
    public ResponseEntity<CheckListItemResponseDTO> createItem(
            @org.springframework.web.bind.annotation.RequestBody @Valid CheckListItemCreateDTO dto,
            @AuthenticationPrincipal CredentialEntity credential) {

        Long userId = credential.getUser().getId();

        CheckListItemResponseDTO createdItem = service.create(dto, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdItem);
    }


    @Operation(
            summary = "Update an item",
            description = "Updates an existing checklist item using its ID and the provided updated data."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Item successfully updated",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CheckListItemResponseDTO.class))),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "Item or checklist not found")
    })
    @PreAuthorize("hasAuthority('MODIFICAR_CHECKLISTITEM')")
    @PutMapping("/{id}")
    public ResponseEntity<CheckListItemResponseDTO> update(
            @PathVariable Long id,
            @org.springframework.web.bind.annotation.RequestBody @Valid CheckListItemUpdateDTO dto,
            @AuthenticationPrincipal CredentialEntity credential) {

        Long userId = credential.getUser().getId();
        CheckListItemResponseDTO updatedItem = service.updateIfOwned(id, dto, userId);
        return ResponseEntity.ok(updatedItem);
    }

    @Operation(summary = "Delete an item", description = "Deletes a checklist item by its ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Item successfully deleted"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "Item not found")
    })
    @PreAuthorize("hasAuthority('ELIMINAR_CHECKLISTITEM')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id,
            @AuthenticationPrincipal CredentialEntity credential) {

        Long userId = credential.getUser().getId();
        service.deleteIfOwned(id, userId);
        return ResponseEntity.noContent().build();
    }

}
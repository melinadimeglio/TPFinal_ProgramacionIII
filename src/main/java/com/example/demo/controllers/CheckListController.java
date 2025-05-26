package com.example.demo.controllers;

import com.example.demo.DTOs.CheckList.CheckListCreateDTO;
import com.example.demo.DTOs.CheckList.CheckListResponseDTO;
import com.example.demo.DTOs.CheckList.CheckListUpdateDTO;
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
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.parameters.RequestBody;


@Tag(name = "Checklists", description = "Operations related to user travel checklists")
@RestController
@RequestMapping("/checklists")
@RequiredArgsConstructor
public class CheckListController {

    private final CheckListService checkListService;

    @Operation(
            summary = "Create a new checklist",
            description = "Creates a new checklist for a specific user and trip.",
            requestBody = @RequestBody(
                    required = true,
                    description = "Checklist data",
                    content = @Content(schema = @Schema(implementation = CheckListCreateDTO.class))
            )
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Checklist successfully created",
                    content = @Content(schema = @Schema(implementation = CheckListResponseDTO.class))
            ),
            @ApiResponse(responseCode = "400", description = "Invalid data")
    })
    @PostMapping
    public ResponseEntity<CheckListResponseDTO> create(
            @org.springframework.web.bind.annotation.RequestBody @Valid CheckListCreateDTO dto) {
        CheckListResponseDTO created = checkListService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @Operation(
            summary = "Update a checklist by ID",
            description = "Updates a checklist with new data, including its name, user, trip and status.",
            requestBody = @RequestBody(
                    required = true,
                    description = "Checklist update data",
                    content = @Content(schema = @Schema(implementation = CheckListUpdateDTO.class))
            )
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Checklist updated successfully",
                    content = @Content(schema = @Schema(implementation = CheckListResponseDTO.class))
            ),
            @ApiResponse(responseCode = "404", description = "Checklist not found"),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    @PutMapping("/{id}")
    public ResponseEntity<CheckListResponseDTO> update(
            @PathVariable Long id,
            @org.springframework.web.bind.annotation.RequestBody @Valid CheckListUpdateDTO dto) {
        CheckListResponseDTO updated = checkListService.update(id, dto);
        return ResponseEntity.ok(updated);
    }

    @Operation(summary = "Get a checklist by ID", description = "Retrieves a checklist by its unique identifier.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Checklist found",
                    content = @Content(schema = @Schema(implementation = CheckListResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Checklist not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<CheckListResponseDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(checkListService.findById(id));
    }

    @Operation(summary = "Get all checklists", description = "Returns all checklists in the system.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of checklists",
                    content = @Content(schema = @Schema(implementation = CheckListResponseDTO.class)))
    })
    @GetMapping
    public ResponseEntity<List<CheckListResponseDTO>> getAll() {
        return ResponseEntity.ok(checkListService.findAll());
    }

    @Operation(summary = "Get all checklists by user ID", description = "Retrieves all checklists created by the specified user.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of checklists for the user",
                    content = @Content(schema = @Schema(implementation = CheckListResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "User not found or has no checklists")
    })
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<CheckListResponseDTO>> getByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(checkListService.findByUserId(userId));
    }

    @Operation(summary = "Delete a checklist by ID", description = "Deletes the specified checklist and all its items.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Checklist deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Checklist not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        checkListService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
package com.example.demo.controllers;

import com.example.demo.DTOs.Expense.Response.ExpenseResponseDTO;
import com.example.demo.DTOs.Filter.ItineraryFilterDTO;
import com.example.demo.DTOs.GlobalError.ErrorResponseDTO;
import com.example.demo.DTOs.Itinerary.Request.ItineraryCreateDTO;
import com.example.demo.DTOs.Itinerary.Response.ItineraryResponseDTO;
import com.example.demo.DTOs.Itinerary.ItineraryUpdateDTO;
import com.example.demo.controllers.hateoas.ItineraryModelAssembler;
import com.example.demo.exceptions.OwnershipException;
import com.example.demo.mappers.ItineraryMapper;
import com.example.demo.security.entities.CredentialEntity;
import com.example.demo.services.ItineraryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
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

@Tag(name = "Itineraries", description = "Operations related to user itineraries")
@RestController
@RequestMapping("/itineraries")
public class ItineraryController {

    private final ItineraryService itineraryService;
    private final ItineraryModelAssembler assembler;
    private final PagedResourcesAssembler<ItineraryResponseDTO> pagedResourcesAssembler;

    @Autowired
    public ItineraryController(ItineraryService itineraryService, ItineraryModelAssembler assembler, PagedResourcesAssembler<ItineraryResponseDTO> pagedResourcesAssembler) {
        this.itineraryService = itineraryService;
        this.assembler = assembler;
        this.pagedResourcesAssembler = pagedResourcesAssembler;
    }

    @Operation(
            summary = "Create a new itinerary",
            description = "Creates a new itinerary by providing the itinerary date, optional notes, and trip ID.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Itinerary details to be created",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ItineraryCreateDTO.class)
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Itinerary successfully created",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ItineraryResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid input data",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Access denied",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Trip not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class)
                    )
            )
    })
    @PostMapping
    @PreAuthorize("hasAuthority('CREAR_ITINERARIO')")
    public ResponseEntity<ItineraryResponseDTO> createItinerary(
            @RequestBody @Valid ItineraryCreateDTO dto,
            @AuthenticationPrincipal CredentialEntity credential) {

        Long myUserId = credential.getUser().getId();
        ItineraryResponseDTO response = itineraryService.save(dto, myUserId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(
            summary = "Get all itineraries",
            description = "Retrieves a list of all itineraries available in the system, including their date, notes, user, trip, and associated activities."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "List of itineraries retrieved successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ItineraryResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request parameters",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - user not authenticated",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden - insufficient permissions",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class)
                    )
            )
    })
    @PreAuthorize("hasAuthority('VER_ITINERARIOS')")
    @GetMapping
    public ResponseEntity<PagedModel<EntityModel<ItineraryResponseDTO>>> getAllItineraries(Pageable pageable) {
        Page<ItineraryResponseDTO> itineraries = itineraryService.findAll(pageable);
        PagedModel<EntityModel<ItineraryResponseDTO>> model = pagedResourcesAssembler.toModel(itineraries, assembler);
        return ResponseEntity.ok(model);
    }

    @Operation(
            summary = "Get all inactive itineraries",
            description = "Retrieves a paginated list of all inactive itineraries."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Itineraries retrieved successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ItineraryResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - user not authenticated",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden - insufficient permissions",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request parameters",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class)
                    )
            )
    })
    @PreAuthorize("hasAuthority('VER_ITINERARIOS')")
    @GetMapping("/inactive")
    public ResponseEntity<PagedModel<EntityModel<ItineraryResponseDTO>>> getAllItinerariesInactive(Pageable pageable) {
        Page<ItineraryResponseDTO> itineraries = itineraryService.findAllInactive(pageable);
        PagedModel<EntityModel<ItineraryResponseDTO>> model = pagedResourcesAssembler.toModel(itineraries, assembler);
        return ResponseEntity.ok(model);
    }


    @Operation(
            summary = "Get itinerary by ID",
            description = "Retrieves a specific itinerary by its unique identifier, only if it belongs to the authenticated user."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Itinerary found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ItineraryResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Access denied",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Itinerary not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class)
                    )
            )
    })
    @PreAuthorize("hasAuthority('VER_ITINERARIO')")
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<ItineraryResponseDTO>> getItineraryById(
            @PathVariable Long id,
            @AuthenticationPrincipal CredentialEntity credential) {

        Long userId = credential.getUser().getId();
        boolean isAdmin = credential.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        ItineraryResponseDTO itineraryResponseDTO;

        if (isAdmin) {
            itineraryResponseDTO = itineraryService.findById(id);
        } else {
            itineraryResponseDTO = itineraryService.findByIdIfBelongsToUser(id, userId);
        }

        return ResponseEntity.ok(assembler.toModel(itineraryResponseDTO));
    }



    @Operation(
            summary = "Get itineraries by user ID",
            description = "Retrieves all itineraries associated with a specific user. Allows filtering by date and destination.",
            parameters = {
                    @Parameter(name = "userId", description = "ID of the user whose itineraries are to be retrieved", required = true, example = "1")
            }
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Itineraries retrieved successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ItineraryResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden - User not authorized to access this resource",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request parameters",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
            )
    })
    @PreAuthorize("hasAuthority('VER_ITINERARIO_USUARIO')")
    @GetMapping("/user/{userId}")
    public ResponseEntity<PagedModel<EntityModel<ItineraryResponseDTO>>> getItinerariesByUserId(
            Pageable pageable,
            @PathVariable Long userId,
            @ModelAttribute ItineraryFilterDTO filters,
            @AuthenticationPrincipal CredentialEntity credential) {

        if (credential.getUser() == null || !credential.getUser().getId().equals(userId)) {
            throw new OwnershipException("You do not have permission to access this resource.");
        }

        Page<ItineraryResponseDTO> itineraries = itineraryService.findByUserIdWithFilters(userId, filters, pageable);
        PagedModel<EntityModel<ItineraryResponseDTO>> model = pagedResourcesAssembler.toModel(itineraries, assembler);
        return ResponseEntity.ok(model);
    }

    @Operation(
            summary = "Update an existing itinerary",
            description = "Updates the details of an itinerary by its ID. Only the owner can update it."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Itinerary updated successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ItineraryResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid input data",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Access denied",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Itinerary not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class)
                    )
            )
    })
    @PreAuthorize("hasAuthority('MODIFICAR_ITINERARIO')")
    @PutMapping("/{id}")
    public ResponseEntity<ItineraryResponseDTO> updateItinerary(
            @PathVariable Long id,
            @RequestBody @Valid ItineraryUpdateDTO dto,
            @AuthenticationPrincipal CredentialEntity credential
    ) {
        Long userId = credential.getUser().getId();
        ItineraryResponseDTO updated = itineraryService.updateAndReturnIfOwned(id, dto, userId);
        return ResponseEntity.ok(updated);
    }


    @Operation(
            summary = "Delete an itinerary by ID",
            description = "Deletes (soft delete) an itinerary by its ID, only if it belongs to the authenticated user."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Itinerary deleted successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request data",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Access denied",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Itinerary not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class)
                    )
            )
    })
    @PreAuthorize("hasAuthority('ELIMINAR_ITINERARIO')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteItinerary(
            @PathVariable Long id,
            @AuthenticationPrincipal CredentialEntity credential
    ) {
        Long userId = credential.getUser().getId();
        itineraryService.softDeleteIfOwned(id, userId);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Restore an itinerary",
            description = "Reactivates a previously deleted itinerary (soft-deleted) only if it belongs to the authenticated user."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Itinerary restored successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request data",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Access denied",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Itinerary not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class)
                    )
            )
    })
    @PreAuthorize("hasAuthority('RESTAURAR_ITINERARIO')")
    @PutMapping("/restore/{id}")
    public ResponseEntity<Void> restoreItinerary(
            @PathVariable Long id,
            @AuthenticationPrincipal CredentialEntity credential
    ) {
        Long userId = credential.getUser().getId();
        itineraryService.restoreIfOwned(id, userId);
        return ResponseEntity.noContent().build();
    }
}

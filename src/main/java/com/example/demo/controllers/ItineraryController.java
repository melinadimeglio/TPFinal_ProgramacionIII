package com.example.demo.controllers;

import com.example.demo.DTOs.Itinerary.Request.ItineraryCreateDTO;
import com.example.demo.DTOs.Itinerary.Response.ItineraryResponseDTO;
import com.example.demo.DTOs.Itinerary.ItineraryUpdateDTO;
import com.example.demo.mappers.ItineraryMapper;
import com.example.demo.services.ItineraryService;
import io.swagger.v3.oas.annotations.Operation;
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

@Tag(name = "Itineraries", description = "Operations related to user itineraries")
@RestController
@RequestMapping("/itineraries")
public class ItineraryController {

    private final ItineraryService itineraryService;
    private final ItineraryMapper itineraryMapper;

    @Autowired
    public ItineraryController(ItineraryService itineraryService, ItineraryMapper itineraryMapper) {
        this.itineraryService = itineraryService;
        this.itineraryMapper = itineraryMapper;
    }

    @Operation(
            summary = "Create a new itinerary",
            description = "This endpoint allows a user to create a new itinerary by providing the itinerary date, optional notes, user ID, and trip ID.",
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
                    description = "Invalid input data"
            )
    })
    @PostMapping
    public ResponseEntity<ItineraryResponseDTO> createItinerary(@RequestBody @Valid ItineraryCreateDTO dto) {
        ItineraryResponseDTO response = itineraryService.save(dto);
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
                            mediaType = "application/json"
                    )
            )
    })
    @GetMapping
    public ResponseEntity<List<ItineraryResponseDTO>> getAllItineraries() {
        return ResponseEntity.ok(itineraryService.findAll());
    }

    @Operation(
            summary = "Get itinerary by ID",
            description = "Retrieves a specific itinerary by its unique identifier. Returns 404 if not found.",
            parameters = {
                    @io.swagger.v3.oas.annotations.Parameter(
                            name = "id",
                            description = "ID of the itinerary to retrieve",
                            required = true,
                            example = "1"
                    )
            }
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
                    responseCode = "404",
                    description = "Itinerary not found"
            )
    })
    @GetMapping("/{id}")
    public ResponseEntity<ItineraryResponseDTO> getItineraryById(@PathVariable Long id) {
        ItineraryResponseDTO response = itineraryService.findById(id);
        return ResponseEntity.ok(response);
    }


    @Operation(
            summary = "Get itineraries by user ID",
            description = "Retrieves all itineraries associated with a specific user. Returns an empty list if the user has no itineraries.",
            parameters = {
                    @io.swagger.v3.oas.annotations.Parameter(
                            name = "userId",
                            description = "ID of the user whose itineraries are to be retrieved",
                            required = true,
                            example = "1"
                    )
            }
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Itineraries retrieved successfully",
                    content = @Content(
                            mediaType = "application/json"
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User not found"
            )
    })
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ItineraryResponseDTO>> getItinerariesByUserId(@PathVariable Long userId) {
        List<ItineraryResponseDTO> responses = itineraryService.findByUserId(userId);
        return ResponseEntity.ok(responses);
    }

    @Operation(
            summary = "Update an existing itinerary",
            description = "Updates the details of an itinerary by its ID. Returns the updated itinerary data."
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
                    description = "Invalid input data"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Itinerary not found"
            )
    })
    @PutMapping("/{id}")
    public ResponseEntity<ItineraryResponseDTO> updateItinerary(
            @PathVariable Long id,
            @RequestBody @Valid ItineraryUpdateDTO dto
    ) {
        ItineraryResponseDTO updated = itineraryService.updateAndReturn(id, dto);
        return ResponseEntity.ok(updated);
    }

    @Operation(
            summary = "Delete an itinerary by ID",
            description = "Deletes an itinerary from the system using its unique ID."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Itinerary deleted successfully. No content is returned in the response."
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Itinerary not found"
            )
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteItinerary(@PathVariable Long id) {
        itineraryService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
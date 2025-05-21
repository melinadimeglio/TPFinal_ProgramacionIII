package com.example.demo.controllers;

import com.example.demo.DTOs.Activity.ActivityResponseDTO;
import com.example.demo.DTOs.Itinerary.ItineraryCreateDTO;
import com.example.demo.DTOs.Itinerary.ItineraryResponseDTO;
import com.example.demo.DTOs.Itinerary.ItineraryUpdateDTO;
import com.example.demo.DTOs.User.UserResponse;
import com.example.demo.entities.ItineraryEntity;
import com.example.demo.mappers.ItineraryMapper;
import com.example.demo.mappers.UserMapper;
import com.example.demo.services.ItineraryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;


import java.util.List;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Itineraries", description = "Operations related to travel itineraries")
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

    @GetMapping
    @Operation(
            summary = "Get all itineraries",
            description = "Returns a list of all itineraries registered in the system."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of itineraries successfully retrieved",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ItineraryResponseDTO.class))),
            @ApiResponse(responseCode = "204", description = "No itineraries found")
    })
    public ResponseEntity<List<ItineraryResponseDTO>> getAllItineraries() {
        List<ItineraryResponseDTO> itineraries = itineraryService.findAll();
        if (itineraries.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(itineraries);
    }


    @Operation(
            summary = "Get itinerary by ID",
            description = "Returns a specific itinerary based on the provided ID.",
            parameters = {
                    @Parameter(name = "id", description = "ID of the itinerary to retrieve", required = true)
            }
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Itinerary found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ItineraryResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Itinerary not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ItineraryResponseDTO> getItineraryById(@PathVariable Long id) {
        return ResponseEntity.ok(itineraryService.findById(id));
    }

    @Operation(
            summary = "Get itineraries by user ID",
            description = "Retrieves all itineraries associated with a specific user."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Itineraries retrieved successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ItineraryResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "User not found or no itineraries for user"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ItineraryResponseDTO>> findByUserId(@PathVariable Long userId) {
        List<ItineraryResponseDTO> activity = itineraryService.findByUserId(userId);
        return ResponseEntity.ok(activity);
    }

    @Operation(
            summary = "Create a new itinerary",
            description = "Creates a new itinerary with a date, time and optional notes.",
            requestBody = @RequestBody(
                    description = "Itinerary data to be created",
                    required = true,
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ItineraryCreateDTO.class)))
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Itinerary successfully created"),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    @PostMapping
    public ResponseEntity<ItineraryResponseDTO> createUser(@RequestBody @Valid ItineraryCreateDTO itinerary) {
        ItineraryEntity itineraryEntity = itineraryMapper.toEntity(itinerary);
        System.out.println(itinerary);
        itineraryService.save(itineraryEntity);
        ItineraryResponseDTO response = itineraryMapper.toDTO(itineraryEntity);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }


    @Operation(
            summary = "Update an existing itinerary",
            description = "Updates the date, time and notes of an existing itinerary.",
            parameters = {
                    @Parameter(name = "id", description = "ID of the itinerary to update", required = true)
            },
            requestBody = @RequestBody(
                    description = "Updated itinerary data",
                    required = true,
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ItineraryUpdateDTO.class)))
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Itinerary successfully updated",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ItineraryResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Itinerary not found")
    })
    // Actualizar un itinerario
    @PutMapping("/{id}")
    public ResponseEntity<ItineraryResponseDTO> updateItinerary(@PathVariable Long id,
                                                           @RequestBody @Valid ItineraryUpdateDTO dto) {
        itineraryService.update(id, dto);
        return ResponseEntity.ok(itineraryService.findById(id));
    }


    @Operation(
            summary = "Delete an itinerary",
            description = "Deletes the itinerary that matches the provided ID.",
            parameters = {
                    @Parameter(name = "id", description = "ID of the itinerary to delete", required = true)
            }
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Itinerary successfully deleted"),
            @ApiResponse(responseCode = "404", description = "Itinerary not found")
    })
    // Eliminar un itinerario
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteItinerary(@PathVariable Long id) {
        itineraryService.delete(id);
        return ResponseEntity.noContent().build();
    }
}


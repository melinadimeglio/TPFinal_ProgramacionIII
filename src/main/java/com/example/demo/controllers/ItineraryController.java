package com.example.demo.controllers;

import com.example.demo.entities.ItineraryEntity;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Itineraries", description = "Operations related to travel itineraries")
@RestController
@RequestMapping("/itineraries")
public class ItineraryController {

    private final ItineraryService itineraryService;

    @Autowired
    public ItineraryController(ItineraryService itineraryService) {
        this.itineraryService = itineraryService;
    }


    @Operation(
            summary = "Get all itineraries",
            description = "Returns a list of all itineraries registered in the system."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of itineraries successfully retrieved",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ItineraryEntity.class))),
            @ApiResponse(responseCode = "204", description = "No itineraries found")
    })
    // Obtener todos los itinerarios
    @GetMapping
    public ResponseEntity<List<ItineraryEntity>> getAllItineraries() {
        return ResponseEntity.ok(itineraryService.findAll());
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
                            schema = @Schema(implementation = ItineraryEntity.class))),
            @ApiResponse(responseCode = "404", description = "Itinerary not found")
    })
    // Obtener un itinerario por ID
    @GetMapping("/{id}")
    public ResponseEntity<ItineraryEntity> getItineraryById(@PathVariable Long id) {
        ItineraryEntity itinerary = itineraryService.findById(id);
        return ResponseEntity.ok(itinerary);
    }

    @Operation(
            summary = "Create a new itinerary",
            description = "Creates a new itinerary with a date, time and optional notes.",
            requestBody = @RequestBody(
                    description = "Itinerary data to be created",
                    required = true,
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ItineraryEntity.class)))
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Itinerary successfully created"),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    // Crear un nuevo itinerario
    @PostMapping
    public ResponseEntity<ItineraryEntity> createItinerary(@RequestBody ItineraryEntity itinerary) {
        if (itinerary.getDate() == null) {
            throw new IllegalArgumentException("La fecha no puede estar vacía.");
        }
        itineraryService.save(itinerary);
        return ResponseEntity.status(HttpStatus.CREATED).body(itinerary);
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
                            schema = @Schema(implementation = ItineraryEntity.class)))
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Itinerary successfully updated",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ItineraryEntity.class))),
            @ApiResponse(responseCode = "404", description = "Itinerary not found")
    })
    // Actualizar un itinerario
    @PutMapping("/{id}")
    public ResponseEntity<ItineraryEntity> updateItinerary(@PathVariable Long id,
                                                           @RequestBody ItineraryEntity updatedItinerary) {
        ItineraryEntity existing = itineraryService.findById(id);

        existing.setDate(updatedItinerary.getDate());
        existing.setTime(updatedItinerary.getTime());
        existing.setNotes(updatedItinerary.getNotes());

        itineraryService.save(existing);
        return ResponseEntity.ok(existing);
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
        ItineraryEntity itinerary = itineraryService.findById(id);
        itineraryService.delete(itinerary);
        return ResponseEntity.noContent().build();
    }
}


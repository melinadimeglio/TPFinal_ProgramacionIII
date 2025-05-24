package com.example.demo.controllers;

import com.example.demo.DTOs.RecommendationDTO;
import com.example.demo.DTOs.Trip.TripCreateDTO;
import com.example.demo.DTOs.Trip.TripResponseDTO;
import com.example.demo.DTOs.Trip.TripUpdateDTO;
import com.example.demo.entities.TripEntity;
import com.example.demo.services.RecommendationService;
import com.example.demo.services.TripService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Trips", description = "Operations related to users trips")
@RestController
@RequestMapping("/trips")
public class TripController {

    private final TripService tripService;
    private final RecommendationService recommendationService;

    public TripController(TripService tripService, RecommendationService recommendationService) {
        this.tripService = tripService;
        this.recommendationService = recommendationService;
    }

    @Autowired


    @Operation(summary = "Get all trips", description = "Returns a list of all trips.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of trips retrieved successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = TripResponseDTO.class)))
    })
    @GetMapping
    public ResponseEntity<List<TripResponseDTO>> getAllTrips() {
        return ResponseEntity.ok(tripService.findAll());
    }

    @Operation(summary = "Get a trip by ID", description = "Returns a specific trip by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Trip found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = TripResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Trip not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<TripResponseDTO> getTripById(
            @Parameter(description = "ID of the trip to retrieve", required = true)
            @PathVariable Long id) {
        return ResponseEntity.ok(tripService.findById(id));
    }


    @Operation(summary = "Create a new trip", description = "Creates a new trip and returns the created trip.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Trip created successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = TripResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    @PostMapping
    public ResponseEntity<TripResponseDTO> createTrip(
            @RequestBody(description = "Data for the new trip", required = true,
                    content = @Content(schema = @Schema(implementation = TripCreateDTO.class)))
            @org.springframework.web.bind.annotation.RequestBody @Valid TripCreateDTO tripCreateDTO) {

        TripResponseDTO responseDTO = tripService.save(tripCreateDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    }


    @Operation(
            summary = "Get trips by user ID",
            description = "Returns all trips associated with a specific user ID."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Trips found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = TripResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User not found"
            )
    })
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<TripResponseDTO>> getTripsByUserId(
            @Parameter(description = "ID of the user whose trips will be retrieved", required = true)
            @PathVariable Long userId) {
        List<TripResponseDTO> trips = tripService.findByUserId(userId);
        return ResponseEntity.ok(trips);
    }


    @Operation(summary = "Update a trip by ID", description = "Updates a trip by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Trip updated successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = TripResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Trip not found"),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    @PutMapping("/{id}")
    public ResponseEntity<TripResponseDTO> updateTrip(
            @Parameter(description = "ID of the trip to update", required = true)
            @PathVariable Long id,
            @RequestBody(description = "Updated trip data", required = true,
                    content = @Content(schema = @Schema(implementation = TripUpdateDTO.class)))
            @org.springframework.web.bind.annotation.RequestBody @Valid TripUpdateDTO tripUpdateDTO) {

        TripResponseDTO updatedTrip = tripService.update(id, tripUpdateDTO);
        return ResponseEntity.ok(updatedTrip);
    }


    @Operation(summary = "Delete a trip by ID", description = "Deletes a trip by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Trip deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Trip not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTrip(
            @Parameter(description = "ID of the trip to delete", required = true)
            @PathVariable Long id) {

        tripService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Obtener recomendaciones de actividades para un viaje",
            description = "Devuelve una lista de actividades sugeridas en base al destino, fechas u otros datos del viaje especificado."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Lista de recomendaciones generada exitosamente",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = RecommendationDTO.class))
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Viaje no encontrado"
            )
    })
    @GetMapping("/{tripId}/recommendations")
    public ResponseEntity<List<RecommendationDTO>> getRecommendations(@PathVariable Long tripId){
        List<RecommendationDTO> recomemendations = recommendationService.getRecommendationsForTrip(tripId);
        return ResponseEntity.ok(recomemendations);
    }

}



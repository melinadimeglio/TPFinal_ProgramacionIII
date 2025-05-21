package com.example.demo.controllers;

import com.example.demo.entities.TripEntity;
import com.example.demo.services.TripService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/trips")
public class TripController {

    private final TripService tripService;

    @Autowired
    public TripController(TripService tripService) {
        this.tripService = tripService;
    }


    @Operation(
            summary = "Get all trips",
            description = "Returns a list of all trips registered in the system."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of trips retrieved successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = TripEntity.class)))
    })
    @GetMapping
    public ResponseEntity<List<TripEntity>> getAllTrips() {
        return ResponseEntity.ok(tripService.findAll());
    }


    @Operation(
            summary = "Get a trip by ID",
            description = "Returns a specific trip by its ID if it exists.",
            parameters = {
                    @Parameter(name = "id", description = "ID of the trip to retrieve", required = true)
            }
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Trip found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = TripEntity.class))),
            @ApiResponse(responseCode = "404", description = "Trip not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<TripEntity> getTripById(@PathVariable Long id) {
        TripEntity trip = tripService.findById(id);
        return ResponseEntity.ok(trip);
    }


    @Operation(
            summary = "Create a new trip",
            description = "Creates a new trip, including destination, dates, estimated budget, number of passengers and status.",
            requestBody = @RequestBody(
                    description = "Data of the new trip to register",
                    required = true,
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = TripEntity.class))
            )
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Trip created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    @PostMapping
    public ResponseEntity<TripEntity> createTrip(@RequestBody TripEntity trip) {
        if (trip.getDestination() == null || trip.getDestination().isBlank()) {
            throw new IllegalArgumentException("El destino no puede estar vacío.");
        }
        tripService.save(trip);
        return ResponseEntity.status(HttpStatus.CREATED).body(trip);
    }

    @Operation(
            summary = "Update a trip by ID",
            description = "Updates the information of a registered trip using its ID and the new provided data.",
            parameters = {
                    @Parameter(name = "id", description = "ID of the trip to update", required = true)
            },
            requestBody = @RequestBody(
                    description = "Updated data of the trip",
                    required = true,
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = TripEntity.class))
            )
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Trip updated successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = TripEntity.class))),
            @ApiResponse(responseCode = "404", description = "Trip not found"),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    @PutMapping("/{id}")
    public ResponseEntity<TripEntity> updateTrip(@PathVariable Long id,
                                                 @RequestBody TripEntity updatedTrip) {
        TripEntity existing = tripService.findById(id);

        existing.setDestination(updatedTrip.getDestination());
        existing.setStartDate(updatedTrip.getStartDate());
        existing.setEndDate(updatedTrip.getEndDate());
        existing.setEstimatedBudget(updatedTrip.getEstimatedBudget());
        existing.setPassengers(updatedTrip.getPassengers());
        existing.setActive(updatedTrip.isActive());

        tripService.save(existing);
        return ResponseEntity.ok(existing);
    }


    @Operation(
            summary = "Delete a trip by ID",
            description = "Deletes the trip corresponding to the provided ID if it exists.",
            parameters = {
                    @Parameter(name = "id", description = "ID of the trip to delete", required = true)
            }
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Trip deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Trip not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTrip(@PathVariable Long id) {
        TripEntity trip = tripService.findById(id);
        tripService.delete(trip);
        return ResponseEntity.noContent().build();
    }
}


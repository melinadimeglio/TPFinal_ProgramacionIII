package com.example.demo.controllers;

import com.example.demo.entities.TripEntity;
import com.example.demo.services.TripService;
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

    // Obtener todos los viajes
    @GetMapping
    public ResponseEntity<List<TripEntity>> getAllTrips() {
        return ResponseEntity.ok(tripService.findAll());
    }

    // Obtener un viaje por ID
    @GetMapping("/{id}")
    public ResponseEntity<TripEntity> getTripById(@PathVariable Long id) {
        TripEntity trip = tripService.findById(id);
        return ResponseEntity.ok(trip);
    }

    // Crear un nuevo viaje
    @PostMapping
    public ResponseEntity<TripEntity> createTrip(@RequestBody TripEntity trip) {
        if (trip.getDestination() == null || trip.getDestination().isBlank()) {
            throw new IllegalArgumentException("El destino no puede estar vacío.");
        }
        tripService.save(trip);
        return ResponseEntity.status(HttpStatus.CREATED).body(trip);
    }

    // Actualizar un viaje existente
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

    // Eliminar un viaje
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTrip(@PathVariable Long id) {
        TripEntity trip = tripService.findById(id);
        tripService.delete(trip);
        return ResponseEntity.noContent().build();
    }
}


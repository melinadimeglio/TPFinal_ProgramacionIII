package com.example.demo.controllers;

import com.example.demo.entities.ItineraryEntity;
import com.example.demo.services.ItineraryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/itineraries")
public class ItineraryController {

    private final ItineraryService itineraryService;

    @Autowired
    public ItineraryController(ItineraryService itineraryService) {
        this.itineraryService = itineraryService;
    }

    // Obtener todos los itinerarios
    @GetMapping
    public ResponseEntity<List<ItineraryEntity>> getAllItineraries() {
        return ResponseEntity.ok(itineraryService.findAll());
    }

    // Obtener un itinerario por ID
    @GetMapping("/{id}")
    public ResponseEntity<ItineraryEntity> getItineraryById(@PathVariable Long id) {
        ItineraryEntity itinerary = itineraryService.findById(id);
        return ResponseEntity.ok(itinerary);
    }

    // Crear un nuevo itinerario
    @PostMapping
    public ResponseEntity<ItineraryEntity> createItinerary(@RequestBody ItineraryEntity itinerary) {
        if (itinerary.getDate() == null) {
            throw new IllegalArgumentException("La fecha no puede estar vacía.");
        }
        itineraryService.save(itinerary);
        return ResponseEntity.status(HttpStatus.CREATED).body(itinerary);
    }

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

    // Eliminar un itinerario
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteItinerary(@PathVariable Long id) {
        ItineraryEntity itinerary = itineraryService.findById(id);
        itineraryService.delete(itinerary);
        return ResponseEntity.noContent().build();
    }
}


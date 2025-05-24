package com.example.demo.controllers;

import com.example.demo.DTOs.Itinerary.ItineraryCreateDTO;
import com.example.demo.DTOs.Itinerary.ItineraryResponseDTO;
import com.example.demo.entities.ItineraryEntity;
import com.example.demo.mappers.ItineraryMapper;
import com.example.demo.services.ItineraryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping
    public ResponseEntity<ItineraryResponseDTO> createItinerary(@RequestBody @Valid ItineraryCreateDTO dto) {
        System.out.println("🟢 DTO recibido: " + dto);

        ItineraryEntity entity = itineraryMapper.toEntity(dto);
        itineraryService.save(entity);
        ItineraryResponseDTO response = itineraryMapper.toDTO(entity);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}

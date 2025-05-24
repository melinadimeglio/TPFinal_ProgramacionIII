package com.example.demo.services;

import com.example.demo.DTOs.RecommendationDTO;
import com.example.demo.DTOs.Trip.TripResponseDTO;
import com.example.demo.api.Coordinates;
import com.example.demo.api.Feature;
import com.example.demo.api.PlacesResponse;
import com.example.demo.entities.RecommendationEntity;
import com.example.demo.entities.TripEntity;
import com.example.demo.mappers.RecommendationMapper;
import com.example.demo.mappers.TripMapper;
import com.example.demo.repositories.RecommendationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecommendationService {

    @Value("${opentrip.api.key}")
    private String apiKey;

    private final GeocodingService geocodingService;
    private final RecommendationMapper recommendationMapper;
    private final RecommendationRepository recommendationRepository;
    private final TripService tripService;
    private final TripMapper tripMapper;
    private final RestTemplate restTemplate = new RestTemplate();

    public List<RecommendationDTO> getRecommendationsForTrip(Long tripId) {
        TripEntity trip = tripService.getTripById(tripId);
        Coordinates coords = geocodingService.getCoordinates(trip.getDestination());

        String url = String.format(
                "https://api.opentripmap.com/0.1/en/places/radius?radius=5000&lon=%f&lat=%f&format=geojson&apikey=%s",
                coords.getLon(), coords.getLat(), apiKey);

        ResponseEntity<PlacesResponse> response = restTemplate.exchange(url, HttpMethod.GET, null, PlacesResponse.class);
        List<Feature> features = response.getBody().getFeatures();

        // Guardar entidades en la base
        List<RecommendationEntity> entities = features.stream()
                .map(recommendationMapper::toEntity)
                .peek(r -> r.setTrip(trip))
                .collect(Collectors.toList());

        recommendationRepository.saveAll(entities);

        return entities.stream()
                .map(r -> {
                    RecommendationDTO dto = new RecommendationDTO();
                    dto.setName(r.getName());
                    dto.setDescription(r.getDescription());
                    dto.setLat(r.getLat());
                    dto.setLon(r.getLon());
                    return dto;
                })
                .collect(Collectors.toList());
    }

}

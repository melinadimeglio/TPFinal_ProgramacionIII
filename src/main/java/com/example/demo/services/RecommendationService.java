package com.example.demo.services;

import com.example.demo.DTOs.RecommendationDTO;
import com.example.demo.DTOs.Trip.TripResponseDTO;
import com.example.demo.api.Coordinates;
import com.example.demo.api.Feature;
import com.example.demo.api.PlacesResponse;
import com.example.demo.entities.CategoryEntity;
import com.example.demo.entities.RecommendationEntity;
import com.example.demo.entities.TripEntity;
import com.example.demo.mappers.RecommendationMapper;
import com.example.demo.mappers.TripMapper;
import com.example.demo.repositories.CategoryRepository;
import com.example.demo.repositories.RecommendationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;
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
    private final CategoryRepository categoryRepository;
    private final RestTemplate restTemplate = new RestTemplate();

    public List<RecommendationDTO> getRecommendationsForTrip(Long tripId) {
        TripEntity trip = tripService.getTripById(tripId);
        Coordinates coords = geocodingService.getCoordinates(trip.getDestination());

        System.out.println("Lon: " + coords.getLon() + ", Lat: " + coords.getLat());

        String url = String.format(
                Locale.US,
                "https://api.opentripmap.com/0.1/en/places/radius?radius=%d&lon=%.6f&lat=%.6f&format=geojson&apikey=%s",
                5000, coords.getLon(), coords.getLat(), apiKey
        );

        ResponseEntity<PlacesResponse> response = restTemplate.exchange(url, HttpMethod.GET, null, PlacesResponse.class);
        List<Feature> features = response.getBody().getFeatures();

        List<RecommendationEntity> entities = features.stream()
                .map(feature -> {
                    RecommendationEntity entity = recommendationMapper.toEntity(feature);
                    entity.setTrip(trip);
                    Set<CategoryEntity> categories = getOrCreateCategories(feature.getProperties().getKinds());
                    entity.setCategories(categories);
                    return entity;
                })
                .collect(Collectors.toList());

        recommendationRepository.saveAll(entities);

        return entities.stream()
                .map(recommendationMapper::toDTO)
                .collect(Collectors.toList());

    }

    public Set<CategoryEntity> getOrCreateCategories(String kinds) {
        if (kinds == null || kinds.isBlank()) return Set.of();

        return Arrays.stream(kinds.split(","))
                .map(String::trim)
                .map(this::getOrCreateCategory)
                .collect(Collectors.toSet());
    }

    public CategoryEntity getOrCreateCategory(String name) {
        return categoryRepository.findByName(name)
                .orElseGet(() -> categoryRepository.save(CategoryEntity.builder().name(name).build()));
    }

}

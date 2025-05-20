package com.example.demo.services;

import com.example.demo.DTO.CoordinatesDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service

public class RecommendationService {

    @Value("${opentrip.api.key}")
    private String apiKey;

    //public CoordinatesDTO getCoordinates (String city){

    //}

}

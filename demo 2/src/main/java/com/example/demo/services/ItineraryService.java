package com.example.demo.services;

import com.example.demo.entities.ItineraryEntity;
import com.example.demo.repositories.ItineraryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ItineraryService {
    private final ItineraryRepository itineraryRepository;

    @Autowired
    public ItineraryService(ItineraryRepository itineraryRepository) {
        this.itineraryRepository = itineraryRepository;
    }

    public List<ItineraryEntity> findAll(){
        return itineraryRepository.findAll();
    }
}

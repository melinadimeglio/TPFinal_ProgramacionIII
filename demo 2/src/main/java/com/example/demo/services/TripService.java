package com.example.demo.services;

import com.example.demo.entities.TripEntity;
import com.example.demo.repositories.ActivityRepository;
import com.example.demo.repositories.TripRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TripService {
    private final TripRepository tripRepository;

    @Autowired
    public TripService(TripRepository tripRepository) {
        this.tripRepository = tripRepository;
    }

    public List<TripEntity> findAll(){
        return tripRepository.findAll();
    }


}

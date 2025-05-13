package com.example.demo.services;

import com.example.demo.entities.TripEntity;
import com.example.demo.repositories.ActivityRepository;
import com.example.demo.repositories.TripRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

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

    public TripEntity findById(Long id){
        return tripRepository.findById(id)
                .orElseThrow(()-> new NoSuchElementException("No se encontro el elemento"));
    }
}

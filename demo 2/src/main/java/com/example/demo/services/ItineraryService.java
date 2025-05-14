package com.example.demo.services;

import com.example.demo.entities.ItineraryEntity;
import com.example.demo.repositories.ItineraryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

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

    public ItineraryEntity findById(Long id){
        return itineraryRepository.findById(id)
                .orElseThrow(()-> new NoSuchElementException("No se encontro el elemento"));
    }

    public void save(ItineraryEntity itineraryEntity){
        itineraryRepository.save(itineraryEntity);
    }

    public void delete(ItineraryEntity itineraryEntity){
        itineraryRepository.delete(itineraryEntity);
    }
}

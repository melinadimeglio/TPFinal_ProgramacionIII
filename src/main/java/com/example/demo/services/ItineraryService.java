package com.example.demo.services;

import com.example.demo.DTOs.Itinerary.ItineraryCreateDTO;
import com.example.demo.DTOs.Itinerary.ItineraryResponseDTO;
import com.example.demo.DTOs.Itinerary.ItineraryUpdateDTO;
import com.example.demo.entities.ItineraryEntity;
import com.example.demo.mappers.ItineraryMapper;
import com.example.demo.repositories.ItineraryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class ItineraryService {
    private final ItineraryRepository itineraryRepository;
    private final ItineraryMapper itineraryMapper;


    @Autowired
    public ItineraryService(ItineraryRepository itineraryRepository, ItineraryMapper itineraryMapper) {
        this.itineraryRepository = itineraryRepository;
        this.itineraryMapper = itineraryMapper;

    }

    public List<ItineraryResponseDTO> findAll(){
        List<ItineraryEntity> entities = itineraryRepository.findAll();
        return itineraryMapper.toDTOList(entities);    }

    public ItineraryResponseDTO findById(Long id){
        ItineraryEntity entity = itineraryRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("No se encontró el itinerario"));
        return itineraryMapper.toDTO(entity);
    }

    public ItineraryEntity getEntityById(Long id) {
        return itineraryRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("No se encontró el itinerario"));
    }


    public void save(ItineraryCreateDTO dto){
        ItineraryEntity entity = itineraryMapper.toEntity(dto);
        itineraryRepository.save(entity);    }


    public void update(Long id, ItineraryUpdateDTO dto) {
        ItineraryEntity entity = getEntityById(id);
        itineraryMapper.updateEntityFromDTO(dto, entity);
        itineraryRepository.save(entity);
    }

    public void delete(Long id) {
        ItineraryEntity entity = getEntityById(id);
        itineraryRepository.delete(entity);
    }
}


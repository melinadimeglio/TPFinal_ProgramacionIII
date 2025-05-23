package com.example.demo.services;

import com.example.demo.DTOs.Expense.ExpenseResponseDTO;
import com.example.demo.DTOs.Itinerary.ItineraryCreateDTO;
import com.example.demo.DTOs.Itinerary.ItineraryResponseDTO;
import com.example.demo.DTOs.Itinerary.ItineraryUpdateDTO;
import com.example.demo.entities.ExpenseEntity;
import com.example.demo.entities.ItineraryEntity;
import com.example.demo.entities.TripEntity;
import com.example.demo.entities.UserEntity;
import com.example.demo.mappers.ItineraryMapper;
import com.example.demo.repositories.ItineraryRepository;
import com.example.demo.repositories.TripRepository;
import com.example.demo.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class ItineraryService {
    private final ItineraryRepository itineraryRepository;
    private final ItineraryMapper itineraryMapper;
    private final TripRepository tripRepository;
    private final UserRepository userRepository;

    @Autowired
    public ItineraryService(ItineraryRepository itineraryRepository, ItineraryMapper itineraryMapper, TripRepository tripRepository, UserRepository userRepository) {
        this.itineraryRepository = itineraryRepository;
        this.itineraryMapper = itineraryMapper;
        this.tripRepository = tripRepository;
        this.userRepository = userRepository;
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

    public List<ItineraryResponseDTO> findByUserId(Long userId) {
        List<ItineraryEntity> itineraries = itineraryRepository.findByUserId(userId);
        return itineraryMapper.toDTOList(itineraries);
    }

    public ItineraryResponseDTO save(ItineraryCreateDTO dto) {
        if (dto.getUserId() == null || dto.getTripId() == null) {
            throw new IllegalArgumentException("El usuario y el viaje no pueden ser nulos.");
        }

        ItineraryEntity entity = itineraryMapper.toEntity(dto);

        UserEntity user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new NoSuchElementException("Usuario no encontrado"));

        TripEntity trip = tripRepository.findById(dto.getTripId())
                .orElseThrow(() -> new NoSuchElementException("Viaje no encontrado"));

        entity.setUser(user);
        entity.setTrip(trip);

        ItineraryEntity saved = itineraryRepository.save(entity);
        return itineraryMapper.toDTO(saved);
    }


        public ItineraryResponseDTO updateAndReturn(Long id, ItineraryUpdateDTO dto) {
        ItineraryEntity entity = getEntityById(id);
        itineraryMapper.updateEntityFromDTO(dto, entity);
        ItineraryEntity saved = itineraryRepository.save(entity);
        return itineraryMapper.toDTO(saved);
    }


    public void delete(Long id) {
        ItineraryEntity entity = getEntityById(id);
        itineraryRepository.delete(entity);
    }
}


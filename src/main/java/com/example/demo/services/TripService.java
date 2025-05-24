package com.example.demo.services;

import com.example.demo.DTOs.Trip.TripCreateDTO;
import com.example.demo.DTOs.Trip.TripResponseDTO;
import com.example.demo.DTOs.Trip.TripUpdateDTO;
import com.example.demo.entities.TripEntity;
import com.example.demo.entities.UserEntity;
import com.example.demo.mappers.TripMapper;
import com.example.demo.repositories.ActivityRepository;
import com.example.demo.repositories.TripRepository;
import com.example.demo.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class TripService {
    private final TripRepository tripRepository;
    private final UserRepository userRepository;
    private final TripMapper tripMapper;

    @Autowired
    public TripService(TripRepository tripRepository, UserRepository userRepository, TripMapper tripMapper) {
        this.tripRepository = tripRepository;
        this.userRepository = userRepository;
        this.tripMapper = tripMapper;
    }

    public List<TripResponseDTO> findAll() {
        List<TripEntity> trips = tripRepository.findAll();
        return tripMapper.toDTOList(trips);
    }

    public TripResponseDTO findById(Long id) {
        TripEntity trip = tripRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("No se encontró el viaje con ID " + id));
        return tripMapper.toDTO(trip);
    }

    public void save(TripCreateDTO dto) {
        TripEntity entity = tripMapper.toEntity(dto);

        if (dto.getUserIds() != null && !dto.getUserIds().isEmpty()) {
            Set<UserEntity> users = userRepository.findAllById(dto.getUserIds())
                    .stream().collect(Collectors.toSet());
            entity.setUsers(users);
        }

        tripRepository.save(entity);
    }

    public void update(Long id, TripUpdateDTO dto) {
        TripEntity existingTrip = tripRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("No se encontró el viaje con ID " + id));

        tripMapper.updateEntityFromDTO(dto, existingTrip);
        tripRepository.save(existingTrip);
    }

    // Eliminar un viaje por ID
    public void delete(Long id) {
        TripEntity trip = tripRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("No se encontró el viaje con ID " + id));
        tripRepository.delete(trip);
    }

    // necesario para la api
    public TripEntity getTripById(Long id) {
        return tripRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("No se encontró el viaje con ID " + id));
    }

}


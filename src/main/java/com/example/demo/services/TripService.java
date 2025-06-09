package com.example.demo.services;

import com.example.demo.DTOs.Trip.Request.TripCreateDTO;
import com.example.demo.DTOs.Trip.Response.TripResponseDTO;
import com.example.demo.DTOs.Trip.TripUpdateDTO;
import com.example.demo.entities.TripEntity;
import com.example.demo.entities.UserEntity;
import com.example.demo.mappers.TripMapper;
import com.example.demo.repositories.TripRepository;
import com.example.demo.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

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

    public Page<TripResponseDTO> findAll(Pageable pageable) {
        return tripRepository.findAllByActiveTrue(pageable)
                .map(tripMapper::toDTO);
    }

    public Page<TripResponseDTO> findAllInactive(Pageable pageable) {
        return tripRepository.findAllByActiveFalse(pageable)
                .map(tripMapper::toDTO);
    }

    public TripResponseDTO findById(Long id) {
        TripEntity trip = tripRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("No se encontró el viaje con ID " + id));
        return tripMapper.toDTO(trip);
    }

    public TripResponseDTO save(TripCreateDTO dto, Long myUserId) {

        // Creamos el set de usuarios participantes
        Set<UserEntity> users = new HashSet<>();

        UserEntity owner = userRepository.findById(myUserId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        users.add(owner);

        if (dto.getSharedUserIds() != null) {
            for (Long sharedId : dto.getSharedUserIds()) {
                UserEntity sharedUser = userRepository.findById(sharedId)
                        .orElseThrow(() -> new RuntimeException("Usuario compartido no encontrado"));
                users.add(sharedUser);
            }
        }

        TripEntity trip = tripMapper.toEntity(dto);
        trip.setUsers(users);

        for (UserEntity user : users) {
            user.getTrips().add(trip);
        }

        TripEntity savedTrip = tripRepository.save(trip);
        return tripMapper.toDTO(savedTrip);
    }



    public TripResponseDTO update(Long id, TripUpdateDTO dto) {
        TripEntity entity = tripRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Trip not found"));

        tripMapper.updateEntityFromDTO(dto, entity);
        TripEntity updated = tripRepository.save(entity);

        return tripMapper.toDTO(updated);
    }


    public void delete(Long id) {
        TripEntity trip = tripRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("No se encontró el viaje con ID " + id));
        trip.setActive(false);
        trip.getChecklist().forEach(checkListEntity -> checkListEntity.setActive(false));
        trip.getItineraries().forEach(itineraryEntity -> itineraryEntity.setActive(false));
    }

    public List<TripResponseDTO> findByUserId(Long userId) {
        List<TripEntity> trips = tripRepository.findByUsersId(userId);
        return tripMapper.toDTOList(trips);
    }

    //necesario para la api
    public TripEntity getTripById(Long id) {
        return tripRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Trip not found"));
    }

}



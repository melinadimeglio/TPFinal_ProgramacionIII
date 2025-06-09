package com.example.demo.services;

import com.example.demo.DTOs.Reservation.Request.ReservationCreateDTO;
import com.example.demo.DTOs.Reservation.Response.ReservationResponseDTO;
import com.example.demo.entities.ActivityEntity;
import com.example.demo.entities.ReservationEntity;
import com.example.demo.entities.UserEntity;
import com.example.demo.enums.ReservationStatus;
import com.example.demo.mappers.ReservationMapper;
import com.example.demo.repositories.ActivityRepository;
import com.example.demo.repositories.ReservationRepository;
import com.example.demo.repositories.UserRepository;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReservationService {
    private final ReservationRepository reservationRepository;
    private final UserRepository userRepository;
    private final ActivityRepository activityRepository;
    private final ReservationMapper reservationMapper;

    @Autowired
    public ReservationService(ReservationRepository reservationRepository,
                              UserRepository userRepository,
                              ActivityRepository activityRepository,
                              ReservationMapper reservationMapper) {
        this.reservationRepository = reservationRepository;
        this.userRepository = userRepository;
        this.activityRepository = activityRepository;
        this.reservationMapper = reservationMapper;
    }

    public ReservationResponseDTO createReservation(ReservationCreateDTO dto, Long userId) {

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        ActivityEntity activity = activityRepository.findById(dto.getActivityId())
                .orElseThrow(() -> new ResourceNotFoundException("Activity not found"));

        ReservationEntity reservation = reservationMapper.toEntity(dto, user, activity);
        ReservationEntity saved = reservationRepository.save(reservation);

        return reservationMapper.toDTO(saved);
    }

    public void cancelReservation(Long reservationId) {
        ReservationEntity reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation not found"));

        reservation.setStatus(ReservationStatus.CANCELLED);
        reservationRepository.save(reservation);
    }

    public List<ReservationResponseDTO> findAll() {
        return reservationRepository.findAll().stream()
                .map(reservationMapper::toDTO)
                .collect(Collectors.toList());
    }

    public List<ReservationResponseDTO> findByUserId(Long userId) {
        return reservationRepository.findByUserId(userId).stream()
                .map(reservationMapper::toDTO)
                .collect(Collectors.toList());
    }

    public List<ReservationResponseDTO> findByCompanyId(Long companyId) {
        return reservationRepository.findByCompanyId(companyId).stream()
                .map(reservationMapper::toDTO)
                .collect(Collectors.toList());
    }


}

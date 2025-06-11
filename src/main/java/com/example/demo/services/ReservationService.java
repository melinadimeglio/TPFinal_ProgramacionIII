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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
        reservation.setStatus(ReservationStatus.PENDING);
        ReservationEntity saved = reservationRepository.save(reservation);

        return reservationMapper.toDTO(saved);
    }

    public void paidReservation(Long reservationId){
        ReservationEntity reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation not found"));

        reservation.setStatus(ReservationStatus.ACTIVE);
        reservationRepository.save(reservation);
    }

    public void cancelReservation(Long reservationId) {
        ReservationEntity reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation not found"));

        reservation.setStatus(ReservationStatus.CANCELLED);
        reservationRepository.save(reservation);
    }

    public Page<ReservationResponseDTO> findAll(Pageable pageable) {
        return reservationRepository.findAll(pageable)
                .map(reservationMapper::toDTO);
    }

    public Page<ReservationResponseDTO> findByUserId(Long userId, Pageable pageable) {
        return reservationRepository.findByUserId(userId, pageable)
                .map(reservationMapper::toDTO);
    }

    public Page<ReservationResponseDTO> findByCompanyId(Long companyId, Pageable pageable) {
        return reservationRepository.findByCompanyId(companyId, pageable)
                .map(reservationMapper::toDTO);
    }


}

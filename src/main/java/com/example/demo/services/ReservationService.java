package com.example.demo.services;

import com.example.demo.DTOs.Itinerary.Response.ItineraryResponseDTO;
import com.example.demo.DTOs.Reservation.Request.ReservationCreateDTO;
import com.example.demo.DTOs.Reservation.Response.ReservationResponseDTO;
import com.example.demo.DTOs.Trip.Response.TripResponseDTO;
import com.example.demo.entities.*;
import com.example.demo.enums.ReservationStatus;
import com.example.demo.mappers.ReservationMapper;
import com.example.demo.repositories.ActivityRepository;
import com.example.demo.repositories.ReservationRepository;
import com.example.demo.repositories.UserRepository;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ReservationService {
    private final ReservationRepository reservationRepository;
    private final UserRepository userRepository;
    private final ActivityRepository activityRepository;
    private final ReservationMapper reservationMapper;
    private final ItineraryService itineraryService;
    private final TripService tripService;
    private final ActivityService activityService;
    private final MPService mpService;

    @Autowired
    public ReservationService(ReservationRepository reservationRepository,
                              UserRepository userRepository,
                              ActivityRepository activityRepository,
                              ReservationMapper reservationMapper, ItineraryService itineraryService, TripService tripService, ActivityService activityService, MPService mpService) {
        this.reservationRepository = reservationRepository;
        this.userRepository = userRepository;
        this.activityRepository = activityRepository;
        this.reservationMapper = reservationMapper;
        this.itineraryService = itineraryService;
        this.tripService = tripService;
        this.activityService = activityService;
        this.mpService = mpService;
    }

    public ReservationResponseDTO createReservation(ReservationCreateDTO dto, Long userId) {

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        ActivityEntity activity = activityRepository.findById(dto.getActivityId())
                .orElseThrow(() -> new ResourceNotFoundException("Activity not found"));

        Optional<CompanyEntity> companyId = Optional.ofNullable(activity.getCompany());

        if (companyId.isEmpty()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No puede crear una reservacion la actividad ingresada.");
        }

        ReservationEntity reservation = reservationMapper.toEntity(dto, user, activity);
        reservation.setStatus(ReservationStatus.PENDING);
        reservation.setPaid(false);
        reservation.setAmount(activity.getPrice());

        ReservationEntity saved = reservationRepository.save(reservation);

        try {
            String link = mpService.mercado(saved);
            saved.setUrlPayment(link);
        } catch (MPException e) {
            throw new RuntimeException("Error al generar reserva o link de pago.");
        } catch (MPApiException e) {
            throw new RuntimeException("Error al generar reserva o link de pago.");
        }

        reservationRepository.save(saved);

        return reservationMapper.toDTO(saved);
    }

    public boolean activityDisponible (Long reservationId) {
        ReservationEntity reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation not found"));

        ActivityEntity activity = activityRepository.findById(reservation.getActivity().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Activity not found"));

        int cant = reservation.getActivity().getUsers().size();

        //System.out.println("CANT USERS: " + cant);

        if (activity.getAvailable_quantity() - cant >= 0) {
            activity.setAvailable_quantity(activity.getAvailable_quantity() - cant);
            reservation.setStatus(ReservationStatus.PENDING);
            reservationRepository.save(reservation);
        } else {
            return false;
        }

        return true;
    }

    public void paidReservation(Long reservationId, Long userId, Pageable pageable){
        ReservationEntity reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation not found"));

        ActivityEntity activity = activityRepository.findById(reservation.getActivity().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Activity not found"));

        Set<ItineraryResponseDTO> itinerariosUser = itineraryService.findByUserId(userId, pageable).toSet();

        Optional<ItineraryResponseDTO> itineraryOptional = itinerariosUser.stream()
                        .filter(itinerary -> itinerary.getItineraryDate().equals(activity.getDate()))
                        .findFirst();

        if (itineraryOptional.isEmpty()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No hay itinerario para la fecha de la actividad. Por favor cree uno.");
        }

        TripEntity trip = tripService.getTripById(itineraryOptional.get().getTripId());
        int cant = trip.getCompanions() + 1 ;

        if (!activityService.updateCapacity(activity.getId(), cant)){
            throw new ResponseStatusException(HttpStatus.CONFLICT, "No se puede guardar la actividad ya que no cuenta con la disponibilidad suficiente.");
        }

        Long itineraryId = itineraryOptional.get().getId();

        if (!itineraryService.addActivity(itineraryId, userId, activity.getId())){
            throw new ResponseStatusException(HttpStatus.CONFLICT, "No se pudo agregar la actividad al itinerario.");
        }


        reservation.setStatus(ReservationStatus.ACTIVE);
        reservation.setPaid(true);
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

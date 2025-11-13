package com.example.demo.services;

import com.example.demo.DTOs.Expense.Request.ExpenseCreateDTO;
import com.example.demo.DTOs.Itinerary.Response.ItineraryResponseDTO;
import com.example.demo.DTOs.Reservation.Request.ReservationCreateDTO;
import com.example.demo.DTOs.Reservation.Response.ReservationResponseDTO;
import com.example.demo.DTOs.Trip.Response.TripResponseDTO;
import com.example.demo.entities.*;
import com.example.demo.enums.ExpenseCategory;
import com.example.demo.enums.ReservationStatus;
import com.example.demo.exceptions.ReservationException;
import com.example.demo.mappers.ReservationMapper;
import com.example.demo.repositories.ActivityRepository;
import com.example.demo.repositories.ItineraryRepository;
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
    private final ExpenseService expenseService;
    private final ItineraryRepository itineraryRepository;

    @Autowired
    public ReservationService(ReservationRepository reservationRepository,
                              UserRepository userRepository,
                              ActivityRepository activityRepository,
                              ReservationMapper reservationMapper, ItineraryService itineraryService, TripService tripService, ActivityService activityService, MPService mpService, ExpenseService expenseService, ItineraryRepository itineraryRepository) {
        this.reservationRepository = reservationRepository;
        this.userRepository = userRepository;
        this.activityRepository = activityRepository;
        this.reservationMapper = reservationMapper;
        this.itineraryService = itineraryService;
        this.tripService = tripService;
        this.activityService = activityService;
        this.mpService = mpService;
        this.expenseService = expenseService;
        this.itineraryRepository = itineraryRepository;
    }

    public ReservationResponseDTO createReservation(ReservationCreateDTO dto, Long userId) throws MPException, MPApiException {

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        ActivityEntity activity = activityRepository.findById(dto.getActivityId())
                .orElseThrow(() -> new ResourceNotFoundException("Activity not found"));

        Optional<CompanyEntity> companyId = Optional.ofNullable(activity.getCompany());

        if (companyId.isEmpty()){
            throw new ReservationException("You cannot create a reservation for the entered activity.");
        }

        Set<ItineraryResponseDTO> itinerariosUser = itineraryService.findByUserId(userId, Pageable.unpaged()).toSet();

        Optional<ItineraryResponseDTO> itineraryOptional = itinerariosUser.stream()
                .filter(itinerary -> itinerary.getItineraryDate().equals(activity.getDate()))
                .findFirst();

        if (itineraryOptional.isEmpty()){
            throw new ReservationException("There is no itinerary for the activity date. Please create one.");
        }

        if (!activityDisponible(itineraryOptional.get().getTripId(), dto.getActivityId())){
            throw new ReservationException("The activity cannot be saved because it does not have sufficient availability.");
        }

        ReservationEntity reservation = reservationMapper.toEntity(dto, user, activity);
        reservation.setStatus(ReservationStatus.PENDING);
        reservation.setPaid(false);
        reservation.setAmount(activity.getPrice());

        ReservationEntity saved = reservationRepository.save(reservation);

        try {
            String link = mpService.mercado(saved);
            saved.setUrlPayment(link);
        } catch (MPApiException e) {
            System.out.println("MP API Error: {}" + e.getApiResponse().getContent());
            throw new ReservationException("Error processing payment. Details: " + e.getApiResponse().getContent());
        }

        reservationRepository.save(saved);

        return reservationMapper.toDTO(saved);
    }

    public boolean activityDisponible (Long tripId, Long activityId) {

        ActivityEntity activity = activityRepository.findById(activityId)
                .orElseThrow(() -> new ResourceNotFoundException("Activity not found"));

        TripEntity trip = tripService.getTripById(tripId);
        int cant = trip.getCompanions() + 1 ;

        if (activity.getAvailable_quantity() - cant < 0) {
            return false;
        }

        return true;
    }

    public void paidReservation(Long reservationId, Long userId, Pageable pageable){
        ReservationEntity reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ReservationException("Reservation not found"));

        ActivityEntity activity = activityRepository.findById(reservation.getActivity().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Activity not found"));

        Set<ItineraryResponseDTO> itinerariosUser = itineraryService.findByUserId(userId, pageable).toSet();

        Optional<ItineraryResponseDTO> itineraryOptional = itinerariosUser.stream()
                        .filter(itinerary -> itinerary.getItineraryDate().equals(activity.getDate()))
                        .findFirst();

        if (itineraryOptional.isEmpty()){
            throw new ReservationException("There is no itinerary for the activity date. Please create one..");
        }

        TripEntity trip = tripService.getTripById(itineraryOptional.get().getTripId());
        int cant = trip.getCompanions() + 1 ;

        if (!activityService.updateCapacity(activity.getId(), cant)){
            throw new ReservationException("The activity cannot be saved because it does not have sufficient availability..");
        }

        Long itineraryId = itineraryOptional.get().getId();

        if (!itineraryService.addActivity(itineraryId, userId, activity.getId())){
            throw new ReservationException("The activity could not be added to the itinerary.");
        }

        Set<Long> users = trip.getUsers().stream()
                        .map(UserEntity::getId)
                        .collect(Collectors.toSet());

        expenseService.save(ExpenseCreateDTO.builder()
                .amount(activity.getPrice())
                .description(activity.getDescription())
                .date(activity.getDate())
                .tripId(trip.getId())
                .category(ExpenseCategory.ACTIVIDADES)
                .sharedUserIds(users)
                .build(), userId);

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

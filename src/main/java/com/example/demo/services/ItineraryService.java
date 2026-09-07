package com.example.demo.services;

import com.example.demo.DTOs.Filter.ItineraryFilterDTO;
import com.example.demo.DTOs.Itinerary.ItineraryUpdateDTO;
import com.example.demo.DTOs.Itinerary.Request.ItineraryCreateDTO;
import com.example.demo.DTOs.Itinerary.Response.ItineraryResponseDTO;
import com.example.demo.entities.ActivityEntity;
import com.example.demo.entities.ItineraryEntity;
import com.example.demo.entities.TripEntity;
import com.example.demo.entities.UserEntity;
import com.example.demo.mappers.ItineraryMapper;
import com.example.demo.repositories.ActivityRepository;
import com.example.demo.repositories.ItineraryRepository;
import com.example.demo.repositories.TripRepository;
import com.example.demo.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class ItineraryService {
    private final ItineraryRepository itineraryRepository;
    private final ItineraryMapper itineraryMapper;
    private final TripRepository tripRepository;
    private final UserRepository userRepository;
    private final ActivityRepository activityRepository;

    @Autowired
    public ItineraryService(ItineraryRepository itineraryRepository, ItineraryMapper itineraryMapper, TripRepository tripRepository, UserRepository userRepository, ActivityRepository activityRepository) {
        this.itineraryRepository = itineraryRepository;
        this.itineraryMapper = itineraryMapper;
        this.tripRepository = tripRepository;
        this.userRepository = userRepository;
        this.activityRepository = activityRepository;
    }

    public Page<ItineraryResponseDTO> findAll(Pageable pageable) {
        return itineraryRepository.findAllByActiveTrue(pageable)
                .map(itineraryMapper::toDTO);
    }

    public Page<ItineraryResponseDTO> findAllInactive(Pageable pageable) {
        return itineraryRepository.findAllByActiveFalse(pageable)
                .map(itineraryMapper::toDTO);
    }

    public boolean addActivity(Long itineraryId, Long userId, Long activityId) {

        ItineraryEntity itinerary = itineraryRepository.findById(itineraryId)
                .orElseThrow(() -> new NoSuchElementException("Itinerary not found."));

        if (!itinerary.getUser().getId().equals(userId)) {
            throw new AccessDeniedException("You do not have permission to view this itinerary.");
        }

        ActivityEntity activity = activityRepository.findById(activityId)
                .orElseThrow(() -> new NoSuchElementException("It was not possible to find the activity to add to the itinerary."));

        if (activity.getStartTime() != null && activity.getEndTime() != null) {
            boolean superposicion = itinerary.getActivities() != null && itinerary.getActivities().stream()
                    .filter(a -> !a.getId().equals(activity.getId()))
                    .filter(a -> a.getStartTime() != null && a.getEndTime() != null)
                    .anyMatch(a ->
                            activity.getStartTime().isBefore(a.getEndTime()) &&
                                    a.getStartTime().isBefore(activity.getEndTime())
                    );

            if (superposicion) {
                throw new IllegalArgumentException("Ya existe una actividad que se superpone en ese horario.");
            }
        }

        activity.setItinerary(itinerary);

        List<ActivityEntity> activitiesItinerary = itinerary.getActivities();
        activitiesItinerary.add(activity);
        itinerary.setActivities(activitiesItinerary);
        itineraryRepository.save(itinerary);

        return true;
    }

    public ItineraryResponseDTO findById(Long id) {
        ItineraryEntity itinerary = itineraryRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Itinerary not found."));

        return itineraryMapper.toDTO(itinerary);
    }

    /*public ItineraryResponseDTO findByIdIfBelongsToUser(Long itineraryId, Long userId) {
        ItineraryEntity itinerary = itineraryRepository.findById(itineraryId)
                .orElseThrow(() -> new NoSuchElementException("Itinerary not found."));

        List<ActivityEntity> activitiesItinerary = itinerary.getActivities();
        System.out.println("ACTIVIDADES: " + activitiesItinerary);

        if (!itinerary.getUser().getId().equals(userId)) {
            throw new AccessDeniedException("You do not have permission to view this itinerary.");
        }

        // Mapear manualmente actividades al DTO
        ItineraryResponseDTO dto = itineraryMapper.toDTO(itinerary);
        dto.setActivities(
                itinerary.getActivities()
                        .stream()
                        .map(activityMapper::toResumeDTO)  // Asegurate de tener un mapper de actividades
                        .collect(Collectors.toList())
        );

        return dto;
    }*/

    @Transactional(readOnly = true)
    public ItineraryResponseDTO findByIdIfBelongsToUser(Long itineraryId, Long userId) {
        ItineraryEntity itinerary = itineraryRepository.findByIdWithActivities(itineraryId)
                .orElseThrow(() -> new NoSuchElementException("Itinerary not found."));

        if (!itinerary.getUser().getId().equals(userId)) {
            throw new AccessDeniedException("No permission");
        }

        return itineraryMapper.toDTO(itinerary);
    }


    public ItineraryEntity getEntityById(Long id) {
        return itineraryRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Itinerary not found."));
    }

    public Page<ItineraryResponseDTO> findByUserId(Long userId, Pageable pageable) {
        return itineraryRepository.findByUserId(userId, pageable)
                .map(itineraryMapper::toDTO);
    }

    public ItineraryResponseDTO save(ItineraryCreateDTO dto, Long myUserId) {

        UserEntity user = userRepository.findById(myUserId)
                .orElseThrow(() -> new NoSuchElementException("User not found."));


        TripEntity trip = tripRepository.findById(dto.getTripId())
                .orElseThrow(() -> new NoSuchElementException("Trip not found."));


        boolean belongsToUser = trip.getUsers().stream()
                .anyMatch(u -> u.getId().equals(myUserId));

        if (dto.getItineraryDate().isBefore(trip.getStartDate()) || dto.getItineraryDate().isAfter(trip.getEndDate())) {
            throw new IllegalArgumentException("The itinerary date does not correspond to the travel date range.");
        }

        if (!belongsToUser) {
            throw new AccessDeniedException("You do not have permission to use this itinerary.");
        }

        boolean alreadyExists = trip.getItineraries() != null && trip.getItineraries().stream()
                .anyMatch(it ->
                        it.isActive()
                                && it.getItineraryDate().equals(dto.getItineraryDate())
                                && it.getUser().getId().equals(myUserId)
                );

        if (alreadyExists) {
            throw new IllegalArgumentException("Ya existe un itinerario para esa fecha en este viaje.");
        }

        ItineraryEntity entity = itineraryMapper.toEntity(dto);
        entity.setUser(user);
        entity.setTrip(trip);

        ItineraryEntity saved = itineraryRepository.save(entity);

        return itineraryMapper.toDTO(saved);
    }


    public ItineraryResponseDTO updateAndReturnIfOwned(Long id, ItineraryUpdateDTO dto, Long userId) {
        ItineraryEntity entity = itineraryRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Itinerary not found."));

        if (!entity.getUser().getId().equals(userId)) {
            throw new AccessDeniedException("You do not have permission to update this itinerary.");
        }

        TripEntity trip = tripRepository.findById(entity.getTrip().getId())
                .orElseThrow(() -> new NoSuchElementException("Trip not found."));

        if (dto.getItineraryDate().isBefore(trip.getStartDate()) || dto.getItineraryDate().isAfter(trip.getEndDate())) {
            throw new IllegalArgumentException("The itinerary date does not correspond to the travel date range.");
        }

        itineraryMapper.updateEntityFromDTO(dto, entity);
        ItineraryEntity saved = itineraryRepository.save(entity);
        return itineraryMapper.toDTO(saved);

    }


    public void softDeleteIfOwned(Long id, Long userId) {
        ItineraryEntity entity = itineraryRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Itinerary not found."));

        if (!entity.getUser().getId().equals(userId)) {
            throw new AccessDeniedException("You do not have permission to delete this itinerary.");
        }

        entity.setActive(false);
        itineraryRepository.save(entity);
    }

    public void restoreIfOwned(Long id, Long userId) {
        ItineraryEntity entity = itineraryRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Itinerary not found."));

        if (!entity.getUser().getId().equals(userId)) {
            throw new AccessDeniedException("You do not have permission to restore this itinerary.");
        }

        entity.setActive(true);
        itineraryRepository.save(entity);
    }

    public Page<ItineraryResponseDTO> findByUserIdWithFilters(Long userId, ItineraryFilterDTO filters, Pageable pageable) {
        Specification<ItineraryEntity> spec = Specification
                .where((Specification<ItineraryEntity>) (root, query, cb) ->
                        cb.equal(root.get("user").get("id"), userId)
                )
                .and((Specification<ItineraryEntity>) (root, query, cb) ->
                        cb.isTrue(root.get("active"))
                );


        if (filters.getDateFrom() != null) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.greaterThanOrEqualTo(root.get("itineraryDate"), LocalDate.parse(filters.getDateFrom())));
        }
        if (filters.getDateTo() != null) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.lessThanOrEqualTo(root.get("itineraryDate"), LocalDate.parse(filters.getDateTo())));
        }

        if (filters.getTripId() != null) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(root.get("trip").get("id"), filters.getTripId()));
        }

        Page<ItineraryEntity> page = itineraryRepository.findAll(spec, pageable);
        return page.map(itineraryMapper::toDTO);
    }

    public boolean tieneSuperposicion(Long itineraryId, LocalTime startTime, LocalTime endTime) {
        if (startTime == null || endTime == null) return false;

        ItineraryEntity itinerary = itineraryRepository.findById(itineraryId)
                .orElseThrow(() -> new NoSuchElementException("Itinerary not found."));

        return itinerary.getActivities() != null && itinerary.getActivities().stream()
                .filter(a -> a.getStartTime() != null && a.getEndTime() != null)
                .anyMatch(a ->
                        startTime.isBefore(a.getEndTime()) &&
                                a.getStartTime().isBefore(endTime)
                );
    }


}


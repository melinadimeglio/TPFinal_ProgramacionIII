package com.example.demo.services;

import com.example.demo.DTOs.Activity.Response.ActivityResponseDTO;
import com.example.demo.DTOs.Filter.ItineraryFilterDTO;
import com.example.demo.DTOs.Itinerary.Request.ItineraryCreateDTO;
import com.example.demo.DTOs.Itinerary.Response.ItineraryResponseDTO;
import com.example.demo.DTOs.Itinerary.ItineraryUpdateDTO;
import com.example.demo.entities.ActivityEntity;
import com.example.demo.entities.ItineraryEntity;
import com.example.demo.entities.TripEntity;
import com.example.demo.entities.UserEntity;
import com.example.demo.mappers.ActivityMapper;
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

import java.time.LocalDate;
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

    public Page<ItineraryResponseDTO> findAll(Pageable pageable){
        return itineraryRepository.findAllByActiveTrue(pageable)
                .map(itineraryMapper::toDTO);
    }

    public Page<ItineraryResponseDTO> findAllInactive(Pageable pageable){
        return itineraryRepository.findAllByActiveFalse(pageable)
                .map(itineraryMapper::toDTO);
    }

    public boolean addActivity (Long itineraryId, Long userId, Long activityId){

        ItineraryEntity itinerary = itineraryRepository.findById(itineraryId)
                .orElseThrow(() -> new NoSuchElementException("No se encontró el itinerario"));

        if (!itinerary.getUser().getId().equals(userId)) {
            throw new AccessDeniedException("No tenés permiso para acceder a este itinerario.");
        }

        ActivityEntity activity = activityRepository.findById(activityId)
                .orElseThrow(() -> new NoSuchElementException("No fue posible encontrar la actividad para agregarla al itinerario."));

        activity.setItinerary(itinerary);

        List<ActivityEntity> activitiesItinerary = itinerary.getActivities();
        activitiesItinerary.add(activity);
        itinerary.setActivities(activitiesItinerary);
        itineraryRepository.save(itinerary);

        return true;
    }

    public ItineraryResponseDTO findByIdIfBelongsToUser(Long itineraryId, Long userId) {
        ItineraryEntity itinerary = itineraryRepository.findById(itineraryId)
                .orElseThrow(() -> new NoSuchElementException("No se encontró el itinerario"));

        if (!itinerary.getUser().getId().equals(userId)) {
            throw new AccessDeniedException("No tenés permiso para ver este itinerario");
        }

        return itineraryMapper.toDTO(itinerary);
    }


    public ItineraryEntity getEntityById(Long id) {
        return itineraryRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("No se encontró el itinerario"));
    }

    public Page<ItineraryResponseDTO> findByUserId(Long userId, Pageable pageable) {
        return itineraryRepository.findByUserId(userId, pageable)
                .map(itineraryMapper::toDTO);
    }

    public ItineraryResponseDTO save(ItineraryCreateDTO dto, Long myUserId) {
        UserEntity user = userRepository.findById(myUserId)
                .orElseThrow(() -> new NoSuchElementException("Usuario no encontrado"));

        TripEntity trip = tripRepository.findById(dto.getTripId())
                .orElseThrow(() -> new NoSuchElementException("Viaje no encontrado"));

        boolean belongsToUser = trip.getUsers().stream()
                .anyMatch(u -> u.getId().equals(myUserId));

        if (!belongsToUser) {
            throw new AccessDeniedException("No tenés permiso para agregar itinerarios a este viaje");
        }

        ItineraryEntity entity = itineraryMapper.toEntity(dto);
        entity.setUser(user);
        entity.setTrip(trip);

        ItineraryEntity saved = itineraryRepository.save(entity);
        return itineraryMapper.toDTO(saved);
    }


    public ItineraryResponseDTO updateAndReturnIfOwned(Long id, ItineraryUpdateDTO dto, Long userId) {
        ItineraryEntity entity = itineraryRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("No se encontró el itinerario"));

        if (!entity.getUser().getId().equals(userId)) {
            throw new AccessDeniedException("No tenés permiso para modificar este itinerario");
        }

        itineraryMapper.updateEntityFromDTO(dto, entity);
        ItineraryEntity saved = itineraryRepository.save(entity);
        return itineraryMapper.toDTO(saved);
    }


    public void softDeleteIfOwned(Long id, Long userId) {
        ItineraryEntity entity = itineraryRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("No se encontró el itinerario"));

        if (!entity.getUser().getId().equals(userId)) {
            throw new AccessDeniedException("No tenés permiso para eliminar este itinerario");
        }

        entity.setActive(false);
        itineraryRepository.save(entity);
    }

    public void restoreIfOwned(Long id, Long userId) {
        ItineraryEntity entity = itineraryRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("No se encontró el itinerario"));

        if (!entity.getUser().getId().equals(userId)) {
            throw new AccessDeniedException("No tenés permiso para restaurar este itinerario");
        }

        entity.setActive(true);
        itineraryRepository.save(entity);
    }

    public Page<ItineraryResponseDTO> findByUserIdWithFilters(Long userId, ItineraryFilterDTO filters, Pageable pageable) {
        Specification<ItineraryEntity> spec = Specification.where(
                (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("user").get("id"), userId)
        );

        if (filters.getDateFrom() != null) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.greaterThanOrEqualTo(root.get("itineraryDate"), LocalDate.parse(filters.getDateFrom())));
        }
        if (filters.getDateTo() != null) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.lessThanOrEqualTo(root.get("itineraryDate"), LocalDate.parse(filters.getDateTo())));
        }

        Page<ItineraryEntity> page = itineraryRepository.findAll(spec, pageable);
        return page.map(itineraryMapper::toDTO);
    }


}


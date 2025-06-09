package com.example.demo.services;

import com.example.demo.DTOs.Activity.Request.CompanyActivityCreateDTO;
import com.example.demo.DTOs.Activity.Request.UserActivityCreateDTO;
import com.example.demo.DTOs.Activity.Response.ActivityResponseDTO;
import com.example.demo.DTOs.Activity.ActivityUpdateDTO;
import com.example.demo.DTOs.Activity.Response.CompanyResponseDTO;
import com.example.demo.entities.ActivityEntity;
import com.example.demo.entities.CompanyEntity;
import com.example.demo.entities.ItineraryEntity;
import com.example.demo.entities.UserEntity;
import com.example.demo.enums.ActivityCategory;
import com.example.demo.mappers.ActivityMapper;
import com.example.demo.repositories.ActivityRepository;
import com.example.demo.repositories.CompanyRepository;
import com.example.demo.repositories.ItineraryRepository;
import com.example.demo.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ActivityService {
    private final ActivityRepository activityRepository;
    private final ActivityMapper activityMapper;
    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;
    private final ItineraryRepository itineraryRepository;

    @Autowired
    public ActivityService(ActivityRepository activityRepository,
                           ActivityMapper activityMapper,
                           UserRepository userRepository,
                           CompanyRepository companyRepository,
                           ItineraryRepository itineraryRepository) {
        this.activityRepository = activityRepository;
        this.activityMapper = activityMapper;
        this.userRepository = userRepository;
        this.companyRepository = companyRepository;
        this.itineraryRepository = itineraryRepository;
    }

    public ActivityResponseDTO createFromUser(UserActivityCreateDTO dto, Long myUserId) {
        ActivityEntity entity = activityMapper.toEntity(dto);
        entity.setAvailable(true);

        Set<UserEntity> users = new HashSet<>();
        UserEntity currentUser = userRepository.findById(myUserId)
                .orElseThrow(() -> new NoSuchElementException("Usuario no encontrado"));
        users.add(currentUser);

        if (dto.getSharedUserIds() != null) {
            dto.getSharedUserIds().forEach(id -> {
                UserEntity sharedUser = userRepository.findById(id)
                        .orElseThrow(() -> new NoSuchElementException("Usuario compartido no encontrado"));
                users.add(sharedUser);
            });
        }

        entity.setUsers(users);

        if (dto.getItineraryId() != null) {
            ItineraryEntity itinerary = itineraryRepository.findById(dto.getItineraryId())
                    .orElseThrow(() -> new NoSuchElementException("Itinerario no encontrado"));
            entity.setItinerary(itinerary);
        }

        ActivityEntity saved = activityRepository.save(entity);
        return activityMapper.toDTO(saved);
    }



    public ActivityResponseDTO createFromCompany(CompanyActivityCreateDTO dto, Long companyId) {

        CompanyEntity company = companyRepository.findById(companyId)
                .orElseThrow(() -> new NoSuchElementException("Empresa no encontrada"));

        ActivityEntity entity = activityMapper.toEntity(dto, company);

        entity.setAvailable(true);

        ActivityEntity saved = activityRepository.save(entity);
        return activityMapper.toDTO(saved);
    }

    public ActivityResponseDTO findById(Long id) {
        ActivityEntity entity = activityRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Actividad no encontrada"));
        return activityMapper.toDTO(entity);
    }

    public Page<CompanyResponseDTO> findByCompanyId(Long companyId, Pageable pageable) {
        return activityRepository.findByCompanyId(companyId, pageable)
                .map(activityMapper::toCompanyResponseDTO); // <-- este es el correcto
    }


    public Page<ActivityResponseDTO> findAll(Pageable pageable) {
        return activityRepository.findAllByActiveTrue(pageable)
                .map(activityMapper::toDTO);
    }

    public Page<ActivityResponseDTO> findByUserId(Long userId, Pageable pageable) {
        return activityRepository.findByUsers_Id(userId, pageable)
                .map(activityMapper::toDTO);
    }

    public ActivityResponseDTO updateAndReturn(Long id,ActivityUpdateDTO dto) {
        ActivityEntity entity = activityRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Actividad no encontrada"));

        activityMapper.updateEntityFromDTO(dto, entity);

        if (entity.getStartTime() != null && entity.getEndTime() != null &&
                entity.getEndTime().isBefore(entity.getStartTime())) {
            throw new IllegalArgumentException("La hora de fin debe ser posterior a la de inicio.");
        }

        ActivityEntity saved = activityRepository.save(entity);
        return activityMapper.toDTO(saved);
    }


    public void delete(Long id) {
        if (!activityRepository.existsById(id)) {
            throw new NoSuchElementException("Actividad no encontrada");
        }
        ActivityEntity activity = activityRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Actividad no encontrada"));

        activity.setAvailable(false);
        activity.getItinerary().setActive(false);
    }

    public ActivityResponseDTO updateActivityByCompany(Long companyId, Long activityId, ActivityUpdateDTO dto) {
        ActivityEntity activity = activityRepository.findById(activityId)
                .orElseThrow(() -> new NoSuchElementException("La actividad no existe"));

        if (activity.getCompany() == null || !activity.getCompany().getId().equals(companyId)) {
            throw new IllegalArgumentException("No tienes permiso para modificar esta actividad");
        }

        activityMapper.updateEntityFromDTO(dto, activity);
        activityRepository.save(activity);

        return activityMapper.toDTO(activity);
    }

    public void deleteActivityByCompany(Long companyId, Long activityId) {
        ActivityEntity activity = activityRepository.findById(activityId)
                .orElseThrow(() -> new NoSuchElementException("La actividad no existe"));

        if (activity.getCompany() == null || !activity.getCompany().getId().equals(companyId)) {
            throw new IllegalArgumentException("No tienes permiso para eliminar esta actividad");
        }
        activity.setAvailable(false);
        activity.getItinerary().setActive(false);
    }

    public Page<ActivityResponseDTO> findWithFilters(ActivityCategory category, LocalDate startDate, LocalDate endDate, Pageable pageable) {

        LocalDate start = (startDate != null) ? startDate : LocalDate.MIN;
        LocalDate end = (endDate != null) ? endDate : LocalDate.MAX;

        Page<ActivityEntity> entities;

        if (category != null) {
            entities = activityRepository.findByCategoryAndDateBetween(category, start, end, pageable);
        } else {
            entities = activityRepository.findByDateBetween(start, end, pageable);
        }

        return entities.map(activityMapper::toDTO);
    }

}

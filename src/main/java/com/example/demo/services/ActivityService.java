package com.example.demo.services;

import com.example.demo.DTOs.Activity.CompanyActivityCreateDTO;
import com.example.demo.DTOs.Activity.UserActivityCreateDTO;
import com.example.demo.DTOs.Activity.ActivityResponseDTO;
import com.example.demo.DTOs.Activity.ActivityUpdateDTO;
import com.example.demo.entities.ActivityEntity;
import com.example.demo.entities.CompanyEntity;
import com.example.demo.entities.ItineraryEntity;
import com.example.demo.entities.UserEntity;
import com.example.demo.mappers.ActivityMapper;
import com.example.demo.repositories.ActivityRepository;
import com.example.demo.repositories.CompanyRepository;
import com.example.demo.repositories.ItineraryRepository;
import com.example.demo.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
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

    public ActivityResponseDTO createFromUser(UserActivityCreateDTO dto) {
        ActivityEntity entity = activityMapper.toEntity(dto);

        entity.setAvailable(true);

        UserEntity user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new NoSuchElementException("Usuario no encontrado"));
        entity.setUser(user);

        if (dto.getItineraryId() != null) {
            ItineraryEntity itinerary = itineraryRepository.findById(dto.getItineraryId())
                    .orElseThrow(() -> new NoSuchElementException("Itinerario no encontrado"));
            entity.setItinerary(itinerary);
        }

        ActivityEntity saved = activityRepository.save(entity);
        return activityMapper.toDTO(saved);
    }


    public ActivityResponseDTO createFromCompany(CompanyActivityCreateDTO dto) {
        ActivityEntity entity = activityMapper.toEntity(dto);

        CompanyEntity company = companyRepository.findById(dto.getCompanyId())
                .orElseThrow(() -> new NoSuchElementException("Empresa no encontrada"));
        entity.setCompany(company);

        ActivityEntity saved = activityRepository.save(entity);
        return activityMapper.toDTO(saved);
    }

    public ActivityResponseDTO findById(Long id) {
        ActivityEntity entity = activityRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Actividad no encontrada"));
        return activityMapper.toDTO(entity);
    }

    public List<ActivityResponseDTO> findByCompanyId(Long companyId) {
        return activityRepository.findByCompanyId(companyId)
                .stream()
                .map(activityMapper::toDTO)
                .collect(Collectors.toList());
    }


    public List<ActivityResponseDTO> findAll() {
        return activityRepository.findAll()
                .stream()
                .map(activityMapper::toDTO)
                .collect(Collectors.toList());
    }

    public List<ActivityResponseDTO> findByUserId(Long userId) {
        return activityRepository.findByUserId(userId)
                .stream()
                .map(activityMapper::toDTO)
                .collect(Collectors.toList());
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
        activityRepository.deleteById(id);
    }
}

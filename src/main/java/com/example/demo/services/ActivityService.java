package com.example.demo.services;

import com.example.demo.DTOs.Activity.ActivityCreateDTO;
import com.example.demo.DTOs.Activity.ActivityResponseDTO;
import com.example.demo.DTOs.Activity.ActivityUpdateDTO;
import com.example.demo.entities.ActivityEntity;
import com.example.demo.mappers.ActivityMapper;
import com.example.demo.repositories.ActivityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class ActivityService {
    private final ActivityRepository activityRepository;
    private final ActivityMapper activityMapper;

    @Autowired
    public ActivityService(ActivityRepository activityRepository, ActivityMapper activityMapper) {
        this.activityRepository = activityRepository;
        this.activityMapper = activityMapper;
    }


    public List<ActivityResponseDTO> findAll() {
        List<ActivityEntity> entities = activityRepository.findAll();
        return activityMapper.toDTOList(entities);
    }

    public ActivityResponseDTO findById(Long id) {
        ActivityEntity entity = activityRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("No se encontró la actividad"));
        return activityMapper.toDTO(entity);
    }

    public void save(ActivityCreateDTO dto) {
        ActivityEntity entity = activityMapper.toEntity(dto);
        activityRepository.save(entity);
    }

    public void save(ActivityEntity entity) {
        activityRepository.save(entity);
    }

    public void delete(Long id) {
        ActivityEntity entity = activityRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("No se encontró la actividad"));
        activityRepository.delete(entity);
    }

    public ActivityEntity getEntityById(Long id) {
        return activityRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("No se encontró la actividad"));
    }

    public void update(Long id, ActivityUpdateDTO dto) {
        ActivityEntity existing = getEntityById(id);
        activityMapper.updateEntityFromDTO(dto, existing);
        activityRepository.save(existing);
    }
}

package com.example.demo.services;

import com.example.demo.entities.ActivityEntity;
import com.example.demo.repositories.ActivityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class ActivityService {
    private final ActivityRepository activityRepository;

    @Autowired
    public ActivityService(ActivityRepository activityRepository) {
        this.activityRepository = activityRepository;
    }

    public List<ActivityEntity> findAll(){
        return activityRepository.findAll();
    }

    public ActivityEntity findById(Long id){
        return activityRepository.findById(id).orElseThrow(()-> new NoSuchElementException("No se encontro el elemento"));
    }
}

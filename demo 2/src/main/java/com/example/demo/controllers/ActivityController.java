package com.example.demo.controllers;

import com.example.demo.entities.ActivityEntity;
import com.example.demo.services.ActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/activities")
public class ActivityController {

    private final ActivityService activityService;

    @Autowired
    public ActivityController(ActivityService activityService) {
        this.activityService = activityService;
    }

    // Obtener todas las actividades
    @GetMapping
    public ResponseEntity<List<ActivityEntity>> getAllActivities() {
        List<ActivityEntity> activities = activityService.findAll();
        return ResponseEntity.ok(activities);
    }

    // Obtener una actividad por ID
    @GetMapping("/{id}")
    public ResponseEntity<ActivityEntity> getActivityById(@PathVariable Long id) {
        ActivityEntity activity = activityService.findById(id);
        return ResponseEntity.ok(activity);
    }

    // Crear una nueva actividad
    @PostMapping
    public ResponseEntity<ActivityEntity> createActivity(@RequestBody ActivityEntity activity) {
        if (activity.getDescription() == null || activity.getDescription().isBlank()) {
            throw new IllegalArgumentException("La descripción no puede estar vacía.");
        }
        activityService.save(activity);
        return ResponseEntity.status(HttpStatus.CREATED).body(activity);
    }

    // Actualizar una actividad existente
    @PutMapping("/{id}")
    public ResponseEntity<ActivityEntity> updateActivity(@PathVariable Long id,
                                                         @RequestBody ActivityEntity updatedActivity) {
        ActivityEntity existing = activityService.findById(id);
        existing.setPrice(updatedActivity.getPrice());
        existing.setAvailability(updatedActivity.isAvailability());
        existing.setDescription(updatedActivity.getDescription());
        existing.setCategory(updatedActivity.getCategory());
        existing.setDate(updatedActivity.getDate());
        existing.setStartTime(updatedActivity.getStartTime());
        existing.setEndTime(updatedActivity.getEndTime());

        activityService.save(existing);
        return ResponseEntity.ok(existing);
    }

    // Eliminar una actividad por ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteActivity(@PathVariable Long id) {
        ActivityEntity activity = activityService.findById(id);
        activityService.delete(activity);
        return ResponseEntity.noContent().build();
    }
}

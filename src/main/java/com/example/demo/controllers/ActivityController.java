package com.example.demo.controllers;

import com.example.demo.entities.ActivityEntity;
import com.example.demo.services.ActivityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Activities", description = "Operations related to travel itinerary activities")
@RestController
@RequestMapping("/activities")
public class ActivityController {

    private final ActivityService activityService;

    @Autowired
    public ActivityController(ActivityService activityService) {
        this.activityService = activityService;
    }

    @Operation(
            summary = "Get all activities",
            description = "Returns a list of all activities registered in the system."
    )
    @ApiResponse(responseCode = "200", description = "Activity list successfully retrieved",
            content = @Content(mediaType = "application/json",
                    array = @io.swagger.v3.oas.annotations.media.ArraySchema(
                            schema = @Schema(implementation = ActivityEntity.class)
                    )))
    @GetMapping
    public ResponseEntity<List<ActivityEntity>> getAllActivities() {
        List<ActivityEntity> activities = activityService.findAll();
        return ResponseEntity.ok(activities);
    }

    @Operation(
            summary = "Get an activity by ID",
            description = "Returns a specific activity by its ID if it exists.",
            parameters = {
                    @Parameter(name = "id", description = "ID of the activity to retrieve", required = true)
            }
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Activity found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ActivityEntity.class))),
            @ApiResponse(responseCode = "404", description = "Activity not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ActivityEntity> getActivityById(@PathVariable Long id) {
        ActivityEntity activity = activityService.findById(id);
        return ResponseEntity.ok(activity);
    }

    @Operation(
            summary = "Create a new activity",
            description = "Creates a new activity linked to a user and itinerary, including price, availability, description, category, date and time.",
            requestBody = @RequestBody(
                    description = "Data of the activity to be created",
                    required = true,
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ActivityEntity.class)))
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Activity successfully created"),
            @ApiResponse(responseCode = "400", description = "Invalid data")
    })
    @PostMapping
    public ResponseEntity<ActivityEntity> createActivity(@org.springframework.web.bind.annotation.RequestBody ActivityEntity activity) {
        if (activity.getDescription() == null || activity.getDescription().isBlank()) {
            throw new IllegalArgumentException("Description must not be empty.");
        }
        activityService.save(activity);
        return ResponseEntity.status(HttpStatus.CREATED).body(activity);
    }

    @Operation(
            summary = "Update an existing activity",
            description = "Updates an existing activity using its ID and the new provided data.",
            parameters = {
                    @Parameter(name = "id", description = "ID of the activity to update", required = true)
            },
            requestBody = @RequestBody(
                    description = "Updated data of the activity",
                    required = true,
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ActivityEntity.class)))
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Activity successfully updated",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ActivityEntity.class))),
            @ApiResponse(responseCode = "404", description = "Activity not found")
    })
    @PutMapping("/{id}")
    public ResponseEntity<ActivityEntity> updateActivity(@PathVariable Long id,
                                                         @org.springframework.web.bind.annotation.RequestBody ActivityEntity updatedActivity) {
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

    @Operation(
            summary = "Delete an activity by ID",
            description = "Deletes the activity corresponding to the provided ID if it exists.",
            parameters = {
                    @Parameter(name = "id", description = "ID of the activity to delete", required = true)
            }
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Activity successfully deleted"),
            @ApiResponse(responseCode = "404", description = "Activity not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteActivity(@PathVariable Long id) {
        ActivityEntity activity = activityService.findById(id);
        activityService.delete(activity);
        return ResponseEntity.noContent().build();
    }
}

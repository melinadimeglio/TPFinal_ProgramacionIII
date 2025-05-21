package com.example.demo.controllers;

import com.example.demo.DTOs.Activity.ActivityCreateDTO;
import com.example.demo.DTOs.Activity.ActivityResponseDTO;
import com.example.demo.DTOs.Activity.ActivityUpdateDTO;
import com.example.demo.DTOs.Expense.ExpenseResponseDTO;
import com.example.demo.entities.ActivityEntity;
import com.example.demo.entities.ItineraryEntity;
import com.example.demo.entities.UserEntity;
import com.example.demo.enums.ActivityCategory;
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
                            schema = @Schema(implementation = ActivityResponseDTO.class)
                    )))
    @GetMapping
    public ResponseEntity<List<ActivityResponseDTO>> getAllActivities() {
        return ResponseEntity.ok(activityService.findAll());
    }

    @Operation(
            summary = "Get activities by user ID",
            description = "Retrieves all activities created by a specific user."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Activities retrieved successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ActivityResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "User not found or no activities for user"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ActivityResponseDTO>> findByUserId(@PathVariable Long userId) {
        List<ActivityResponseDTO> activity = activityService.findByUserId(userId);
        return ResponseEntity.ok(activity);
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
                            schema = @Schema(implementation = ActivityResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Activity not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ActivityResponseDTO> getActivityById(@PathVariable Long id) {
        return ResponseEntity.ok(activityService.findById(id));
    }

    @Operation(
            summary = "Create a new activity",
            description = "Creates a new activity linked to a user and itinerary, including price, availability, description, category, date and time.",
            requestBody = @RequestBody(
                    description = "Data of the activity to be created",
                    required = true,
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ActivityCreateDTO.class)))
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Activity successfully created"),
            @ApiResponse(responseCode = "400", description = "Invalid data")
    })
    @PostMapping
    public ResponseEntity<Void> createActivity(@RequestBody ActivityCreateDTO dto) {
        if (dto.getDescription() == null || dto.getDescription().isBlank()) {
            throw new IllegalArgumentException("Description must not be empty.");
        }
        activityService.save(dto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
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
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ActivityUpdateDTO.class)
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Activity successfully updated",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ActivityResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Activity not found")
    })
    @PutMapping("/{id}")
    public ResponseEntity<ActivityResponseDTO> updateActivity(
            @PathVariable Long id,
            @RequestBody ActivityUpdateDTO dto) {

        activityService.update(id, dto);
        ActivityResponseDTO updated = activityService.findById(id);
        return ResponseEntity.ok(updated);
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
        activityService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

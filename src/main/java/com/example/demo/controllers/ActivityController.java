package com.example.demo.controllers;

import com.example.demo.DTOs.Activity.ActivityUpdateDTO;
import com.example.demo.DTOs.Activity.CompanyActivityCreateDTO;
import com.example.demo.DTOs.Activity.UserActivityCreateDTO;
import com.example.demo.DTOs.Activity.ActivityResponseDTO;
import com.example.demo.controllers.hateoas.ActivityModelAssembler;
import com.example.demo.services.ActivityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Tag(name = "Activities", description = "Operations related to users and companies activities")
@RestController
@RequestMapping("/activities")
public class ActivityController {

    private final ActivityService activityService;
    private final ActivityModelAssembler assembler;

    @Autowired
    public ActivityController(ActivityService activityService, ActivityModelAssembler assembler) {
        this.activityService = activityService;
        this.assembler = assembler;
    }

    @Operation(
            summary = "Create an activity shared by one or more users",
            description = "This endpoint allows one or more users to create a new shared activity associated with an itinerary.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Activity details including users who will participate",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UserActivityCreateDTO.class)
                    )
            )
    )
    @PostMapping("/user")
    public ResponseEntity<ActivityResponseDTO> createFromUser(@RequestBody @Valid UserActivityCreateDTO dto) {
        ActivityResponseDTO createdActivity = activityService.createFromUser(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdActivity);
    }

    @Operation(
            summary = "Create an activity by a company",
            description = "This endpoint allows a company to create a new activity. The activity will not be associated with any user or itinerary initially.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Activity details to be created by the company",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CompanyActivityCreateDTO.class)
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Activity successfully created",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ActivityResponseDTO.class)
                    )
            ),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    @PostMapping("/company")
    public ResponseEntity<ActivityResponseDTO> createFromCompany(@RequestBody @Valid CompanyActivityCreateDTO dto) {
        ActivityResponseDTO response = activityService.createFromCompany(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }


    @Operation(
            summary = "Get all activities by company ID",
            description = "Returns a list of all activities created by the specified company."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Activities retrieved successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ActivityResponseDTO.class))
            ),
            @ApiResponse(responseCode = "404", description = "Company not found or has no activities")
    })
    @GetMapping("/company/{companyId}")
    public ResponseEntity<CollectionModel<EntityModel<ActivityResponseDTO>>> getByCompanyId(@PathVariable Long companyId) {
        List<ActivityResponseDTO> activities = activityService.findByCompanyId(companyId);

        return ResponseEntity.ok(assembler.toCollectionModelByCompany(activities, companyId));
    }

    @Operation(
            summary = "Get all activities",
            description = "Retrieves a list of all activities in the system, regardless of user or company."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "List of activities retrieved successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ActivityResponseDTO.class)
                    )
            )
    })
    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<ActivityResponseDTO>>> getAllActivities() {
        List<ActivityResponseDTO> activities = activityService.findAll();

        return ResponseEntity.ok(assembler.toCollectionModel(activities));
    }

    @Operation(
            summary = "Get an activity by ID",
            description = "Retrieves a single activity based on its unique identifier."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Activity retrieved successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ActivityResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Activity not found"
            )
    })
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<ActivityResponseDTO>> getActivityById(@PathVariable Long id) {
        ActivityResponseDTO activity = activityService.findById(id);

        return ResponseEntity.ok(assembler.toModel(activity));
    }

    @Operation(
            summary = "Get activities by user ID",
            description = "Retrieves a list of all activities created by the specified user."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Activities retrieved successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ActivityResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "No activities found for the given user ID"
            )
    })
    @GetMapping("/user/{userId}")
    public ResponseEntity<CollectionModel<EntityModel<ActivityResponseDTO>>> getActivitiesByUserId(@PathVariable Long userId) {
        List<ActivityResponseDTO> activities = activityService.findByUserId(userId);

        return ResponseEntity.ok(assembler.toCollectionModelByUser(activities, userId));
    }

    @Operation(
            summary = "Update an existing activity",
            description = "This endpoint allows updating an existing activity. Only the provided fields will be updated."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Activity updated successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ActivityResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid input data"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Activity not found"
            )
    })
    @PutMapping("/{id}")
    public ResponseEntity<ActivityResponseDTO> updateActivity(
            @PathVariable Long id,
            @RequestBody @Valid ActivityUpdateDTO dto
    ) {
        ActivityResponseDTO updated = activityService.updateAndReturn(id, dto);
        return ResponseEntity.ok(updated);
    }



    @Operation(
            summary = "Delete an activity by ID",
            description = "This endpoint deletes an activity from the system using its unique ID."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Activity deleted successfully. No content is returned in the response body."
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Activity not found"
            )
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteActivity(@PathVariable Long id) {
        activityService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Update a company activity",
            description = "Allows a company to update one of its own activities. Only the company that owns the activity can modify it.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Activity fields to update",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ActivityUpdateDTO.class)
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Activity updated successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ActivityResponseDTO.class)
                    )
            ),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "404", description = "Activity not found or company mismatch")
    })
    @PutMapping("/company/{companyId}/activities/{activityId}")
    public ResponseEntity<ActivityResponseDTO> updateActivityByCompany(
            @PathVariable Long companyId,
            @PathVariable Long activityId,
            @RequestBody @Valid ActivityUpdateDTO dto) {

        ActivityResponseDTO updated = activityService.updateActivityByCompany(companyId, activityId, dto);
        return ResponseEntity.ok(updated);
    }

    @Operation(
            summary = "Delete a company activity",
            description = "Allows a company to delete one of its own activities. Only the company that owns the activity can delete it."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Activity deleted successfully"
            ),
            @ApiResponse(responseCode = "404", description = "Activity not found or company mismatch")
    })
    @DeleteMapping("/company/{companyId}/activities/{activityId}")
    public ResponseEntity<Void> deleteActivityByCompany(
            @PathVariable Long companyId,
            @PathVariable Long activityId) {

        activityService.deleteActivityByCompany(companyId, activityId);
        return ResponseEntity.noContent().build();
    }
}

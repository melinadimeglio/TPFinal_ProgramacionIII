package com.example.demo.controllers;

import com.example.demo.DTOs.Activity.ActivityUpdateDTO;
import com.example.demo.DTOs.Activity.Request.CompanyActivityCreateDTO;
import com.example.demo.DTOs.Activity.Request.UserActivityCreateDTO;
import com.example.demo.DTOs.Activity.Response.ActivityResponseDTO;
import com.example.demo.controllers.hateoas.ActivityModelAssembler;
import com.example.demo.enums.ActivityCategory;
import com.example.demo.security.entities.CredentialEntity;
import com.example.demo.services.ActivityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springdoc.ui.SpringDocUIException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.PagedModel;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@Tag(name = "Activities", description = "Operations related to users and companies activities")
@RestController
@RequestMapping("/activities")
public class ActivityController {

    private final ActivityService activityService;
    private final ActivityModelAssembler assembler;
    private final PagedResourcesAssembler<ActivityResponseDTO> pagedResourcesAssembler;

    @Autowired
    public ActivityController(ActivityService activityService, ActivityModelAssembler assembler, PagedResourcesAssembler<ActivityResponseDTO> pagedResourcesAssembler) {
        this.activityService = activityService;
        this.assembler = assembler;
        this.pagedResourcesAssembler = pagedResourcesAssembler;
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
    @PreAuthorize("hasAuthority('CREAR_ACTIVIDAD_USUARIO')")
    @PostMapping("/user")
    public ResponseEntity<ActivityResponseDTO> createFromUser(
            @RequestBody @Valid UserActivityCreateDTO dto,
            @AuthenticationPrincipal CredentialEntity credential) {

        Long myUserId = credential.getUser().getId();
        ActivityResponseDTO createdActivity = activityService.createFromUser(dto, myUserId);
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
    @PreAuthorize("hasAuthority('CREAR_ACTIVIDAD_EMPRESA')")
    @PostMapping("/company")
    public ResponseEntity<ActivityResponseDTO> createFromCompany(
            @RequestBody @Valid CompanyActivityCreateDTO dto,
            @AuthenticationPrincipal CredentialEntity credential) {

        Long companyId = credential.getCompany().getId();

        ActivityResponseDTO response = activityService.createFromCompany(dto, companyId);
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
    @PreAuthorize("hasAuthority('VER_ACTIVIDAD_EMPRESA')")
    @GetMapping("/company/{companyId}")
    public ResponseEntity<CollectionModel<EntityModel<ActivityResponseDTO>>> getByCompanyId(@PathVariable Long companyId) {
        List<ActivityResponseDTO> activities = activityService.findByCompanyId(companyId);

        return ResponseEntity.ok(assembler.toCollectionModelByCompany(activities, companyId));
    }

    @Operation(
            summary = "Get all activities with optional filters",
            description = "Returns all activities, optionally filtered by category and/or date."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Activities retrieved successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ActivityResponseDTO.class)
                    )
            )
    })
    @PreAuthorize("hasAuthority('VER_TODAS_ACTIVIDADES')")
    @GetMapping
    public ResponseEntity<PagedModel<EntityModel<ActivityResponseDTO>>> getAllActivities(
            Pageable pageable,
            @RequestParam(required = false) ActivityCategory category,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        Page<ActivityResponseDTO> activities = activityService.findWithFilters(category, startDate, endDate, pageable);
        PagedModel model = pagedResourcesAssembler.toModel(activities, assembler);
        return ResponseEntity.ok(model);
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
    @PreAuthorize("hasAuthority('VER_ACTIVIDAD')")
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
    @PreAuthorize("hasAuthority('VER_ACTIVIDAD_USUARIO')")
    @GetMapping("/user/{userId}")
    public ResponseEntity<CollectionModel<EntityModel<ActivityResponseDTO>>> getActivitiesByUserId(
            @PathVariable Long userId,
            @AuthenticationPrincipal CredentialEntity credential) {

        if (credential.getUser() == null || !credential.getUser().getId().equals(userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

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
    @PreAuthorize("hasAuthority('MODIFICAR_ACTIVIDADES_USUARIO')")
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
    @PreAuthorize("hasAuthority('ELIMINAR_ACTIVIDAD_USUARIO')")
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
    @PreAuthorize("hasAuthority('MODIFICAR_ACTIVIDADES_EMPRESA')")
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
    @PreAuthorize("hasAuthority('ELIMINAR_ACTIVIDAD_EMPRESA')")
    @DeleteMapping("/company/{companyId}/activities/{activityId}")
    public ResponseEntity<Void> deleteActivityByCompany(
            @PathVariable Long companyId,
            @PathVariable Long activityId) {

        activityService.deleteActivityByCompany(companyId, activityId);
        return ResponseEntity.noContent().build();
    }
}

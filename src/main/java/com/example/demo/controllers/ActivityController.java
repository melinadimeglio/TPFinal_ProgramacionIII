package com.example.demo.controllers;

import com.example.demo.DTOs.Activity.ActivityUpdateDTO;
import com.example.demo.DTOs.Activity.CompanyActivityUpdateDTO;
import com.example.demo.DTOs.Filter.ActivityFilterDTO;
import com.example.demo.DTOs.Activity.Request.CompanyActivityCreateDTO;
import com.example.demo.DTOs.Activity.Request.UserActivityCreateDTO;
import com.example.demo.DTOs.Activity.Response.ActivityCompanyResponseDTO;
import com.example.demo.DTOs.Activity.Response.ActivityResponseDTO;
import com.example.demo.DTOs.Itinerary.Response.ItineraryResponseDTO;
import com.example.demo.controllers.hateoas.ActivityCompanyModelAssembler;
import com.example.demo.controllers.hateoas.ActivityModelAssembler;
import com.example.demo.entities.CompanyEntity;
import com.example.demo.entities.UserEntity;
import com.example.demo.enums.ActivityCategory;
import com.example.demo.exceptions.OwnershipException;
import com.example.demo.exceptions.ReservationException;
import com.example.demo.repositories.ItineraryRepository;
import com.example.demo.security.entities.CredentialEntity;
import com.example.demo.services.ActivityService;
import com.example.demo.services.ItineraryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.PagedModel;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@Tag(name = "Activities", description = "Operations related to users and companies activities")
@RestController
@RequestMapping("/activities")
public class ActivityController {

    private final ActivityService activityService;
    private final ActivityModelAssembler assembler;
    private final PagedResourcesAssembler<ActivityResponseDTO> pagedResourcesAssembler;
    private final PagedResourcesAssembler<ActivityCompanyResponseDTO> pagedResourcesAssemblerCompany;
    private final ItineraryService itineraryService;
    private final ItineraryRepository itineraryRepository;
    private final ActivityCompanyModelAssembler activityCompanyAssembler;

    @Autowired
    public ActivityController(ActivityService activityService, ActivityModelAssembler assembler, PagedResourcesAssembler<ActivityResponseDTO> pagedResourcesAssembler, PagedResourcesAssembler<ActivityCompanyResponseDTO> pagedResourcesAssemblerCompany, ItineraryService itineraryService, ItineraryRepository itineraryRepository,
                              ActivityCompanyModelAssembler activityCompanyAssembler) {
        this.activityService = activityService;
        this.assembler = assembler;
        this.pagedResourcesAssembler = pagedResourcesAssembler;
        this.pagedResourcesAssemblerCompany = pagedResourcesAssemblerCompany;
        this.itineraryService = itineraryService;
        this.itineraryRepository = itineraryRepository;
        this.activityCompanyAssembler = activityCompanyAssembler ;
    }

    @Operation(
            summary = "Create an activity shared by one or more users",
            description = "This endpoint allows one or more users to create a new shared activity associated with an itinerary. " +
                    "Only the owner of the itinerary can associate activities to it."
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
            @ApiResponse(
                    responseCode = "403",
                    description = "Access denied - You are not allowed to associate activities to this itinerary"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid input data"
            )
    })
    @PreAuthorize("hasAuthority('CREAR_ACTIVIDAD_USUARIO')")
    @PostMapping("/user")
    public ResponseEntity<ActivityResponseDTO> createFromUser(
            @RequestBody @Valid UserActivityCreateDTO dto,
            @AuthenticationPrincipal CredentialEntity credential, Pageable pageable) {

        Long myUserId = credential.getUser().getId();
        Set<ItineraryResponseDTO> itineraries = itineraryService.findByUserId(myUserId, pageable).toSet();

        Optional<ItineraryResponseDTO> itinerario = itineraries.stream()
                .filter(itinerary -> itinerary.getItineraryDate().equals(dto.getDate()))
                .findFirst();

        if (itinerario.isEmpty()){
            throw new ReservationException("There is no itinerary for the activity date. Please create one first..");
        }

        ActivityResponseDTO createdActivity = activityService.createFromUser(dto, myUserId, itinerario.get().getId());
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
    public ResponseEntity<ActivityCompanyResponseDTO> createActivityFromCompany(
            @RequestBody @Valid CompanyActivityCreateDTO dto,
            @AuthenticationPrincipal CredentialEntity credential) {

        Long companyId = 0L;

        List<String> authorities = credential.getAuthorities()
                .stream()
                .map(Object::toString)
                .toList();

        if (authorities.contains("ROLE_ADMIN")){
            companyId = dto.getCompanyId();
        }else if (authorities.contains("ROLE_COMPANY")){
            if (credential.getCompany() == null) {
                throw new RuntimeException("The company is not associated with the user COMPANY.");
            }
            companyId = credential.getCompany().getId();
        }

        ActivityCompanyResponseDTO response = activityService.createFromCompanyService(dto, companyId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(
            summary = "Get activities by company ID",
            description = "Returns a paginated list of activities created by the authenticated company only."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Activities retrieved successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ActivityResponseDTO.class))),
            @ApiResponse(responseCode = "403", description = "Access denied - Cannot view other companies' activities")
    })
    @PreAuthorize("hasAuthority('VER_ACTIVIDAD_EMPRESA')")
    @GetMapping("/company/{companyId}")
    public ResponseEntity<PagedModel<EntityModel<ActivityCompanyResponseDTO>>> getByCompanyId(
            @PathVariable Long companyId,
            @AuthenticationPrincipal CredentialEntity credential,
            Pageable pageable) {

        Optional <CompanyEntity> myCompanyId = Optional.ofNullable(credential.getCompany());
        boolean isAdmin = credential.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if ((!isAdmin && myCompanyId.isEmpty()) ||
                (!isAdmin && !myCompanyId.get().getId().equals(companyId))) {
            throw new OwnershipException("You do not have permission to access this resource.");
        }

        Page<ActivityCompanyResponseDTO> activities = activityService.findByCompanyId(companyId, pageable);
        PagedModel<EntityModel<ActivityCompanyResponseDTO>> model = pagedResourcesAssemblerCompany.toModel(activities);
        return ResponseEntity.ok(model);
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
            Pageable pageable) {

        Page<ActivityResponseDTO> activities = activityService.findAll(pageable);
        PagedModel<EntityModel<ActivityResponseDTO>> model = pagedResourcesAssembler.toModel(activities, assembler);
        return ResponseEntity.ok(model);
    }


    @PreAuthorize("hasAuthority('VER_TODAS_ACTIVIDADES_EMPRESA')")
    @GetMapping("/company")
    public ResponseEntity<PagedModel<EntityModel<ActivityCompanyResponseDTO>>> getAllActivitiesCompany(
            Pageable pageable,
            @RequestParam(required = false) ActivityCategory category,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) Long availableQuantity
    ) {
        Page<ActivityCompanyResponseDTO> activities = activityService.findAllCompany(
                category, startDate, endDate, minPrice, maxPrice, availableQuantity, pageable
        );

        PagedModel<EntityModel<ActivityCompanyResponseDTO>> model =
                pagedResourcesAssemblerCompany.toModel(activities, activityCompanyAssembler);

        return ResponseEntity.ok(model);
    }


    @Operation(
            summary = "Get all inactive activities",
            description = "Retrieves a paginated list of all inactive activities in the system. Requires the 'VER_TODAS_ACTIVIDADES' authority."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Activities retrieved successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ActivityResponseDTO.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - user not authenticated"),
            @ApiResponse(responseCode = "403", description = "Forbidden - insufficient permissions")
    })

    @PreAuthorize("hasAuthority('VER_TODAS_ACTIVIDADES')")
    @GetMapping("/inactive")
    public ResponseEntity<PagedModel<EntityModel<ActivityResponseDTO>>> getAllActivitiesInactive(
            Pageable pageable) {

        Page<ActivityResponseDTO> activities = activityService.findAllInactive(pageable);
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
                    responseCode = "403",
                    description = "Forbidden: You are not allowed to view this activity"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Activity not found"
            )
    })
    @PreAuthorize("hasAuthority('VER_ACTIVIDAD')")
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<ActivityResponseDTO>> getActivityById(
            @PathVariable Long id,
            @AuthenticationPrincipal CredentialEntity credential) {

        ActivityResponseDTO activity = activityService.findById(id);

        boolean isUserAuthorized = credential.getUser() != null &&
                activity.getUserIds() != null &&
                activity.getUserIds().contains(credential.getUser().getId());

        boolean isCompanyAuthorized = credential.getCompany() != null &&
                activity.getCompanyId() != null &&
                activity.getCompanyId().equals(credential.getCompany().getId());

        boolean isAdmin = credential.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (!isUserAuthorized && !isCompanyAuthorized && !isAdmin) {
            throw new OwnershipException("You do not have permission to access this resource.");
        }

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
    public ResponseEntity<PagedModel<EntityModel<ActivityResponseDTO>>> getActivitiesByUserId(
            @PathVariable Long userId,
            @AuthenticationPrincipal CredentialEntity credential,
            ActivityFilterDTO filters,
            Pageable pageable) {

        Optional <UserEntity> myUserId = Optional.ofNullable(credential.getUser());
        boolean isAdmin = credential.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if ((!isAdmin && myUserId.isEmpty()) ||
                (!isAdmin && !myUserId.get().getId().equals(userId))) {
            throw new OwnershipException("You do not have permission to access this resource.");

        }

        Page<ActivityResponseDTO> activities = activityService.findByUserIdWithFilters(userId, filters, pageable);
        PagedModel<EntityModel<ActivityResponseDTO>> model = pagedResourcesAssembler.toModel(activities, assembler);
        return ResponseEntity.ok(model);
    }


    @Operation(
            summary = "Update an existing activity",
            description = "This endpoint allows updating an existing activity only if it belongs to the authenticated user. Only the provided fields will be updated."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Activity updated successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ActivityResponseDTO.class))),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "Activity not found")
    })
    @PreAuthorize("hasAuthority('MODIFICAR_ACTIVIDADES_USUARIO')")
    @PutMapping("/{id}")
    public ResponseEntity<ActivityResponseDTO> updateActivity(
            @PathVariable Long id,
            @RequestBody @Valid ActivityUpdateDTO dto,
            @AuthenticationPrincipal CredentialEntity credential
    ) {
        Long myUserId = credential.getUser().getId();
        ActivityResponseDTO updated = activityService.updateAndReturnIfOwned(id, dto, myUserId);
        return ResponseEntity.ok(updated);
    }

    @Operation(
            summary = "Delete an activity by ID",
            description = "Deletes an activity only if it belongs to the authenticated user."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Activity deleted successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "Activity not found")
    })
    @PreAuthorize("hasAuthority('ELIMINAR_ACTIVIDAD_USUARIO')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteActivity(
            @PathVariable Long id,
            @AuthenticationPrincipal CredentialEntity credential) {

        Long myUserId = credential.getUser().getId();
        activityService.deleteIfOwned(id, myUserId);
        return ResponseEntity.noContent().build();
    }


    @Operation(
            summary = "Restore a deleted activity",
            description = "Reactivates an activity previously soft-deleted, only if it belongs to the authenticated user."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Activity restored successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "Activity not found")
    })
    @PreAuthorize("hasAuthority('RESTAURAR_ACTIVIDAD_USUARIO')")
    @PutMapping("/restore/{id}")
    public ResponseEntity<Void> restoreActivity(
            @PathVariable Long id,
            @AuthenticationPrincipal CredentialEntity credential) {

        Long myUserId = credential.getUser().getId();
        activityService.restoreIfOwned(id, myUserId);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Update a company activity",
            description = "Allows the authenticated company to update one of its own activities."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Activity updated successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ActivityResponseDTO.class))),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "Activity not found")
    })
    @PreAuthorize("hasAuthority('MODIFICAR_ACTIVIDADES_EMPRESA')")
    @PutMapping("/company/{companyId}/activities/{activityId}")
    public ResponseEntity<ActivityResponseDTO> updateActivityByCompany(
            @PathVariable Long companyId,
            @PathVariable Long activityId,
            @RequestBody @Valid CompanyActivityUpdateDTO dto,
            @AuthenticationPrincipal CredentialEntity credential) {

        Long myCompanyId = credential.getCompany().getId();

        if (!myCompanyId.equals(companyId)) {
            throw new OwnershipException("You do not have permission to access this resource.");
        }

        ActivityResponseDTO updated = activityService.updateActivityByCompany(myCompanyId, activityId, dto);
        return ResponseEntity.ok(updated);
    }


    @Operation(
            summary = "Delete a company activity",
            description = "Allows the authenticated company to delete one of its own activities."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Activity deleted successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "Activity not found")
    })
    @PreAuthorize("hasAuthority('ELIMINAR_ACTIVIDAD_EMPRESA')")
    @DeleteMapping("/company/{companyId}/{activityId}")
    public ResponseEntity<Void> deleteActivityByCompany(
            @PathVariable Long companyId,
            @PathVariable Long activityId,
            @AuthenticationPrincipal CredentialEntity credential) {

        Long myCompanyId = credential.getCompany().getId();

        if (!myCompanyId.equals(companyId)) {
            throw new OwnershipException("You do not have permission to access this resource.");
        }

        activityService.deleteActivityByCompany(myCompanyId, activityId);
        return ResponseEntity.noContent().build();
    }


    @Operation(
            summary = "Restore a company activity",
            description = "Allows the authenticated company to restore one of its own previously deleted activities."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Activity restored successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "Activity not found")
    })
    @PreAuthorize("hasAuthority('RESTAURAR_ACTIVIDAD_EMPRESA')")
    @PutMapping("/company/{companyId}/{activityId}/restore")
    public ResponseEntity<Void> restoreActivityByCompany(
            @PathVariable Long companyId,
            @PathVariable Long activityId,
            @AuthenticationPrincipal CredentialEntity credential) {

        Long myCompanyId = credential.getCompany().getId();

        if (!myCompanyId.equals(companyId)) {
            throw new OwnershipException("You do not have permission to access this resource.");
        }

        activityService.restoreActivityByCompany(myCompanyId, activityId);
        return ResponseEntity.noContent().build();
    }

}

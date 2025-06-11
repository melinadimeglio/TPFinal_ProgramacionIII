package com.example.demo.controllers;


import com.example.demo.DTOs.RecommendationDTO;
import com.example.demo.DTOs.Trip.Request.TripCreateDTO;
import com.example.demo.DTOs.Trip.Response.TripResponseDTO;
import com.example.demo.DTOs.Trip.TripUpdateDTO;
import com.example.demo.controllers.hateoas.TripModelAssembler;
import com.example.demo.entities.TripEntity;
import com.example.demo.entities.UserEntity;
import com.example.demo.repositories.TripRepository;
import com.example.demo.repositories.UserRepository;
import com.example.demo.security.entities.CredentialEntity;
import com.example.demo.services.RecommendationService;
import com.example.demo.services.TripService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Tag(name = "Trips", description = "Operations related to users trips")
@RestController
@RequestMapping("/trips")
public class TripController {

    private final TripService tripService;
    private final RecommendationService recommendationService;
    private final UserRepository userRepository;
    private final TripRepository tripRepository;
    private final TripModelAssembler assembler;
    private final PagedResourcesAssembler<TripResponseDTO> pagedResourcesAssembler;
    private final PagedResourcesAssembler<RecommendationDTO> pagedResourcesAssemblerRec;

    @Autowired
    public TripController(TripService tripService, RecommendationService recommendationService, UserRepository userRepository, TripRepository tripRepository, TripModelAssembler assembler, PagedResourcesAssembler<TripResponseDTO> pagedResourcesAssembler, PagedResourcesAssembler<RecommendationDTO> pagedResourcesAssemblerRec) {
        this.tripService = tripService;
        this.recommendationService = recommendationService;
        this.userRepository = userRepository;
        this.tripRepository = tripRepository;
        this.assembler = assembler;
        this.pagedResourcesAssembler = pagedResourcesAssembler;
        this.pagedResourcesAssemblerRec = pagedResourcesAssemblerRec;
    }

    @Operation(summary = "Get all trips", description = "Returns a list of all trips.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of trips retrieved successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = TripResponseDTO.class)))
    })
    @PreAuthorize("hasAuthority('VER_VIAJES')")
    @GetMapping
    public ResponseEntity<PagedModel<EntityModel<TripResponseDTO>>> getAllTrips(Pageable pageable) {
        Page<TripResponseDTO> trips = tripService.findAll(pageable);
        PagedModel<EntityModel<TripResponseDTO>> model = pagedResourcesAssembler.toModel(trips, assembler);
        return ResponseEntity.ok(model);
    }

    @Operation(summary = "Get a trip by ID", description = "Returns a specific trip by its ID, only if it belongs to the authenticated user.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Trip found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = TripResponseDTO.class))),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "Trip not found")
    })
    @PreAuthorize("hasAuthority('VER_VIAJE')")
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<TripResponseDTO>> getTripById(
            @PathVariable Long id,
            @AuthenticationPrincipal CredentialEntity credential) {

        Long userId = credential.getUser().getId();

        TripResponseDTO trip = tripService.findByIdForUser(id, userId);

        return ResponseEntity.ok(assembler.toModel(trip));
    }



    @Operation(
            summary = "Create a new trip",
            description = "Creates a new trip and returns the created trip.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Trip data to create",
                    required = true,
                    content = @Content(schema = @Schema(implementation = TripCreateDTO.class))
            )
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Trip created successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = TripResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    @PreAuthorize("hasAuthority('CREAR_VIAJE')")
    @PostMapping
    public ResponseEntity<TripResponseDTO> createTrip(
            @org.springframework.web.bind.annotation.RequestBody @Valid TripCreateDTO tripCreateDTO,
            @AuthenticationPrincipal CredentialEntity credential) {

        Long myUserId = credential.getUser().getId();

        TripResponseDTO responseDTO = tripService.save(tripCreateDTO, myUserId);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    }


    @Operation(
            summary = "Get trips by user ID",
            description = "Returns all trips associated with a specific user ID."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Trips found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = TripResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden - User not authorized to access this resource"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User not found"
            )
    })
    @PreAuthorize("hasAuthority('VER_VIAJE_USUARIO')")
    @GetMapping("/user/{userId}")
    public ResponseEntity<PagedModel<EntityModel<TripResponseDTO>>> getTripsByUserId(
            @PathVariable Long userId,
            @RequestParam(required = false) String destination,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)LocalDate date,
            @AuthenticationPrincipal CredentialEntity credential,
            Pageable pageable) {

        if (credential.getUser() == null || !credential.getUser().getId().equals(userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        Page<TripResponseDTO> trips = tripService.findByUserId(userId, destination, date, pageable);
        PagedModel<EntityModel<TripResponseDTO>> model = pagedResourcesAssembler.toModel(trips, assembler);
        return ResponseEntity.ok(model);
    }

    @Operation(summary = "Update a trip by ID", description = "Updates a trip by its ID only if it belongs to the authenticated user.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Trip updated successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = TripResponseDTO.class))),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "Trip not found"),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    @PreAuthorize("hasAuthority('MODIFICAR_VIAJE')")
    @PutMapping("/{id}")
    public ResponseEntity<TripResponseDTO> updateTrip(
            @Parameter(description = "ID of the trip to update", required = true)
            @PathVariable Long id,
            @org.springframework.web.bind.annotation.RequestBody @Valid TripUpdateDTO tripUpdateDTO,
            @AuthenticationPrincipal CredentialEntity credential) {

        Long userId = credential.getUser().getId();
        TripResponseDTO updatedTrip = tripService.updateIfBelongsToUser(id, tripUpdateDTO, userId);
        return ResponseEntity.ok(updatedTrip);
    }

    @Operation(summary = "Delete a trip by ID", description = "Performs a soft delete of a trip by its ID, only if it belongs to the authenticated user.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Trip deleted successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "Trip not found")
    })
    @PreAuthorize("hasAuthority('ELIMINAR_VIAJE')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTrip(
            @Parameter(description = "ID of the trip to delete", required = true)
            @PathVariable Long id,
            @AuthenticationPrincipal CredentialEntity credential) {

        Long userId = credential.getUser().getId();
        tripService.softDeleteIfBelongsToUser(id, userId);

        return ResponseEntity.noContent().build();
    }



    @Operation(
            summary = "Restore a trip",
            description = "Reactivates a trip that was previously deleted (soft-deleted), only if it belongs to the authenticated user."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Trip restored successfully. No content is returned in the response body."
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Access denied"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Trip not found"
            )
    })
    @PreAuthorize("hasAuthority('RESTAURAR_VIAJE')")
    @PutMapping("/restore/{id}")
    public ResponseEntity<Void> restoreTrip(
            @Parameter(description = "ID of the trip to restore") @PathVariable Long id,
            @AuthenticationPrincipal CredentialEntity credential) {

        Long userId = credential.getUser().getId();
        tripService.restoreIfBelongsToUser(id, userId);
        return ResponseEntity.noContent().build();
    }


    @Operation(
            summary = "Obtener recomendaciones de actividades para un viaje",
            description = "Devuelve una lista de actividades sugeridas en base al destino, fechas u otros datos del viaje especificado."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Lista de recomendaciones generada exitosamente",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = RecommendationDTO.class))
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Viaje no encontrado"
            )
    })
    @PreAuthorize("hasAuthority('OBTENER_RECOMENDACIONES_VIAJE')")
    @GetMapping("/{tripId}/recommendations")
    public ResponseEntity<PagedModel<EntityModel<RecommendationDTO>>> getRecommendations(@PathVariable Long tripId, Pageable pageable){
        Page<RecommendationDTO> recomemendations = recommendationService.getRecommendationsForTrip(tripId, pageable);
        return ResponseEntity.ok(pagedResourcesAssemblerRec.toModel(recomemendations));
    }

    @PreAuthorize("hasAuthority('OBTENER_RECOMENDACIONES_FILTRADAS')")
    @GetMapping("/{tripId}/recommendations/filtered")
    public ResponseEntity<PagedModel<EntityModel<RecommendationDTO>>> getFilteredRecommendations(@PathVariable Long tripId, Pageable pageable){
        Page<RecommendationDTO> recomendations = recommendationService.getRecommendationsForTrip(tripId, pageable);
        TripEntity trip = tripService.getTripById(tripId);

        Set<UserEntity> users = trip.getUsers();

        Set<String> allPreferences = users.stream()
                .flatMap(user -> user.getPreferencias().stream())
                .map(pref -> pref.getKindApi().toLowerCase())
                .collect(Collectors.toSet());

        System.out.println("Usuarios del viaje: " + users.size());
        users.forEach(u -> System.out.println(u.getPreferencias()));

        List<RecommendationDTO> filteredRecommendations = recomendations.stream()
                .filter(rec -> rec.getCategories().stream()
                        .map(cat -> cat.getName().toLowerCase())
                        .anyMatch(allPreferences::contains))
                .collect(Collectors.toList());

        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), filteredRecommendations.size());
        List<RecommendationDTO> paged = filteredRecommendations.subList(start, end);

        Page<RecommendationDTO> pagedResult = new PageImpl<>(paged, pageable, filteredRecommendations.size());

        return ResponseEntity.ok(pagedResourcesAssemblerRec.toModel(pagedResult));
    }

    //solo para hateoas
    @Operation(summary = "Get all trips (paged - HATEOAS only)", description = "Retrieves all trips in paginated format using HATEOAS structure. Currently not implemented.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "404", description = "Endpoint not yet implemented")
    })
    @GetMapping("/paged")
    public ResponseEntity<PagedModel<EntityModel<TripResponseDTO>>> getAllTrips() {
        return ResponseEntity.notFound().build();
    }

}



package com.example.demo.controllers;

import com.example.demo.DTOs.Reservation.Request.ReservationCreateDTO;
import com.example.demo.DTOs.Reservation.Response.ReservationResponseDTO;
import com.example.demo.security.entities.CredentialEntity;
import com.example.demo.services.ReservationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reservation")
public class ReservationController {

    private final ReservationService reservationService;

    @Autowired
    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @Operation(
            summary = "Create a new reservation",
            description = "Allows a user to reserve (and pay for) an activity."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Reservation successfully created",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ReservationResponseDTO.class)
                    )
            ),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    @PreAuthorize("hasAuthority('CREAR_RESERVA')")
    @PostMapping
    public ResponseEntity<ReservationResponseDTO> createReservation(
            @RequestBody @Valid ReservationCreateDTO dto,
            @AuthenticationPrincipal CredentialEntity credential) {

        Long myUserId = credential.getUser().getId();
        ReservationResponseDTO reservation = reservationService.createReservation(dto, myUserId);
        return ResponseEntity.status(HttpStatus.CREATED).body(reservation);
    }

    @Operation(
            summary = "Cancel a reservation",
            description = "Cancels an existing reservation by its ID."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reservation cancelled successfully"),
            @ApiResponse(responseCode = "404", description = "Reservation not found")
    })
    @PreAuthorize("hasAuthority('CANCELAR_RESERVA')")
    @PutMapping("/{id}/cancel")
    public ResponseEntity<Void> cancelReservation(@PathVariable Long id) {
        reservationService.cancelReservation(id);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Get all reservations", description = "Retrieve all reservations (admin access).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reservations retrieved successfully")
    })
    @PreAuthorize("hasAuthority('VER_TODAS_RESERVAS')")
    @GetMapping
    public ResponseEntity<List<ReservationResponseDTO>> getAllReservations() {
        List<ReservationResponseDTO> reservations = reservationService.findAll();
        return ResponseEntity.ok(reservations);
    }

    @Operation(summary = "Get my reservations", description = "Retrieve reservations for the logged-in user.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reservations retrieved successfully")
    })
    @PreAuthorize("hasAuthority('VER_RESERVAS_USUARIO')")
    @GetMapping("/my")
    public ResponseEntity<List<ReservationResponseDTO>> getMyReservations(@AuthenticationPrincipal CredentialEntity credential) {
        Long myUserId = credential.getUser().getId();
        List<ReservationResponseDTO> reservations = reservationService.findByUserId(myUserId);
        return ResponseEntity.ok(reservations);
    }

    @Operation(summary = "Get reservations by company", description = "Retrieve reservations for a specific company.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reservations retrieved successfully")
    })
    @PreAuthorize("hasAuthority('VER_RESERVAS_EMPRESA')")
    @GetMapping("/company/{companyId}")
    public ResponseEntity<List<ReservationResponseDTO>> getReservationsByCompany(@PathVariable Long companyId) {
        List<ReservationResponseDTO> reservations = reservationService.findByCompanyId(companyId);
        return ResponseEntity.ok(reservations);
    }


}


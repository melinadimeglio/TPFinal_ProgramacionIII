package com.example.demo.controllers;

import com.example.demo.DTOs.Trip.TripInvitationDTO;
import com.example.demo.security.entities.CredentialEntity;
import com.example.demo.services.TripInvitationService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Trip Invitations", description = "Operations related to trip invitations")
@RestController
@RequestMapping("/tripinvitation")
public class TripInvitationController {


    private final TripInvitationService tripInvitationService;

    public TripInvitationController(TripInvitationService tripInvitationService) {
        this.tripInvitationService = tripInvitationService;
    }

    @PreAuthorize("hasAuthority('ENVIAR_INVITACION')")
    @PostMapping("/{tripId}/invite/{receiverId}")
    public ResponseEntity<Void> sendTripInvitation (
            @PathVariable Long receiverId,
            @PathVariable Long tripId,
            @AuthenticationPrincipal CredentialEntity credential
            ){

        String email = credential.getEmail();
        tripInvitationService.sendInvitation(email, receiverId, tripId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PreAuthorize("hasAuthority('VER_INVITACIONES_PENDIENTES')")
    @GetMapping("/pending")
    public ResponseEntity<List<TripInvitationDTO>> getTripInvitations (
            @AuthenticationPrincipal CredentialEntity credential
    ){
        String email = credential.getEmail();
        List<TripInvitationDTO> pendingInvitations = tripInvitationService.getPendingInvitations(email);
        return ResponseEntity.ok(pendingInvitations);
    }

    @PreAuthorize("hasAuthority('ACEPTAR_INVITACION_VIAJE')")
    @PutMapping("/accept/{invitationId}")
    public ResponseEntity<Long> acceptTripInvitation (
            @PathVariable Long invitationId,
            @AuthenticationPrincipal CredentialEntity credential
    ){

        String email = credential.getEmail();
        Long tripId = tripInvitationService.acceptInvitation(email, invitationId);

        return ResponseEntity.ok(tripId);
    }

    @PreAuthorize("hasAuthority('RECHAZAR_INVITACION_VIAJE')")
    @PutMapping("/deny/{invitationId}")
    public ResponseEntity<Void> denyTripInvitation (
            @PathVariable Long invitationId,
            @AuthenticationPrincipal CredentialEntity credential
    ){

        String email = credential.getEmail();
        tripInvitationService.denyInvitation(email, invitationId);

        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAuthority('VER_INVITACIONES_VIAJES_PENDIENTES')")
    @GetMapping("/sent/{tripId}")
    public ResponseEntity<List<TripInvitationDTO>> getSentInvitations(
            @PathVariable Long tripId,
            @AuthenticationPrincipal CredentialEntity credential) {

        String email = credential.getEmail();
        return ResponseEntity.ok(tripInvitationService.getSentInvitations(email, tripId));
    }

}

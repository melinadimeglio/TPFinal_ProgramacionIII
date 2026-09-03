package com.example.demo.controllers;

import com.example.demo.DTOs.FriendRequest.FriendRequestDTO;
import com.example.demo.security.entities.CredentialEntity;
import com.example.demo.services.FriendRequestService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;


@Tag(name = "Friend Requests", description = "Operations related to friend requests")
@RestController
@RequestMapping("/friendrequest")
public class FriendRequestController {

    private final FriendRequestService friendRequestService;

    public FriendRequestController(FriendRequestService friendRequestService) {
        this.friendRequestService = friendRequestService;
    }

    @PreAuthorize("hasAuthority('ENVIAR_SOLICITUD')")
    @PostMapping("/{receiverId}")
    public ResponseEntity<Void> sendRequest(
            @PathVariable Long receiverId,
            @AuthenticationPrincipal CredentialEntity credential) {

        String email = credential.getEmail();
        friendRequestService.sendRequest(email, receiverId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PreAuthorize("hasAuthority('VER_SOLICITUDES_PENDIENTES')")
    @GetMapping
    public ResponseEntity<List<FriendRequestDTO>> getPending(
            @AuthenticationPrincipal CredentialEntity credential){

        String email = credential.getEmail();
        List<FriendRequestDTO> pending = friendRequestService.getPendingRequest(email);
        return ResponseEntity.ok(pending);
    }

    @PreAuthorize("hasAuthority('VER_SOLICITUDES_PENDIENTES')")
    @GetMapping("/requests/sent")
    public ResponseEntity<List<FriendRequestDTO>> getSentRequests(
            @AuthenticationPrincipal CredentialEntity credential) {
        String email = credential.getEmail();
        return ResponseEntity.ok(friendRequestService.getSentRequests(email));
    }

    @PreAuthorize("hasAuthority('ACEPTAR_SOLICITUD')")
    @PutMapping("/{requestId}/accept")
    public ResponseEntity<Void> acceptRequest(
            @PathVariable Long requestId,
            @AuthenticationPrincipal CredentialEntity credential) {

        String email = credential.getEmail();
        friendRequestService.acceptRequest(email, requestId);
        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }

    @PreAuthorize("hasAuthority('RECHAZAR_SOLICITUD')")
    @PutMapping("/{requestId}/denied")
    public ResponseEntity<Void> denyRequest(
            @PathVariable Long requestId,
            @AuthenticationPrincipal CredentialEntity credential) {

        String email = credential.getEmail();
        friendRequestService.denyRequest(email, requestId);
        return ResponseEntity.noContent().build();
    }

}

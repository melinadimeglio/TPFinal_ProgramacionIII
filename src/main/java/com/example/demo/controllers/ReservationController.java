package com.example.demo.controllers;

import com.example.demo.DTOs.GlobalError.ErrorResponseDTO;
import com.example.demo.DTOs.Reservation.Request.ReservationCreateDTO;
import com.example.demo.DTOs.Reservation.Response.ReservationResponseDTO;
import com.example.demo.exceptions.ReservationException;
import com.example.demo.security.entities.CredentialEntity;
import com.example.demo.services.MPService;
import com.example.demo.services.ReservationService;
import com.mercadopago.client.payment.PaymentClient;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import com.mercadopago.resources.payment.Payment;
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
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/reservation")
@Tag(name = "Reservations", description = "Operations related to reservations and payments")
public class ReservationController {

    private final ReservationService reservationService;
    private final PagedResourcesAssembler<ReservationResponseDTO> pagedResourcesAssembler;

    @Autowired
    public ReservationController(ReservationService reservationService, PagedResourcesAssembler<ReservationResponseDTO> pagedResourcesAssembler) {
        this.reservationService = reservationService;
        this.pagedResourcesAssembler = pagedResourcesAssembler;
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
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid input data",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - user not authenticated",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden - insufficient permissions",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Activity not found or unavailable",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
            )
    })
    @PreAuthorize("hasAuthority('CREAR_RESERVA')")
    @PostMapping
    public ResponseEntity<ReservationResponseDTO> createReservation(
            @RequestBody @Valid ReservationCreateDTO dto,
            @AuthenticationPrincipal CredentialEntity credential) throws MPException, MPApiException {

        Long myUserId = credential.getUser().getId();
        ReservationResponseDTO reservation = reservationService.createReservation(dto, myUserId);

        return ResponseEntity.status(HttpStatus.CREATED).body(reservation);
    }

    @Operation(
            summary = "Confirm payment for a reservation",
            description = "Validates the payment status and marks the reservation as paid if the payment was approved."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Reservation marked as paid successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request or payment not approved",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - user not authenticated",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden - insufficient permissions",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Reservation not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
            )
    })
    @PreAuthorize("hasAuthority('PAGAR_RESERVA')")
    @GetMapping("/confirmar-pago")
    public ResponseEntity<String> confirmarPago(@RequestParam Long external_reference,
                                                @RequestParam Long payment_id,
                                                @AuthenticationPrincipal CredentialEntity credential, Pageable pageable) throws MPException, MPApiException {

        Long myUserId = credential.getUser().getId();
        Set<ReservationResponseDTO> reservas = reservationService.findByUserId(myUserId, pageable).toSet();
        List<Long> idReservas = reservas.stream()
                .map(ReservationResponseDTO::getId)
                .toList();

        if (idReservas.contains(external_reference)){
                PaymentClient paymentClient = new PaymentClient();
                Payment payment = paymentClient.get(payment_id);

                if(payment.getStatus().equalsIgnoreCase("approved")){
                    reservationService.paidReservation(external_reference, myUserId, pageable);
                    return ResponseEntity.ok("Reservation marked as paid.");
                }else{
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body("The payment was not approved.");
                }

        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Your user does not have a reservation corresponding to the payment.");
        }
    }

    @Operation(
            summary = "Cancel a reservation",
            description = "Cancels an existing reservation by its ID."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Reservation cancelled successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request data",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - user not authenticated",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden - insufficient permissions",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Reservation not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
            )
    })
    @PreAuthorize("hasAuthority('CANCELAR_RESERVA')")
    @PutMapping("/{id}/cancel")
    public ResponseEntity<Void> cancelReservation(@PathVariable Long id) {
        reservationService.cancelReservation(id);
        return ResponseEntity.ok().build();
    }

    @Operation(
            summary = "Get all reservations",
            description = "Retrieve all reservations (admin access)."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Reservations retrieved successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ReservationResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - user not authenticated",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden - insufficient permissions",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
            )
    })
    @PreAuthorize("hasAuthority('VER_TODAS_RESERVAS')")
    @GetMapping
    public ResponseEntity<PagedModel<EntityModel<ReservationResponseDTO>>> getAllReservations(Pageable pageable) {
        Page<ReservationResponseDTO> reservations = reservationService.findAll(pageable);
        PagedModel<EntityModel<ReservationResponseDTO>> model = pagedResourcesAssembler.toModel(reservations);
        return ResponseEntity.ok(model);
    }

    @Operation(
            summary = "Get my reservations",
            description = "Retrieve reservations for the logged-in user."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Reservations retrieved successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ReservationResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - user not authenticated",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden - insufficient permissions",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
            )
    })
    @PreAuthorize("hasAuthority('VER_RESERVAS_USUARIO')")
    @GetMapping("/my")
    public ResponseEntity<PagedModel<EntityModel<ReservationResponseDTO>>> getMyReservations(@AuthenticationPrincipal CredentialEntity credential, Pageable pageable) {
        Long myUserId = credential.getUser().getId();
        Page<ReservationResponseDTO> reservations = reservationService.findByUserId(myUserId, pageable);
        PagedModel<EntityModel<ReservationResponseDTO>> model = pagedResourcesAssembler.toModel(reservations);
        return ResponseEntity.ok(model);
    }

    @Operation(
            summary = "Get reservations by company",
            description = "Retrieve reservations for a specific company."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Reservations retrieved successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ReservationResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - user not authenticated",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden - insufficient permissions",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Company not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
            )
    })
    @PreAuthorize("hasAuthority('VER_RESERVAS_EMPRESA')")
    @GetMapping("/company/{companyId}")
    public ResponseEntity<PagedModel<EntityModel<ReservationResponseDTO>>> getReservationsByCompany(@PathVariable Long companyId, Pageable pageable) {
        Page<ReservationResponseDTO> reservations = reservationService.findByCompanyId(companyId, pageable);
        PagedModel<EntityModel<ReservationResponseDTO>> model = pagedResourcesAssembler.toModel(reservations);
        return ResponseEntity.ok(model);
    }
}


package com.example.demo.DTOs.Reservation.Response;

import com.example.demo.enums.ReservationStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReservationResponseDTO {

    @Schema(description = "ID de la reserva", example = "100")
    private Long id;

    @Schema(description = "ID del usuario que realizó la reserva", example = "15")
    private Long userId;

    @Schema(description = "ID de la actividad reservada", example = "1")
    private Long activityId;

    @Schema(description = "Indica si la reserva está paga", example = "true")
    private boolean paid;

    @Schema(description = "Monto total de la reserva", example = "1500.50")
    private Double amount;

    @Schema(description = "Fecha y hora en que se realizó la reserva", example = "2025-06-20T15:30:00")
    private LocalDateTime reservationDate;

    @Schema(description = "Estado actual de la reserva", example = "ACTIVE")
    private ReservationStatus status;

    @Schema(description = "URL para realizar el pago")
    private String urlPayment;

}

package com.example.demo.DTOs.Reservation.Request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReservationCreateDTO {

    @Schema(description = "Id de la actividad", example = "1")
    @NotNull(message = "El ID de la actividad no puede ser nulo.")
    private Long activityId;

    @Schema(description = "Precio de la actividad", example = "100.00")
    @DecimalMin(value = "0.0", inclusive = true, message = "El precio no puede ser negativo.")
    @NotNull(message = "El monto no puede ser nulo.")
    private Double amount;
}

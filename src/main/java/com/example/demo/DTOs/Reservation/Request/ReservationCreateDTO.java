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

}

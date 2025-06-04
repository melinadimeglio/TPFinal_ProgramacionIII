package com.example.demo.DTOs.Itinerary.Request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class ItineraryCreateDTO {

    @Schema(description = "Fecha del itinerario (debe ser hoy o en el futuro)", example = "2025-06-20")
    @NotNull(message = "La fecha del itinerario es obligatoria.")
    @FutureOrPresent(message = "La fecha debe ser hoy o en el futuro.")
    private LocalDate itineraryDate;

    @Schema(description = "Notas adicionales para el itinerario", example = "Día libre para recorrer la ciudad")
    private String notes;

    @Schema(description = "ID del viaje al que pertenece el itinerario", example = "10")
    @NotNull(message = "El viaje es obligatorio.")
    private Long tripId;
}

package com.example.demo.DTOs.Itinerary;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class ItineraryUpdateDTO {

    @Schema(description = "Fecha del itinerario (debe ser hoy o posterior)", example = "2025-06-20")
    @FutureOrPresent(message = "La fecha debe ser el día de hoy o posterior.")
    private LocalDate itineraryDate;

    @Schema(description = "Notas adicionales para el itinerario", example = "Día libre para recorrer la ciudad")
    private String notes;

}
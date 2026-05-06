package com.example.demo.DTOs.Itinerary;


import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class ItineraryUpdateDTO {

    @Schema(description = "Fecha del itinerario (debe ser hoy o posterior)", example = "2025-06-20")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate itineraryDate;

    @Schema(description = "Notas adicionales para el itinerario", example = "Día libre para recorrer la ciudad")
    private String notes;

}
package com.example.demo.DTOs.Itinerary.Response;

import com.example.demo.DTOs.Activity.Response.ActivityResumeDTO;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class ItineraryResponseDTO {

    @Schema(description = "ID único del itinerario")
    private Long id;

    @Schema(description = "Fecha del itinerario")
    private LocalDate itineraryDate;

    @Schema(description = "Notas adicionales del itinerario")
    private String notes;

    @Schema(description = "Lista de actividades resumidas asociadas al itinerario")
    private List<ActivityResumeDTO> activities;

    @Schema(description = "ID del usuario propietario")
    private Long userId;

    @Schema(description = "ID del viaje asociado")
    private Long tripId;

}

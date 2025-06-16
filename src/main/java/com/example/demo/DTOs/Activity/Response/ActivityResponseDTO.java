package com.example.demo.DTOs.Activity.Response;

import com.example.demo.enums.ActivityCategory;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class ActivityResponseDTO {

    @Schema(description = "ID único de la actividad", example = "42")
    private Long id;

    @Schema(description = "Precio de la actividad", example = "1800.50")
    private Double price;

    @Schema(description = "Indica si la actividad aún se encuentra disponible", example = "true")
    private boolean available;

    @Schema(description = "Nombre de la actividad", example = "Caminata por el cerro")
    private String name;

    @Schema(description = "Descripción de la actividad", example = "Una caminata guiada por senderos naturales del cerro.")
    private String description;

    @Schema(description = "Categoría de la actividad", example = "AVENTURA")
    private ActivityCategory category;

    @Schema(description = "Fecha de realización de la actividad", example = "2025-08-01")
    private LocalDate date;

    @Schema(description = "Hora de inicio de la actividad", example = "07:30")
    private LocalTime startTime;

    @Schema(description = "Hora de finalización de la actividad", example = "11:30")
    private LocalTime endTime;

    @Schema(description = "ID del itinerario al que pertenece esta actividad", example = "5")
    private Long itineraryId;

    @Schema(description = "IDs de los usuarios que participan en la actividad", example = "[1, 2, 3]")
    private Set<Long> userIds;

    @Schema(description = "ID de la empresa que organiza la actividad", example = "7")
    private Long companyId;

    @Schema(description = "Cantidad de lugares disponibles", example = "10")
    private Integer available_quantity;
}

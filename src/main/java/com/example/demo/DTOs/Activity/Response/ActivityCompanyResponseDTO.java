package com.example.demo.DTOs.Activity.Response;

import com.example.demo.enums.ActivityCategory;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ActivityCompanyResponseDTO {

    @Schema(description = "ID único de la actividad", example = "2")
    private Long id;

    @Schema(description = "Precio de la actividad", example = "2500.0")
    private Double price;

    @Schema(description = "Nombre de la actividad", example = "Excursión al glaciar")
    private String name;

    @Schema(description = "Descripción de la actividad", example = "Una experiencia inolvidable visitando el glaciar Perito Moreno.")
    private String description;

    @Schema(description = "Categoría de la actividad", example = "AVENTURA")
    private ActivityCategory category;

    @Schema(description = "Fecha en la que se realizará la actividad", example = "2025-07-15")
    private LocalDate date;

    @Schema(description = "Hora de inicio", example = "08:30")
    private LocalTime startTime;

    @Schema(description = "Hora de finalización", example = "13:00")
    private LocalTime endTime;

    @Schema(description = "Cantidad de lugares disponibles", example = "10")
    private Long available_quantity;
}
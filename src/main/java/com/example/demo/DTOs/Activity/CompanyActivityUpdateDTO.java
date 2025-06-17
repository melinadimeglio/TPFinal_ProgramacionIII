package com.example.demo.DTOs.Activity;

import com.example.demo.enums.ActivityCategory;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompanyActivityUpdateDTO {

    @Schema(description = "Precio de la actividad (no puede ser negativo)", example = "1500.50")
    @DecimalMin(value = "0.0", inclusive = true, message = "El precio no puede ser negativo.")    private Double price;

    @Schema(description = "Nombre de la actividad", example = "Tour por el centro histórico")
    private String name;

    @Schema(description = "Descripción de la actividad", example = "Recorrido guiado por los principales puntos históricos de la ciudad.")
    private String description;

    @Schema(description = "Categoría de la actividad", example = "AVENTURA")
    private ActivityCategory category;

    @Schema(description = "Fecha en la que se realizará la actividad (hoy o en el futuro)", example = "2025-06-10")
    @FutureOrPresent(message = "La fecha debe ser hoy o en el futuro.")
    private LocalDate date;

    @Schema(description = "Hora de inicio de la actividad", example = "09:00")
    private LocalTime startTime;

    @Schema(description = "Hora de finalización de la actividad", example = "12:00")
    private LocalTime endTime;

    @Schema(description = "Cantidad de lugares disponibles en la actividad", example = "12")
    private Long available_quantity;

}


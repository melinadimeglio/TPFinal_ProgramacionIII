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

public class ActivityUpdateDTO {

    @Schema(description = "Precio de la actividad (no puede ser negativo)", example = "1800.0")
    @DecimalMin(value = "0.0", inclusive = true, message = "El precio no puede ser negativo.")
    private Double price;

    @Schema(description = "Nombre de la actividad", example = "Tour por el centro histórico")
    @NotBlank(message = "El nombre es obligatorio")
    private String name;

    @Schema(description = "Descripción detallada de la actividad", example = "Un recorrido guiado por los principales puntos turísticos")
    @NotBlank(message = "La descripción es obligatoria.")
    private String description;

    @Schema(description = "Categoría de la actividad", example = "SHOPPING")
    @NotNull(message = "La categoría es obligatoria.")
    private ActivityCategory category;

    @Schema(description = "Fecha de la actividad", example = "2025-07-10")
    @NotNull(message = "La fecha es obligatoria.")
    @FutureOrPresent(message = "La fecha debe ser hoy o en el futuro.")
    private LocalDate date;

    @Schema(description = "Hora de inicio", example = "10:00:00")
    private LocalTime startTime;

    @Schema(description = "Hora de fin", example = "12:00:00")
    private LocalTime endTime;

    @Schema(description = "Indica si la actividad está disponible o no", example = "true")
    private Boolean available;

    @Schema(description = "ID del itinerario al que se quiere asociar la actividad", example = "3")
    private Long itineraryId;
}

package com.example.demo.DTOs.Activity.Request;

import com.example.demo.enums.ActivityCategory;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserActivityCreateDTO {

    @Schema(description = "Precio de la actividad (no puede ser negativo)", example = "150.0")
    @DecimalMin(value = "0.0", inclusive = true, message = "El precio no puede ser negativo.")
    private Double price;

    @Schema(description = "Nombre de la actividad", example = "Tour por el centro histórico")
    @NotBlank(message = "El nombre es obligatorio.")
    private String name;

    @Schema(description = "Descripción detallada de la actividad", example = "Un recorrido guiado por los principales puntos turísticos.")
    @NotBlank(message = "La descripción es obligatoria.")
    private String description;

    @Schema(description = "Categoría de la actividad", example = "SHOPPING")
    @NotNull(message = "La categoría es obligatoria.")
    private ActivityCategory category;

    @Schema(description = "Fecha de la actividad (hoy o en el futuro)", example = "2025-07-10")
    @NotNull(message = "La fecha es obligatoria.")
    @FutureOrPresent(message = "La fecha debe ser hoy o en el futuro.")
    private LocalDate date;

    @Schema(description = "Hora de inicio de la actividad", example = "10:00:00")
    @NotNull(message = "La hora de inicio es obligatoria.")
    private LocalTime startTime;

    @Schema(description = "Hora de fin de la actividad", example = "12:00:00")
    @NotNull(message = "La hora de fin es obligatoria.")
    private LocalTime endTime;

    @Schema(description = "Lista de IDs de usuarios que también participan en la actividad", example = "[3, 5]")
    private Set<Long> sharedUserIds;
}

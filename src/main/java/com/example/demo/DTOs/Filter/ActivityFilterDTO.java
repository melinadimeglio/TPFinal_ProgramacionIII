package com.example.demo.DTOs.Filter;

import com.example.demo.enums.ActivityCategory;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.springdoc.core.annotations.ParameterObject;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ParameterObject
public class ActivityFilterDTO {

    @Schema(description = "Categoría de la actividad para filtrar", example = "AVENTURA")
    private ActivityCategory category;

    @Schema(description = "Fecha mínima para filtrar actividades (inclusive)", example = "2025-06-01")
    private LocalDate startDate;

    @Schema(description = "Fecha máxima para filtrar actividades (inclusive)", example = "2025-06-30")
    private LocalDate endDate;
}

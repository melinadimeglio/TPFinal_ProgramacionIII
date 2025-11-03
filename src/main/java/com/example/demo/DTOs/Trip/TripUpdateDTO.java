package com.example.demo.DTOs.Trip;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;

import java.time.LocalDate;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class TripUpdateDTO {

    @Schema(description = "Nombre actualizado del viaje", example = "Vacaciones en Europa")
    private String name;

    @Schema(description = "Destino actualizado del viaje", example = "París")
    private String destination;

    @Schema(description = "Fecha de inicio del viaje (debe ser hoy o una posterior)", example = "2025-07-01")
    private LocalDate startDate;

    @Schema(description = "Fecha de fin del viaje (debe ser hoy o una posterior)", example = "2025-07-10")
    private LocalDate endDate;

    @Schema(description = "Presupuesto estimado actualizado para el viaje", example = "5000.00")
    @PositiveOrZero(message = "El presupuesto debe ser cero o positivo.")
    private Double estimatedBudget;

    @Schema(description = "Cantidad de acompañantes actualizada", example = "2")
    @Min(value = 0, message = "La cantidad de acompañantes no puede ser negativa.")
    private Integer companions;

    @Schema(description = "Lista de IDs de los usuarios acompañantes (opcional)", example = "[2, 5]", nullable = true)
    private Set<Long> sharedUserIds;
}

package com.example.demo.DTOs.Trip.Request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class TripCreateDTO {

    @Schema(description = "Nombre del viaje", example = "Vacaciones en Europa")
    @NotBlank(message = "El nombre del viaje es obligatorio.")
    private String name;

    @Schema(description = "Destino del viaje", example = "París")
    @NotBlank(message = "El destino es obligatorio.")
    private String destination;

    @Schema(description = "Fecha de inicio del viaje", example = "2025-07-01")
    @NotNull(message = "La fecha de inicio es obligatoria.")
    private LocalDate startDate;

    @Schema(description = "Fecha de fin del viaje", example = "2025-07-10")
    @NotNull(message = "La fecha de fin es obligatoria.")
    private LocalDate endDate;

    @Schema(description = "Presupuesto estimado para el viaje", example = "5000.00")
    @NotNull(message = "El presupuesto estimado es obligatorio.")
    @PositiveOrZero(message = "El presupuesto debe ser cero o positivo.")
    private Double estimatedBudget;

    @Schema(description = "Cantidad de acompañantes", example = "2")
    @Min(value = 0, message = "La cantidad de acompañantes no puede ser negativa.")
    private Integer companions;

    @Schema(description = "Lista de IDs de los usuarios acompañantes (opcional)", example = "[2, 5]", nullable = true)
    private Set<Long> sharedUserIds;
}

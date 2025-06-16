package com.example.demo.DTOs.Trip.Request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
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
    @FutureOrPresent(message = "La fecha de comienzo del viaje debe ser la fecha de hoy, o una posterior.")
    private LocalDate startDate;

    @Schema(description = "Fecha de fin del viaje (opcional)", example = "2025-07-10")
    @FutureOrPresent(message = "La fecha de comienzo del viaje debe ser la fecha de hoy, o una posterior.")
    private LocalDate endDate;

    @Schema(description = "Presupuesto estimado para el viaje", example = "5000.00")
    @NotNull(message = "El presupuesto estimado es obligatorio.")
    @PositiveOrZero(message = "El presupuesto debe ser cero o positivo.")
    private Double estimatedBudget;

    @Schema(description = "Cantidad de acompañantes", example = "2")
    private int companions;

    @Schema(description = "Lista de IDs de los usuarios acompañantes", example = "[2, 5]")
    private Set<Long> sharedUserIds;
}

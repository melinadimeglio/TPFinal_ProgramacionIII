package com.example.demo.DTOs.Trip.Response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class TripResponseDTO {

    @Schema(description = "ID único del viaje", example = "15")
    private Long id;

    @Schema(description = "Nombre del viaje", example = "Vacaciones en Europa")
    private String name;

    @Schema(description = "Destino del viaje", example = "París")
    private String destination;

    @Schema(description = "Fecha de inicio del viaje", example = "2025-07-01")
    private LocalDate startDate;

    @Schema(description = "Fecha de fin del viaje (puede ser null)", example = "2025-07-10", nullable = true)
    private LocalDate endDate;

    @Schema(description = "Presupuesto estimado para el viaje", example = "5000.00")
    private Double estimatedBudget;

    @Schema(description = "Cantidad de acompañantes", example = "2")
    private int companions;

    @Schema(description = "Indica si el viaje está activo", example = "true")
    private boolean active;

    @Schema(description = "Lista de IDs de los usuarios que participan en el viaje", example = "[1, 2, 5]")
    private List<Long> userIds;

}

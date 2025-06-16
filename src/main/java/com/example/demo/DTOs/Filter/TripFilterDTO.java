package com.example.demo.DTOs.Filter;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TripFilterDTO {

    @Schema(description = "Destino del viaje para filtrar", example = "Buenos Aires")
    private String destination;

    @Schema(description = "Fecha de inicio del viaje (formato yyyy-MM-dd)", example = "2025-06-01")
    private LocalDate startDate;

    @Schema(description = "Fecha de fin del viaje (formato yyyy-MM-dd)", example = "2025-06-30")
    private LocalDate endDate;
}

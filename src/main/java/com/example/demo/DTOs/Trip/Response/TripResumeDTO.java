package com.example.demo.DTOs.Trip.Response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class TripResumeDTO {

    @Schema(description = "ID único del viaje", example = "15")
    private Long id;

    @Schema(description = "Nombre del viaje", example = "Vacaciones en Europa")
    private String name;

    @Schema(description = "Destino del viaje", example = "París")
    private String destination;

    @Schema(description = "Estado del viaje (activo o no)", example = "true")
    private boolean active;

}

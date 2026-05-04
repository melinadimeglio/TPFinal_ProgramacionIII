package com.example.demo.DTOs.Activity.Response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class ActivityResumeDTO {

    @Schema(description = "Nombre de la actividad", example = "Caminata por el cerro")
    private String name;
    @Schema(description = "Descripción de la actividad", example = "Una caminata guiada por senderos naturales del cerro.")
    private String description;
    @Schema(
            description = "URL de la imagen de la actividad almacenada en Cloudinary",
            example = "https://res.cloudinary.com/tu-cloud/image/upload/v1234567890/travelplanner/actividad.jpg",
            nullable = true
    )
    private String imageUrl;

}

package com.example.demo.DTOs.Review.Request;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewRequestDTO {
    @NotNull(message = "El ID de actividad es obligatorio")
    private Long activityId;

    @NotNull(message = "La puntuación general es obligatoria")
    @Min(value = 1, message = "La puntuación mínima es 1")
    @Max(value = 5, message = "La puntuación máxima es 5")
    private Integer rating;

    @NotBlank(message = "El título es obligatorio")
    @Size(max = 100, message = "El título no puede superar los 100 caracteres")
    private String title;

    @NotBlank(message = "El comentario es obligatorio")
    @Size(min = 20, message = "El comentario debe tener al menos 20 caracteres")
    @Size(max = 500, message = "El comentario no puede superar los 500 caracteres")
    private String comentary;

    @NotNull @Min(1) @Max(5)
    private Integer ratingGuide;

    @NotNull @Min(1) @Max(5)
    private Integer ratingPuntuality;

    @NotNull @Min(1) @Max(5)
    private Integer ratingPrice;

    @NotNull @Min(1) @Max(5)
    private Integer ratingSecurity;
}

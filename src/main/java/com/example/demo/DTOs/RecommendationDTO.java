package com.example.demo.DTOs;

import com.example.demo.entities.CategoryEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder

public class RecommendationDTO {

    @Schema(description = "Nombre de la recomendación o lugar sugerido", example = "Cristo Redentor")
    private String name;

    @Schema(description = "Categorías asociadas a la recomendación")
    private Set<CategoryDTO> categories;

}

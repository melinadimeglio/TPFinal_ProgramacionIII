package com.example.demo.DTOs;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class CategoryDTO {

    @Schema(description = "Nombre de la categoria", example = "CULTURAL")
    public String name;

}

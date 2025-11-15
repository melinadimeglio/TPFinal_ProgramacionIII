package com.example.demo.DTOs.Filter;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.springdoc.core.annotations.ParameterObject;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ParameterObject
public class CheckListFilterDTO {

    @Schema(description = "Indica si se quieren filtrar solo checklists completadas o no completadas", example = "true")
    private Boolean completed;

    @Schema(description = "Indica si se quieren filtrar solo checklists activas o inactivas", example = "true")
    private Boolean active;
}

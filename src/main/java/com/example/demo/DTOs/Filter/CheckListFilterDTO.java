package com.example.demo.DTOs.Filter;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CheckListFilterDTO {

    @Schema(description = "Indica si se quieren filtrar solo checklists completadas o no completadas", example = "true")
    private Boolean completed;
}

package com.example.demo.DTOs.Filter;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CheckListItemFilterDTO {

    @Schema(description = "ID de la checklist a la que pertenece el ítem", example = "8")
    private Long checklistId;

    @Schema(description = "Estado del ítem: true para completado, false para pendiente", example = "true")
    private Boolean status;
}

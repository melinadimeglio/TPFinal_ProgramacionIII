package com.example.demo.DTOs.CheckList.Response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CheckListItemResponseDTO {

    @Schema(description = "ID único del ítem de la checklist", example = "10")
    private Long id;

    @Schema(description = "Descripción del ítem", example = "Cargar el pasaporte")
    private String description;

    @Schema(description = "Estado del ítem (completado o no)", example = "true")
    private boolean status;

    @Schema(description = "ID del usuario que creó o está asociado al ítem", example = "3")
    private Long userId;

    @Schema(description = "ID de la checklist a la que pertenece el ítem", example = "8")
    private Long checklistId;

}
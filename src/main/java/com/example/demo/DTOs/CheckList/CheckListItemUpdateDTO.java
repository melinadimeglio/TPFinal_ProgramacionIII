package com.example.demo.DTOs.CheckList;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import jakarta.validation.constraints.NotBlank;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CheckListItemUpdateDTO {

    @Schema(description = "Descripción del ítem de la checklist", example = "Cargar el pasaporte")
    @NotBlank(message = "La descripción es obligatoria.")
    private String description;

    @Schema(description = "Estado del ítem (completado o no)", example = "true")
    private boolean status;

    @Schema(description = "ID de la checklist a la que pertenece el ítem", example = "8")
    @NotNull(message = "El ID de la checklist es obligatorio.")
    private Long checklistId;
}
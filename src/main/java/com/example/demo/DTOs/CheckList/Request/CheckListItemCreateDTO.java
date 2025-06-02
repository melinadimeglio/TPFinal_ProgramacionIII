package com.example.demo.DTOs.CheckList.Request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class CheckListItemCreateDTO {

    @Schema(description = "Descripción del ítem de la checklist", example = "Cargar el pasaporte")
    @NotBlank(message = "La descripción es obligatoria.")
    private String description;

    @Schema(description = "ID de la checklist a la que pertenece el ítem", example = "8")
    @NotNull(message = "El ID de la checklist es obligatorio.")
    private Long checklistId;

}

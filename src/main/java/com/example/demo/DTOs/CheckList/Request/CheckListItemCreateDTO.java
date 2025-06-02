package com.example.demo.DTOs.CheckList.Request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class CheckListItemCreateDTO {

    @NotBlank(message = "La descripción es obligatoria.")
    private String description;

    @NotNull(message = "El ID de la checklist es obligatorio.")
    private Long checklistId;

}

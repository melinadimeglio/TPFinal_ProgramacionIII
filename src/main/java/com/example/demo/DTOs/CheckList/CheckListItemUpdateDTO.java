package com.example.demo.DTOs.CheckList;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import jakarta.validation.constraints.NotBlank;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CheckListItemUpdateDTO {

    private String description;

    private boolean status;

    @NotNull(message = "El ID de la checklist es obligatorio.")
    private Long checklistId;
}
package com.example.demo.DTOs.CheckList;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class CheckListItemDTO {

    private Long id;

    @NotBlank(message = "La descripción del ítem es obligatoria.")
    private String description;
    private boolean status;

    @NotNull(message = "El ID del usuario es obligatorio.")
    private Long userId;
    private String userName;

}

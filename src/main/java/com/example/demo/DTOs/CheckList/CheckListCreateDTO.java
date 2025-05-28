package com.example.demo.DTOs.CheckList;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class CheckListCreateDTO {
    @NotBlank(message = "El nombre de la checklist es obligatorio.")
    private String name;

    @NotNull(message = "El ID del viaje es obligatorio.")
    private Long tripId;

    @NotNull(message = "El ID del usuario es obligatorio.")
    private Long userId;
}

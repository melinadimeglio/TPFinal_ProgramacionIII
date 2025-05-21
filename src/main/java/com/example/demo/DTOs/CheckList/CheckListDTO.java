package com.example.demo.DTOs.CheckList;

import com.example.demo.entities.CheckListItemEntity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class CheckListDTO {

    private Long id;

    @NotBlank(message = "El nombre del ítem de la checklist es obligatorio.")
    private String item;

    @NotNull(message = "El ID del viaje es obligatorio.")
    private Long tripId;
    private List<CheckListItemEntity> items;

}

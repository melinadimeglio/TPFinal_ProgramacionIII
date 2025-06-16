package com.example.demo.DTOs.CheckList.Response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class CheckListResponseDTO {

    @Schema(description = "ID único de la checklist", example = "5")
    private Long id;

    @Schema(description = "Nombre de la checklist", example = "Checklist de Viaje a Brasil")
    private String name;

    @Schema(description = "Indica si la checklist está completada", example = "false")
    private boolean completed;

    @Schema(description = "ID del viaje asociado a la checklist", example = "7")
    private Long tripId;

    @Schema(description = "ID del usuario propietario de la checklist", example = "3")
    private Long userId;

    @Schema(description = "Lista de ítems que componen la checklist")
    private List<CheckListItemResponseDTO> items;

}

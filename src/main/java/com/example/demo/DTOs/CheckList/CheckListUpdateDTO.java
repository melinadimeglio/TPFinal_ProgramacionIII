package com.example.demo.DTOs.CheckList;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class CheckListUpdateDTO {

    @Schema(description = "Nombre de la checklist", example = "Checklist de Viaje a Brasil")
    @NotBlank(message = "El nombre de la checklist es obligatorio.")
    private String name;

    @Schema(description = "Indica si la checklist está completada", example = "false")
    private Boolean completed;

    @Schema(description = "ID del viaje asociado a la checklist", example = "5")
    @NotNull(message = "El ID del viaje es obligatorio.")
    private Long tripId;

}

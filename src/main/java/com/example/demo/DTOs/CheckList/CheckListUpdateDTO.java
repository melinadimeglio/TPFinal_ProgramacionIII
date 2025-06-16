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

        private Boolean completed;

        @NotNull(message = "El ID del viaje es obligatorio.")
        private Long tripId;

}

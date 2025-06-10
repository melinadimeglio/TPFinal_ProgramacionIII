package com.example.demo.DTOs.CheckList;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class CheckListUpdateDTO {

        @NotBlank(message = "El nombre de la checklist es obligatorio.")
        private String name;

        private boolean completed;

        @NotNull(message = "El ID del viaje es obligatorio.")
        private Long tripId;

}

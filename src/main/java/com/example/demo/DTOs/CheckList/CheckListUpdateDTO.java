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

        private String name;

        private Boolean completed;

        @NotNull(message = "El ID del viaje es obligatorio.")
        private Long tripId;

}

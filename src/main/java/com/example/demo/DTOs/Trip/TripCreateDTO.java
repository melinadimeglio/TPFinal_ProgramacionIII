package com.example.demo.DTOs.Trip;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class TripCreateDTO {

    @NotBlank(message = "El destino es obligatorio.")
    private String destination;

    @NotNull(message = "La fecha de inicio es obligatoria.")
    private LocalDate startDate;

    @NotNull(message = "La fecha de fin es obligatoria.")
    private LocalDate endDate;

    @NotNull(message = "El presupuesto estimado es obligatorio.")
    @PositiveOrZero(message = "El presupuesto debe ser cero o positivo.")
    private Double estimatedBudget;

    @Min(value = 1, message = "Debe haber al menos un pasajero.")
    private int passengers;
    private boolean active;

    @NotNull(message = "Debe incluir al menos un usuario asociado.")
    private List<Long> userIds;

}

package com.example.demo.DTOs.Trip;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class TripUpdateDTO {

    private String destination;
    private LocalDate endDate;

    @PositiveOrZero(message = "El presupuesto debe ser cero o positivo.")
    private Double estimatedBudget;

    @Min(value = 1, message = "Debe haber al menos un pasajero.")
    private Integer companions;

    private Boolean active;

}

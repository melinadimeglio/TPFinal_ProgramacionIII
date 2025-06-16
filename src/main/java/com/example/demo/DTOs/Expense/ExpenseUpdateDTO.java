package com.example.demo.DTOs.Expense;

import com.example.demo.enums.ExpenseCategory;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.time.LocalDate;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class ExpenseUpdateDTO {

    @Schema(description = "Categoría del gasto", example = "ALOJAMIENTO")
    @NotNull(message = "La categoría es obligatoria.")
    private ExpenseCategory category;

    @Schema(description = "Descripción del gasto", example = "Hotel 5 estrellas con desayuno incluido")
    private String description;

    @Schema(description = "Monto total del gasto", example = "1500.00")
    @Positive(message = "El monto debe ser positivo.")
    @NotNull(message = "El monto es obligatorio.")
    private Double amount;

    @Schema(description = "Fecha en la que se realizó el gasto", example = "2025-06-15")
    @PastOrPresent(message = "La fecha debe ser el día de hoy o posterior.")
    @NotNull(message = "La fecha es obligatoria.")
    private LocalDate date;

    @Schema(description = "IDs de usuarios que participaron del gasto", example = "[3, 5]")
    private Set<Long> userIds;

    @Schema(description = "ID del viaje al que pertenece el gasto", example = "7")
    @NotNull(message = "El ID del viaje es obligatorio.")
    private Long tripId;
}


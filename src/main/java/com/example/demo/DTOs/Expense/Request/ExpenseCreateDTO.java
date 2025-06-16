package com.example.demo.DTOs.Expense.Request;

import com.example.demo.enums.ExpenseCategory;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class ExpenseCreateDTO {

    @Schema(description = "Categoría del gasto", example = "ALOJAMIENTO")
    @NotNull(message = "La categoría del gasto es obligatoria.")
    private ExpenseCategory category;

    @Schema(description = "Descripción opcional del gasto", example = "Hotel 5 estrellas con desayuno incluido")
    private String description;

    @Schema(description = "Monto total del gasto", example = "1500.00")
    @NotNull(message = "El monto es obligatorio.")
    @Positive(message = "El monto debe ser un valor positivo.")
    private Double amount;

    @Schema(description = "Fecha en la que se realizó el gasto", example = "2025-06-15")
    @NotNull(message = "La fecha es obligatoria.")
    @FutureOrPresent(message = "La fecha debe ser el día de hoy o posterior.")
    private LocalDate date;

    @Schema(description = "Lista de IDs de usuarios que tambien participan del gasto", example = "[3, 5]")
    private Set<Long> sharedUserIds;

    @Schema(description = "ID del viaje al que pertenece el gasto", example = "7")
    @NotNull(message = "El ID del viaje es obligatorio.")
    private Long tripId;
}

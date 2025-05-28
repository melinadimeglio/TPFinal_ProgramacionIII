package com.example.demo.DTOs.Expense;

import com.example.demo.enums.ExpenseCategory;
import jakarta.validation.constraints.NotBlank;
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

    @NotNull(message = "La categoría del gasto es obligatoria.")
    private ExpenseCategory category;

    private String description;

    @NotNull(message = "El monto es obligatorio.")
    @Positive(message = "El monto debe ser un valor positivo.")
    private Double amount;

    @NotNull(message = "La fecha es obligatoria.")
    private LocalDate date;

    @NotNull(message = "Debe ingresar al menos un usuario.")
    @Size(min = 1, message = "Debe haber al menos un usuario para dividir el gasto.")
    private Set<Long> userIds;

    @NotNull(message = "El ID del viaje es obligatorio.")
    private Long tripId;
}

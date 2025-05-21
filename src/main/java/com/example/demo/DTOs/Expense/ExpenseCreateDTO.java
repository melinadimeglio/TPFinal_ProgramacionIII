package com.example.demo.DTOs.Expense;

import com.example.demo.enums.ExpenseCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.time.LocalDate;

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

    @NotBlank(message = "El ID del usuario es obligatorio.")
    private String userId;

}

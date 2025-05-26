package com.example.demo.DTOs.Expense;

import com.example.demo.enums.ExpenseCategory;
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

    private ExpenseCategory category;
    private String description;

    @Positive(message = "El monto debe ser positivo.")
    private Double amount;
    private LocalDate date;
    private Set<Long> userIds;
    private Long tripId;
}


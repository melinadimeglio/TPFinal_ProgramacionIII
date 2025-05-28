package com.example.demo.DTOs.Expense;
import com.example.demo.enums.ExpenseCategory;
import lombok.*;

import java.time.LocalDate;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class ExpenseResponseDTO {

    private Long id;
    private ExpenseCategory category;
    private String description;
    private Double amount;
    private LocalDate date;
    private Set<Long> userIds;
    private Double dividedAmount;
    private Long tripId;
    private String budgetWarning;

}

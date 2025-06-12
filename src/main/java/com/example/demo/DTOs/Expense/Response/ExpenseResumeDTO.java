package com.example.demo.DTOs.Expense.Response;

import lombok.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class ExpenseResumeDTO {
    private Long id;
    private String category;
    private String description;
    private Double amount;
    private LocalDate date;
    private Set<Long> userIds;
    private Long tripId;
}

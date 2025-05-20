package com.example.demo.DTOs.Expense;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class ExpenseCreateDTO {

    private String category;
    private String description;
    private Double amount;
    private LocalDate date;
    private String username;

}

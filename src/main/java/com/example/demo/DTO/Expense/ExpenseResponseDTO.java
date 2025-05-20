package com.example.demo.DTO.Expense;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class ExpenseResponseDTO {

    private String category;
    private String description;
    private Double amount;
    private LocalDate date;
    private String username;

}

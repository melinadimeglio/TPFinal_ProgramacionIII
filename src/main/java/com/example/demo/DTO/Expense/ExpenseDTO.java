package com.example.demo.DTO.Expense;
import com.example.demo.enums.ExpenseCategory;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class ExpenseDTO {

    private Long id;
    private String category;
    private String description;
    private Double amount;
    private LocalDate date;
    private Long userId;

}

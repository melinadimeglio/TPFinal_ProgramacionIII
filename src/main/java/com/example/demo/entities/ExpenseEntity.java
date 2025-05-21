package com.example.demo.entities;

import com.example.demo.enums.ExpenseCategory;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.time.LocalDate;
import java.util.Date;

@Entity
@Table(name = "Expense")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class ExpenseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "La categoría del gasto es obligatoria.")
    private ExpenseCategory category;

    @NotNull(message = "El monto es obligatorio.")
    @Positive(message = "El monto debe ser un valor positivo.")
    private Double amount;
    private String description;

    @NotNull(message = "La fecha es obligatoria.")
    private LocalDate date;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

}

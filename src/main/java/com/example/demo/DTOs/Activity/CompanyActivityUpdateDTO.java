package com.example.demo.DTOs.Activity;

import com.example.demo.enums.ActivityCategory;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.FutureOrPresent;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompanyActivityUpdateDTO {
    @DecimalMin(value = "0.0", inclusive = true, message = "El precio no puede ser negativo.")
    private Double price;

    private String name;

    private String description;

    private ActivityCategory category;

    @FutureOrPresent(message = "La fecha debe ser hoy o en el futuro.")
    private LocalDate date;

    private LocalTime startTime;

    private LocalTime endTime;
}


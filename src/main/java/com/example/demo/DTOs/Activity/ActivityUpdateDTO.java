package com.example.demo.DTOs.Activity;

import com.example.demo.enums.ActivityCategory;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class ActivityUpdateDTO {

    @DecimalMin(value = "0.0", inclusive = true, message = "El precio no puede ser negativo.")
    private Double price;
    private Boolean availability;

    @NotBlank(message = "La descripción no puede estar vacía si se proporciona.")
    private String description;
    private ActivityCategory category;

    @FutureOrPresent(message = "La fecha debe ser hoy o en el futuro.")
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;

}

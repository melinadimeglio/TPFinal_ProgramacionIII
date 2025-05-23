package com.example.demo.DTOs.Activity;

import com.example.demo.enums.ActivityCategory;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class UserActivityCreateDTO {

    private Double price;

    @NotBlank(message = "El nombre es obligatorio")
    private String name;

    @NotBlank(message = "La descripción es obligatoria.")
    private String description;

    @NotNull(message = "La categoría es obligatoria.")
    private ActivityCategory category;

    @NotNull(message = "La fecha es obligatoria.")
    @FutureOrPresent(message = "La fecha debe ser hoy o en el futuro.")
    private LocalDate date;

    private LocalTime startTime;

    private LocalTime endTime;

    private Long itineraryId;

    @NotNull(message = "El ID del usuario es obligatorio.")
    private Long userId;
}

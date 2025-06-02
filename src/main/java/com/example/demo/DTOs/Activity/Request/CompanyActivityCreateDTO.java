package com.example.demo.DTOs.Activity.Request;

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
public class CompanyActivityCreateDTO {
    @NotNull(message = "El precio es obligatorio.")
    @DecimalMin(value = "0.0", inclusive = true, message = "El precio no puede ser negativo.")
    private Double price;

    @NotBlank(message = "El nombre es obligatorio.")
    private String name;

    @NotBlank(message = "La descripción es obligatoria.")
    private String description;

    @NotNull(message = "La categoría es obligatoria.")
    private ActivityCategory category;

    @NotNull(message = "La fecha es obligatoria.")
    @FutureOrPresent(message = "La fecha debe ser hoy o en el futuro.")
    private LocalDate date;

    @NotNull(message = "La hora de inicio es obligatoria.")
    private LocalTime startTime;

    @NotNull(message = "La hora de fin es obligatoria.")
    private LocalTime endTime;

    @NotNull(message = "El ID de la empresa es obligatorio.")
    private Long companyId;
}


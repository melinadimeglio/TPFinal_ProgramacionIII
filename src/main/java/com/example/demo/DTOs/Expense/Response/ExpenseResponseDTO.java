package com.example.demo.DTOs.Expense.Response;
import com.example.demo.enums.ExpenseCategory;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDate;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class ExpenseResponseDTO {

    @Schema(description = "ID único del gasto", example = "15")
    private Long id;

    @Schema(description = "Categoría del gasto", example = "ALOJAMIENTO")
    private ExpenseCategory category;

    @Schema(description = "Descripción del gasto", example = "Hotel 5 estrellas con desayuno incluido")
    private String description;

    @Schema(description = "Monto total del gasto", example = "1500.00")
    private Double amount;

    @Schema(description = "Fecha en la que se realizó el gasto", example = "2025-06-15")
    private LocalDate date;

    @Schema(description = "IDs de usuarios que participaron en el gasto", example = "[3, 5]")
    private Set<Long> userIds;

    @Schema(description = "Monto dividido entre los usuarios participantes", example = "750.00")
    private Double dividedAmount;

    @Schema(description = "ID del viaje al que pertenece el gasto", example = "7")
    private Long tripId;

    @Schema(description = "Advertencia presupuestaria relacionada con este gasto", example = "Excede el presupuesto asignado")
    private String budgetWarning;

}

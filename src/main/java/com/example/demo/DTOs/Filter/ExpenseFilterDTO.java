package com.example.demo.DTOs.Filter;

import com.example.demo.enums.ExpenseCategory;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ParameterObject
public class ExpenseFilterDTO {

    @Schema(description = "Categoría del gasto para filtrar", example = "ALOJAMIENTO")
    private ExpenseCategory category;

    @Schema(description = "Monto mínimo del gasto para filtrar", example = "100.0")
    private Double minAmount;

    @Schema(description = "Monto máximo del gasto para filtrar", example = "2000.0")
    private Double maxAmount;

    @Schema(description = "Fecha inicial para filtrar gastos (inclusive)", example = "2025-06-01", format = "date")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate startDate;

    @Schema(description = "Fecha final para filtrar gastos (inclusive)", example = "2025-06-30", format = "date")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate endDate;

    @Schema(description = "ID del viaje para filtrar", example = "5")
    private Long tripId;
}

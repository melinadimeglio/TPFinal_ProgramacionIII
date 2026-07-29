package com.example.demo.DTOs.Dashboard;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DashboardDTO {

    @Schema(description = "Cantidad de viajes realizados por el usuario ", example = "12")
    private int totalViajes;

    @Schema(description = "Lista de destinos visitados por el usuario", example = "Mexico, Brasil")
    private List<String> destinosVisitados;

    @Schema(description = "Cantidad de actividades realizadas por el usuario ", example = "15")
    private int totalActividades;

    @Schema(description = "Gastos realizados por categoria", example = "Actividades, 5")
    private Map<String, Double> gastosPorCategoria;

    @Schema(description = "Gastos realizados por mes", example = "ACCEPTED")
    private Map<String, Double> gastosPorMes;

}
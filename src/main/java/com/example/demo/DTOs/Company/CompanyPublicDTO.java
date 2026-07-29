package com.example.demo.DTOs.Company;

import com.example.demo.DTOs.Activity.Response.ActivityCompanyResponseDTO;
import com.example.demo.enums.ActivityCategory;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompanyPublicDTO {

    @Schema(description = "Nombre de la actividad", example = "Excursión al glaciar")
    private String companyName;

    @Schema(description = "Descripción de la actividad", example = "Una experiencia inolvidable visitando el glaciar Perito Moreno.")
    private String description;

    @Schema(description = "Numero telefonico de la empresa", example = "22345588541")
    private String phone;

    @Schema(description = "ID de la empresa", example = "123456")
    private String taxId;

    @Schema(description = "Lugar fisico donde se encuentra la empresa", example = "Madrid")
    private String location;

    @Schema(description = "Lista de actividades brindadas por la empresa", example = "13:00")
    private List<ActivityCompanyResponseDTO> activities;

}

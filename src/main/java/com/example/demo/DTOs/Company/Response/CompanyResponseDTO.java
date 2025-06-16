package com.example.demo.DTOs.Company.Response;

import com.example.demo.DTOs.Activity.Response.ActivityResumeDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class CompanyResponseDTO {

    @Schema(description = "ID único de la empresa", example = "10")
    private Long id;

    @Schema(description = "Nombre de usuario de la empresa", example = "turismoGlobal")
    private String username;

    @Schema(description = "Número de identificación tributaria (Tax ID)", example = "30567890123")
    private String taxId;

    @Schema(description = "Ubicación principal de la empresa", example = "Buenos Aires, Argentina")
    private String location;

    @Schema(description = "Descripción general de la empresa", example = "Empresa dedicada al turismo receptivo internacional.")
    private String description;

    @Schema(description = "Teléfono de contacto de la empresa", example = "+5491112345678")
    private String phone;

    @Schema(description = "Estado activo o inactivo de la empresa", example = "true")
    private boolean active;

    @Schema(description = "Lista resumida de actividades asociadas a la empresa")
    private List<ActivityResumeDTO> activities;

}

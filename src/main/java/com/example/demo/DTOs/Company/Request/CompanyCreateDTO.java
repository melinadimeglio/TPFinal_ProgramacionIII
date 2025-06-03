package com.example.demo.DTOs.Company.Request;

import com.example.demo.DTOs.Activity.Response.ActivityResumeDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class CompanyCreateDTO {

    @Schema(description = "Nombre de usuario de la empresa", example = "turismoGlobal")
    @NotBlank(message = "El nombre de usuario no debe estar vacio.")
    private String username;

    @Schema(description = "Email de contacto de la empresa", example = "info@turismoglobal.com")
    @Email(message = "El email debe tener un formato valido.")
    @NotBlank(message = "El email no debe estar vacio.")
    private String email;

    @Schema(description = "Contraseña para la cuenta de empresa", example = "contrasenaSegura123")
    @NotBlank(message = "La contraseña no debe estar vacia")
    private String password;

    @Schema(description = "Número de identificación tributaria (Tax ID)", example = "30567890123")
    @NotBlank(message = "El TAX ID no debe estar vacio.")
    private String taxId;

    @Schema(description = "Ubicación principal de la empresa", example = "Buenos Aires, Argentina")
    @NotBlank(message = "La ubicacion no debe estar vacia.")
    private String location;

    @Schema(description = "Teléfono de contacto de la empresa", example = "+54 911 1234 5678")
    @NotBlank(message = "El telefono no debe estar vacio.")
    private String phone;

    @Schema(description = "Descripción general de la empresa", example = "Empresa dedicada al turismo receptivo internacional.")
    @NotBlank(message = "La descripcion no puede estar vacia")
    private String description;
}

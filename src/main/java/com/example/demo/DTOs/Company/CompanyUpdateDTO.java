package com.example.demo.DTOs.Company;

import com.example.demo.DTOs.Activity.Response.ActivityResumeDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class CompanyUpdateDTO {

    @Schema(description = "Nombre de usuario de la empresa", example = "turismoGlobal")
    @Size(min = 5, max = 20, message = "El username debe tener entre 5 y 20 caracteres.")
    private String username;

    @Schema(description = "Email de contacto de la empresa", example = "info@turismoglobal.com")
    @Email(message = "El email debe tener un formato valido.")
    private String email;

    @Schema(description = "Contraseña para la cuenta de empresa", example = "Progra3_2025")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&*!()_+=\\-]).{8,20}$",
            message = "La contraseña debe tener entre 8 y 20 caracteres, incluyendo una mayúscula, una minúscula, un número y un carácter especial.")
    private String password;

    @Schema(description = "Ubicación principal de la empresa", example = "Buenos Aires, Argentina")
    private String location;

    @Schema(description = "Número de identificación tributaria (Tax ID)", example = "30567890123")
    @Pattern(regexp = "^[0-9]{11}$", message = "El TAX ID debe tener exactamente 11 dígitos numéricos")
    private String taxId;

    @Schema(description = "Teléfono de contacto de la empresa", example = "+54 911 1234 5678")
    @Pattern(regexp = "^\\+?[1-9]\\d{7,14}$",
            message = "El número de teléfono debe estar en formato internacional, comenzando con '+' y contener entre 8 y 15 dígitos")
    private String phone;

    @Schema(description = "Estado activo o inactivo de la empresa", example = "true")
    private Boolean active;

    @Schema(description = "Descripción general de la empresa", example = "Empresa dedicada al turismo receptivo internacional.")
    private String description;

    @Schema(description = "Lista de actividades resumidas asociadas a la empresa")
    private List<ActivityResumeDTO> activities;
}

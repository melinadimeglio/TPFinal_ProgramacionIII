package com.example.demo.DTOs.Company.Request;

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

public class CompanyCreateDTO {

    @Schema(description = "Nombre de usuario de la empresa", example = "turismoGlobal")
    @NotBlank(message = "El nombre de usuario no debe estar vacio.")
    @Size(min = 5, max = 20, message = "El username debe tener entre 5 y 20 caracteres.")
    private String username;

    @Schema(description = "Email de contacto de la empresa", example = "info@turismoglobal.com")
    @Email(message = "El email debe tener un formato valido.")
    @NotBlank(message = "El email no debe estar vacio.")
    private String email;

    @Schema(description = "Contraseña para la cuenta de empresa", example = "Progra3_2025")
    @NotBlank(message = "La contraseña no debe estar vacia")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&*!()_+=\\-]).{8,20}$",
            message = "La contraseña debe tener entre 8 y 20 caracteres, incluyendo una mayúscula, una minúscula, un número y un carácter especial.")
    private String password;

    @Schema(description = "Número de identificación tributaria (Tax ID)", example = "30567890123")
    @NotBlank(message = "El TAX ID no debe estar vacio.")
    @Pattern(regexp = "^[0-9]{11}$", message = "El TAX ID debe tener exactamente 11 dígitos numéricos")
    private String taxId;

    @Schema(description = "Ubicación principal de la empresa", example = "Buenos Aires, Argentina")
    @NotBlank(message = "La ubicacion no debe estar vacia.")
    private String location;

    @Schema(description = "Teléfono de contacto de la empresa", example = "+54 911 1234 5678")
    @NotBlank(message = "El telefono no debe estar vacio.")
    @Pattern(regexp = "^\\+?[1-9]\\d{7,14}$",
            message = "El número de teléfono debe estar en formato internacional, comenzando con '+' y contener entre 8 y 15 dígitos")
    private String phone;

    @Schema(description = "Descripción general de la empresa", example = "Empresa dedicada al turismo receptivo internacional.")
    @NotBlank(message = "La descripcion no puede estar vacia")
    private String description;
}

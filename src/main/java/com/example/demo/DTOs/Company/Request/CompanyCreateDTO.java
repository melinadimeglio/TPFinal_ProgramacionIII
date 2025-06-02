package com.example.demo.DTOs.Company.Request;

import com.example.demo.DTOs.Activity.Response.ActivityResumeDTO;
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

    @NotBlank(message = "El nombre de usuario no debe estar vacio.")
    private String username;

    @Email(message = "El email debe tener un formato valido.")
    @NotBlank(message = "El email no debe estar vacio.")
    private String email;

    @NotBlank(message = "La contraseña no debe estar vacia")
    private String password;

    @NotBlank(message = "El TAX ID no debe estar vacio.")
    private String taxId;

    @NotBlank(message = "La ubicacion no debe estar vacia.")
    private String location;

    @NotBlank(message = "El telefono no debe estar vacio.")
    private String phone;

    @NotBlank(message = "La descripcion no puede estar vacia")
    private String description;

    private List<ActivityResumeDTO> activities;
}

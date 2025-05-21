package com.example.demo.DTOs.Company;

import com.example.demo.DTOs.Activity.ActivityResumeDTO;
import jakarta.validation.constraints.Email;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class CompanyUpdateDTO {

    private String username;

    @Email(message = "El email debe tener un formato valido.")
    private String email;
    private String password;  // opcional para cambio de contraseña
    private String taxId;
    private String location;
    private String phone;
    private Boolean active;
    private List<ActivityResumeDTO> activities;

}
